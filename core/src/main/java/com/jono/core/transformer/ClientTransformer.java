package com.jono.core.transformer;

import com.jono.core.IdDescription;
import com.jono.core.dto.ClientDto;
import com.jono.core.entity.ClientEntity;
import com.jono.core.entity.ClientStatusEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
public class ClientTransformer implements Transformer<ClientEntity, ClientDto> {

    private final EntityManager em;

    protected ClientTransformer(final EntityManager em) {
        this.em = em;
    }

    @Override
    public ClientDto toDto(final ClientEntity clientEntity) {
        return new ClientDto(clientEntity.accountNo(), clientEntity.name(), IdDescription.from(clientEntity.status()))
                .id(clientEntity.id());
    }

    @Override
    public Collection<? extends ClientDto> toDtos(final Collection<? extends ClientEntity> entities) {
        Objects.requireNonNull(entities);

        return entities.stream()
                       .map(this::toDto)
                       .toList();
    }

    @Override
    public ClientEntity toEntity(final ClientDto dto) {
        Objects.requireNonNull(dto);

        final var status = em.find(ClientStatusEntity.class, dto.status().id());

        final var result = new ClientEntity(dto.accountNo(), dto.name(), status);
        result.status(status);

        return result;
    }

    @Override
    public Collection<? extends ClientEntity> toEntities(final Collection<? extends ClientDto> dtos) {
        Objects.requireNonNull(dtos);

        return dtos.stream()
                   .map(this::toEntity)
                   .toList();
    }

}
