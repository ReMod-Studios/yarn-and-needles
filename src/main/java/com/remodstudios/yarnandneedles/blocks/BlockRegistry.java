package com.remodstudios.yarnandneedles.blocks;

import com.remodstudios.yarnandneedles.datagen.ResourceGenerator;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.Map;

import static com.remodstudios.yarnandneedles.datagen.ResourceGenerators.SIMPLE_BLOCK;

public class BlockRegistry {
    public final Map<Identifier, Pair<Block, RegistrySettings>> BLOCKS = new Object2ObjectLinkedOpenHashMap<>();
    protected final String namespace;

    public BlockRegistry(String namespace) {
        this.namespace = namespace;
    }

    public void init() {
        register();
    }

    public void register() {
        BLOCKS.forEach((id, pair) -> {
            Block block = pair.getLeft();
            Registry.register(Registry.BLOCK, id, block);
            RenderLayer layer = pair.getRight().renderLayer;
            if (layer != null) BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
        });
    }

    protected <B extends Block> B add(String name, B block) {
        return add(name, RegistrySettings.of(), block);
    }

    protected <B extends Block> B add(String name, RegistrySettings settings, B block) {
        BLOCKS.put(id(name), new Pair<>(block, settings)); return block;
    }

    public void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack) {
        BLOCKS.forEach((id, pair) -> pair.getRight().resourceGenerator.generateAssets(pack, id));
    }

    public void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack) {
        BLOCKS.forEach((id, pair) -> pair.getRight().resourceGenerator.generateData(pack, id));
    }

    protected final Identifier id(String name) {
        return new Identifier(namespace, name);
    }

    public Identifier idOf(Block block) {
        return BLOCKS.entrySet().stream()
                .filter(entry -> entry.getValue().getLeft().equals(block))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Block not found in registry!"));
    }

    public static class RegistrySettings {
        public final ResourceGenerator resourceGenerator;
        public final RenderLayer renderLayer;

        protected RegistrySettings(ResourceGenerator resourceGenerator, RenderLayer renderLayer) {
            this.resourceGenerator = resourceGenerator;
            this.renderLayer = renderLayer;
        }

        public static RegistrySettings of() {
            return new RegistrySettings(SIMPLE_BLOCK, null);
        }
        public static RegistrySettings of(ResourceGenerator resourceGenerator) {
            return new RegistrySettings(resourceGenerator, null);
        }
        public static RegistrySettings of(ResourceGenerator resourceGenerator, RenderLayer renderLayer) {
            return new RegistrySettings(resourceGenerator, renderLayer);
        }
    }
}
