package com.remodstudios.yarnandneedles.datagen.generators.block;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockWithEntityGenerator extends AbstractParentedBlockGenerator {

    public BlockWithEntityGenerator(Identifier particleTextureId) {
        super(particleTextureId);
    }

    @Override
    protected void generateModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addBlockModel(id, model -> model.texture("particle", baseBlockId));
    }
}
