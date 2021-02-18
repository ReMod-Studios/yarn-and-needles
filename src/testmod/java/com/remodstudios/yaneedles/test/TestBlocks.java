package com.remodstudios.yaneedles.test;

import com.remodstudios.yaneedles.annotations.ResGen;
import com.remodstudios.yaneedles.annotations.block.BlockRegistry;
import com.remodstudios.yaneedles.annotations.CustomId;
import com.remodstudios.yaneedles.datagen.generators.block.BlockWithEntityGenerator;
import com.remodstudios.yaneedles.datagen.generators.block.ButtonBlockGenerator;
import com.remodstudios.yaneedles.datagen.generators.block.LogBlockGenerator;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@BlockRegistry(namespace = Test.MOD_ID)
public class TestBlocks {
    // default case: SimpleBlockGenerator
    public static final Block YAY = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));

    @ResGen(value = ButtonBlockGenerator.class, args = "baseBlockId=haha:testy_test_this_is_not_a_valid_block")
    public static final Block YAYER = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));

    @CustomId("yayn-t")
    // this should not create an additional resource generator; instead it should reuse the previous one
    @ResGen(value = ButtonBlockGenerator.class, args = "baseBlockId=haha:testy_test_this_is_not_a_valid_block")
    public static final Block YAYN_T = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));

    @CustomId("yayn-ter")
    // test argument parsing
    @ResGen(
        value = ButtonBlockGenerator.class,
        args = "baseBlockId=haha:testy_test_this_is_not_a_valid_block,haha=haha_it_does_not_recognize_this_option,lol=1"
    )
    public static final Block YAYN_TER = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
}
