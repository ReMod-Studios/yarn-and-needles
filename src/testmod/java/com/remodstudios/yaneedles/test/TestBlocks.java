package com.remodstudios.yaneedles.test;

import com.remodstudios.yaneedles.annotations.ResGen;
import com.remodstudios.yaneedles.annotations.block.BlockRegistry;
import com.remodstudios.yaneedles.annotations.CustomId;
import com.remodstudios.yaneedles.datagen.generators.block.BlockWithEntityGenerator;
import com.remodstudios.yaneedles.datagen.generators.block.LogBlockGenerator;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@BlockRegistry(namespace = Test.MOD_ID)
public class TestBlocks {
    public static final Block YAY = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));

    @ResGen(LogBlockGenerator.class)
    public static final Block YAYER = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));

    @CustomId("yayn-t")
    @ResGen(LogBlockGenerator.class)
    public static final Block YAYN_T = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
}
