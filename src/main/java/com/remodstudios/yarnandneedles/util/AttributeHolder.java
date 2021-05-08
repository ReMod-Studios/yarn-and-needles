package com.remodstudios.yarnandneedles.util;

import org.jetbrains.annotations.NotNull;

public interface AttributeHolder {
    <T> T getAttribute(@NotNull Attribute<T> attr, T defaultValue);
    <T> void putAttribute(@NotNull Attribute<T> attr, T value);
}
