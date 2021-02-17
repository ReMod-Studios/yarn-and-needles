package com.remodstudios.yaneedles.datagen.generators.block;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;

public class LogBlockGenerator extends SimpleBlockGenerator {

    @Override
    protected void generateBlockStates(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        Identifier horizontalModelId = getBlockSubPath(id, "_horizontal");
        Identifier verticalModelId = getBlockSubPath(id);

        pack.addBlockState(id, state -> state
            .variant("axis=x", variant -> variant.model(horizontalModelId).rotationX(90).rotationY(90))
            .variant("axis=z", variant -> variant.model(horizontalModelId).rotationX(90))
            .variant("axis=y", variant -> variant.model(verticalModelId)));
    }

    @Override
    protected void generateModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        Identifier sideTexturePath = getBlockSubPath(id);
        Identifier endTexturePath = getBlockSubPath(id, "_top");

        pack.addBlockModel(id, model -> model
            .parent(new Identifier("block/cube_column"))
            .texture("end", endTexturePath)
            .texture("side", sideTexturePath)
        );
        pack.addBlockModel(IdUtils.wrapPath(id, "_horizontal"), model -> model
            .parent(new Identifier("block/cube_column_horizontal"))
            .texture("end", endTexturePath)
            .texture("side", sideTexturePath)
        );
    }
}
