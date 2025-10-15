package com.jono.core.config;

import com.jono.core.service.constant.ConstantProxy;
import com.jono.core.service.constant.Id;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class EntityConstantAutoRegistrar implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter((metadataReader, _) -> {
            final var isInterface = metadataReader.getClassMetadata().isInterface();
            final var nameMatches = metadataReader.getClassMetadata().getClassName().endsWith("Constant");
            return isInterface && nameMatches;
        });

        for (final var candidate : scanner.findCandidateComponents(Id.class.getPackage().getName())) {
            try {
                @SuppressWarnings("unchecked") final var iface = (Class<Object>) Class.forName(candidate.getBeanClassName());
                final var beanDef = BeanDefinitionBuilder
                        .genericBeanDefinition(iface, () -> ConstantProxy.create(iface))
                        .getBeanDefinition();
                registry.registerBeanDefinition(iface.getSimpleName(), beanDef);
            } catch (final Exception e) {
                throw new RuntimeException("Failed to register dynamic constant interface", e);
            }
        }
    }

}
