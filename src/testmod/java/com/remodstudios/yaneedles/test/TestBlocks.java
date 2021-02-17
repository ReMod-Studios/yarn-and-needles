package com.remodstudios.yaneedles.test;

import com.remodstudios.yaneedles.annotations.block.BlockRegistry;
import com.remodstudios.yaneedles.annotations.CustomId;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@BlockRegistry(namespace = "yaneedles-test")
public class TestBlocks {
    public static Block YAY = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
    public static Block YAYER = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
    @CustomId("yayn-t")
    public static Block YAYN_T = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
}
