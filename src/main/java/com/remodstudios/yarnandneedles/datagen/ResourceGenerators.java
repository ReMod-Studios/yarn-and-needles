package com.remodstudios.yarnandneedles.datagen;

import com.remodstudios.yarnandneedles.datagen.generators.block.DoorBlockGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.block.LogBlockGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.block.SimpleBlockGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.block.TrapdoorBlockGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.item.BlockItemGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.item.SimpleItemGenerator;

public final class ResourceGenerators {

    public static final ResourceGenerator SIMPLE_BLOCK = new SimpleBlockGenerator();
    public static final ResourceGenerator SIMPLE_ITEM = new SimpleItemGenerator();
    public static final ResourceGenerator BLOCK_ITEM = new BlockItemGenerator();
    public static final ResourceGenerator LOG_BLOCK = new LogBlockGenerator();
    public static final ResourceGenerator DOOR_BLOCK = new DoorBlockGenerator();
    public static final ResourceGenerator TRAPDOOR_BLOCK = new TrapdoorBlockGenerator();

    private ResourceGenerators() {}
}
