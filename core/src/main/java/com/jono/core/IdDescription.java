package com.jono.core;

import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public record IdDescription(Integer id, String description) implements IdDesc {

    @Nullable
    public static IdDesc from(final IdDesc idDesc) {
        if (idDesc == null) {
            return null;
        }
        return new IdDescription(idDesc.id(), idDesc.description());
    }

    public static List<? extends IdDesc> from(final Collection<? extends IdDesc> idDescs) {
        if (idDescs == null) {
            return List.of();
        }
        return idDescs.stream()
                      .map(idDesc -> new IdDescription(idDesc.id(), idDesc.description()))
                      .toList();
    }

}
