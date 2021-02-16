package com.remodstudios.yaneedles.extensions.util;

import com.remodstudios.yaneedles.extensions.mixin.signs.SignTypeAccessor;
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
