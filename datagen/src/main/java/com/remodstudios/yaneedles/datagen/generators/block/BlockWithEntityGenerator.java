package com.remodstudios.yaneedles.datagen.generators.block;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BlockWithEntityGenerator extends SimpleBlockGenerator {

    @NotNull protected final Identifier particleTextureId;

    public BlockWithEntityGenerator(Map<String, String> arguments) {
        this(new Identifier(arguments.get("particleTextureId")));
    }
    public BlockWithEntityGenerator(@NotNull Identifier particleTextureId) {
        this.particleTextureId = particleTextureId;
    }

    @Override
    protected void generateModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addBlockModel(id, model -> model.texture("particle", particleTextureId));
    }
}
