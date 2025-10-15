package com.jono.core.service.constant;

import com.jono.core.config.CaffeineConfig;
import com.jono.core.config.SpringContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("ClassWithOnlyPrivateConstructors")
public class ConstantProxy implements InvocationHandler {

    private static volatile Cache cache;

    public static Object create(final Class<?> iface) {
        return Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class[]{iface},
                new ConstantProxy()
        );
    }

    private static Class<?> extractEntityType(final Method method) {
        final var returnType = method.getGenericReturnType();
        if (returnType instanceof final ParameterizedType pt) {
            final var typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length == 1 && typeArgs[0] instanceof final Class<?> clazz) {
                return clazz;
            }
        }
        // If not parameterized, assume the method return type itself
        return method.getReturnType();
    }

    private static Object lookupEntity(final Class<?> entityType, final int id) {
        return getEntityManager().find(entityType, id);
    }

    private static Object lookupEntities(final Class<?> entityType, final List<Integer> ids) {
        final var session = getEntityManager().unwrap(Session.class);

        return session.byMultipleIds(entityType)
                      .withBatchSize(50)
                      .multiLoad(ids);
    }

    private static EntityManager getEntityManager() {
        return SpringContext.getBean(EntityManager.class);
    }

    private static Cache getCache() {
        if (cache == null) {
            synchronized (ConstantProxy.class) {
                if (cache == null) {
                    cache = SpringContext.getBean(CacheManager.class).getCache(CaffeineConfig.ENTITY_CONSTANTS_CACHE_NAME);
                }
            }
        }
        return cache;
    }

    private ConstantProxy() {
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final var declaringClass = method.getDeclaringClass();

        if (declaringClass == Object.class) {
            return method.invoke(this, args);
        }

        if (method.isDefault()) {
            final var lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup());
            return lookup.unreflectSpecial(method, declaringClass)
                         .bindTo(proxy)
                         .invokeWithArguments(args);
        }

        final var key = declaringClass.getName() + '.' + method.getName();

        return getCache().get(key, () -> {
            final var ids = method.getAnnotationsByType(Id.class);
            if (ids.length == 0) {
                throw new IllegalStateException("Method " + method + " missing @Id annotations");
            }

            final var entityType = extractEntityType(method);

            if (Collection.class.isAssignableFrom(method.getReturnType())) {
                return lookupEntities(entityType, Arrays.stream(ids).map(Id::value).toList());
            } else {
                // Just return the first ID
                return lookupEntity(entityType, ids[0].value());
            }
        });
    }

}
