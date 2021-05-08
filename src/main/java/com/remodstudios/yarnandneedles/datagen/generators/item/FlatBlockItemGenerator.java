package com.remodstudios.yarnandneedles.datagen.generators.item;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;

public class FlatBlockItemGenerator extends SimpleItemGenerator {
    public FlatBlockItemGenerator(Identifier parentModelId) {
        super(parentModelId);
    }
    public FlatBlockItemGenerator() {
        super();
    }

    @Override
    protected void generateItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addItemModel(id, model -> model
                .parent(parentModelId)
                .texture("layer0", IdUtils.wrapPath("block/", id))
        );
    }
}
