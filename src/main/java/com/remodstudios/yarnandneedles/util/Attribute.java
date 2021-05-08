package com.remodstudios.yarnandneedles.util;

import org.jetbrains.annotations.NotNull;

public final class Attribute<T> {
    public static <T> @NotNull Attribute<T> of(@NotNull Class<? extends T> type, @NotNull String name) {
        return new Attribute<>(type, name);
    }

    private final @NotNull Class<? extends T> type;
    private final @NotNull String name;

    private Attribute(@NotNull Class<? extends T> type, @NotNull String name) {
        this.type = type;
        this.name = name;
    }

    public Class<? extends T> type() {
        return type;
    }

    public String name() {
        return name;
    }
}
