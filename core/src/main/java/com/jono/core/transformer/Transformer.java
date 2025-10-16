package com.jono.core.transformer;

import java.util.Collection;

public interface Transformer<Entity, Dto> {

    Dto toDto(Entity entity);

    Collection<Dto> toDtos(Collection<Entity> entities);

    Entity toEntity(Dto dto);

    Collection<Entity> toEntities(Collection<Dto> dtos);

}
