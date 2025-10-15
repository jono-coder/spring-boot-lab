package com.jono.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(@JsonSubTypes.Type(value = IdDescription.class, name = "IdDescription"))
public interface IdDesc {
    Integer id();

    String description();
}
