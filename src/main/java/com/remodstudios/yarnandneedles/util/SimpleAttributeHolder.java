package com.remodstudios.yarnandneedles.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SimpleAttributeHolder implements AttributeHolder {
    private final HashMap<Attribute<?>, Object> attributes;

    public SimpleAttributeHolder() {
        attributes = new HashMap<>();
    }

    @Override
    public <T> T getAttribute(@NotNull Attribute<T> attr, T defaultValue) {
        Object rawValue = attributes.get(attr);
        if (attr.type().isInstance(rawValue))
            return attr.type().cast(rawValue);
        return defaultValue;
    }

    @Override
    public <T> void putAttribute(@NotNull Attribute<T> attr, T value) {
        attributes.put(attr, value);
    }
}
