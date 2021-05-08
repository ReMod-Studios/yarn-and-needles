package com.remodstudios.yarnandneedles.datagen.generators.item;

import com.remodstudios.yarnandneedles.datagen.ResourceGenerator;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;

public class FlatBlockItemGenerator implements ResourceGenerator {
    private static final Identifier PARENT_ID = new Identifier("item/generated");

    @Override
    public void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addItemModel(id, model -> model
                .parent(PARENT_ID)
                .texture("layer0", IdUtils.wrapPath("block/", id)));
    }

    @Override
    public void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack, Identifier id) {

    }
}
