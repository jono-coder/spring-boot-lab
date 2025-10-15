package com.jono.core.transformer;

import java.util.Collection;

public interface Transformer<Entity, Dto> {

    Dto toDto(Entity entity);

    Collection<? extends Dto> toDtos(Collection<? extends Entity> entities);

    Entity toEntity(Dto dto);

    Collection<? extends Entity> toEntities(Collection<? extends Dto> dtos);

}
