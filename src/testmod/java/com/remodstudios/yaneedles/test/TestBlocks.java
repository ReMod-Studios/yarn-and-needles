package com.remodstudios.yaneedles.test;

import com.remodstudios.yaneedles.annotations.BlockRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@BlockRegistry(namespace = "yaneedles-test")
public class TestBlocks {
    public static final Block YAY = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
    public static final Block YAYER = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
    @BlockRegistry.Entry("yayn-t")
    public static final Block YAYN_T = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
}
