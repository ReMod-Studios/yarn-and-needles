package com.remodstudios.yarnandneedles.blocks.util;

import com.remodstudios.yarnandneedles.mixin.signs.SignTypeAccessor;
import net.minecraft.util.SignType;

import java.util.Arrays;
import java.util.Collection;

public class SignTypeRegistry {
    public static void register(SignType... type) {
        register(Arrays.asList(type));
    }
    public static void register(Collection<SignType> type) {
        SignTypeAccessor.getValues().addAll(type);
    }
}
