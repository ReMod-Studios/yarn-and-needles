package com.remodstudios.yaneedles.test;

import com.remodstudios.yaneedles.annotations.CustomId;
import com.remodstudios.yaneedles.annotations.ResGen;
import com.remodstudios.yaneedles.annotations.item.ItemRegistry;
import com.remodstudios.yaneedles.datagen.generators.item.BlockItemGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

@ItemRegistry(namespace = Test.MOD_ID)
public class TestItems {
    @ResGen(BlockItemGenerator.class)
    public static BlockItem YAY = new BlockItem(TestBlocks.YAY, new FabricItemSettings());
    @ResGen(BlockItemGenerator.class)
    public static BlockItem YAYER = new BlockItem(TestBlocks.YAYER, new FabricItemSettings());
    @CustomId("yayn-t")
    @ResGen(BlockItemGenerator.class)
    public static BlockItem YAYN_T = new BlockItem(TestBlocks.YAYN_T, new FabricItemSettings());
    public static Item HAI = new Item(new FabricItemSettings());
}
