package com.remodstudios.yarnandneedles.datagen.generators.block;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;

public class PlantBlockResourceGenerator extends SimpleBlockGenerator {
    @Override
    protected void generateModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addBlockModel(id, model -> model
                .parent(new Identifier("block/cross"))
                .texture("cross", IdUtils.wrapPath("block/", id))
        );
    }
}
