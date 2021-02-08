package com.remodstudios.yarnandneedles.items;

import com.remodstudios.yarnandneedles.datagen.ResourceGenerator;
import com.remodstudios.yarnandneedles.datagen.ResourceGenerators;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class ItemRegistry {
    public final Map<Identifier, Pair<Item, ResourceGenerator>> ITEMS = new Object2ObjectLinkedOpenHashMap<>();
    protected final String namespace;

    public ItemRegistry(String namespace) {
        this.namespace = namespace;
    }

    public void init() {
        register();
    }

    public void register() {
        ITEMS.forEach((id, pair) -> Registry.register(Registry.ITEM, id, pair.getLeft()));
    }

    //region Datagen methods
    public void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack) {
        ITEMS.forEach((id, pair) -> pair.getRight().generateAssets(pack, id));
    }

    public void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack) {
        ITEMS.forEach((id, pair) -> pair.getRight().generateData(pack, id));
    }
    //endregion
    //region Registry methods
    protected Item add(String name) {
        return add(name, new Item(getDefaultSettings()));
    }

    protected <I extends Item> I add(String name, I item) {
        return add(name, ResourceGenerators.SIMPLE_ITEM, item);
    }

    protected <I extends Item> I add(String name, ResourceGenerator generator, I item) {
        ITEMS.put(id(name), new Pair<>(item, generator));
        return item;
    }

    protected BlockItem addBlockItem(String name, Block block) {
        return addBlockItem(name, ResourceGenerators.BLOCK_ITEM, block);
    }

    protected BlockItem addBlockItem(String name, ResourceGenerator generator, Block block) {
        return addBlockItem(name, ResourceGenerators.BLOCK_ITEM, block, getDefaultSettings());
    }

    protected BlockItem addBlockItem(String name, ResourceGenerator generator, Block block, FabricItemSettings settings) {
        BlockItem item = new BlockItem(block, settings);
        ITEMS.put(id(name), new Pair<>(item, generator));
        return item;
    }
    //endregion

    protected final Identifier id(String name) {
        return new Identifier(namespace, name);
    }

    protected FabricItemSettings getDefaultSettings() {
        return new FabricItemSettings();
    }

    public static class RegistrySettings {
        public final ResourceGenerator resourceGenerator;

        protected RegistrySettings(ResourceGenerator resourceGenerator) {
            this.resourceGenerator = resourceGenerator;
        }

        public static RegistrySettings of() {
            return new RegistrySettings(ResourceGenerators.SIMPLE_ITEM);
        }
        public static RegistrySettings of(ResourceGenerator resourceGenerator) {
            return new RegistrySettings(resourceGenerator);
        }
    }
}
