package com.remodstudios.yaneedles.registry.impl;

import io.activej.codegen.ClassBuilder;
import io.activej.codegen.DefiningClassLoader;
import io.activej.codegen.expression.Expression;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static io.activej.codegen.expression.Expressions.*;

public class EntrypointHandler implements PreLaunchEntrypoint {

    private static final Logger LOGGER = LogManager.getLogger("YaNeedles|Registry");

    @Override
    public void onPreLaunch() {
        LOGGER.info("Entrypoint Handler - processing registry entrypoints");
        FabricLoader loader = FabricLoader.getInstance();

        DefiningClassLoader classLoader = DefiningClassLoader.create(this.getClass().getClassLoader());

        Class<?> wtf = Sample.class;

    }
}
