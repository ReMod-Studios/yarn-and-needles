package com.remodstudios.yarnandneedles.datagen;

import com.remodstudios.yarnandneedles.datagen.generators.NoOpResourceGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.block.*;
import com.remodstudios.yarnandneedles.datagen.generators.item.BlockItemGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.item.FlatBlockItemGenerator;
import com.remodstudios.yarnandneedles.datagen.generators.item.SimpleItemGenerator;
import net.minecraft.util.Identifier;

public final class ResourceGenerators {

    public static final ResourceGenerator NO_OP = new NoOpResourceGenerator();
    public static final ResourceGenerator SIMPLE_BLOCK = new SimpleBlockGenerator();
    public static final ResourceGenerator SIMPLE_ITEM = new SimpleItemGenerator();
    public static final ResourceGenerator HANDHELD_ITEM = new SimpleItemGenerator(new Identifier("item/handheld"));
    public static final ResourceGenerator BLOCK_ITEM = new BlockItemGenerator();
    public static final ResourceGenerator FLAT_BLOCK_ITEM = new FlatBlockItemGenerator();
    public static final ResourceGenerator LOG_BLOCK = new LogBlockGenerator();
    public static final ResourceGenerator DOOR_BLOCK = new DoorBlockGenerator();
    public static final ResourceGenerator TRAPDOOR_BLOCK = new TrapdoorBlockGenerator();
    public static final ResourceGenerator PLANT_BLOCK = new PlantBlockResourceGenerator();

    private ResourceGenerators() {}
}
