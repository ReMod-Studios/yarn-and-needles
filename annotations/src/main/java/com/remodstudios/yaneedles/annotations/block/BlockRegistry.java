package com.remodstudios.yaneedles.annotations.block;

import com.remodstudios.yaneedles.annotations.ResGen;
import com.remodstudios.yaneedles.datagen.generators.block.SimpleBlockGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface BlockRegistry {
    String namespace();

    /**
     * Specifies the output class name.
     * <p>When set to "" (the default) the processor will use the default class name,
     * which is the input class name appended with {@code Registry}.
     * (e.g. {@code MyBlocks -> MyBlocksRegistry})
     */
    String outputClassName() default "";

    /**
     * If set to true, then the annotation processor will only process
     * fields marked with {@link BlockRegistry.Entry}.
     * <p>Defaults to false.
     */
    boolean onlyCheckMarkedEntries() default false;

    /**
     * Sets the default resource generator across the registry.
     * <p>Defaults to a {@link SimpleBlockGenerator} with no supplied arguments.
     */
    ResGen defaultResourceGenerator() default @ResGen(SimpleBlockGenerator.class);

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Entry { }
}
