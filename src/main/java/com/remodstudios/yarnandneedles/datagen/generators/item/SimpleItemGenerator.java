package com.remodstudios.yarnandneedles.datagen.generators.item;

import com.remodstudios.yarnandneedles.datagen.ResourceGenerator;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;

public class SimpleItemGenerator implements ResourceGenerator {
    protected static final Identifier DEFAULT_PARENT_ID = new Identifier("item/generated");

    protected final Identifier parentModelId;

    public SimpleItemGenerator(Identifier parentModelId) {
        this.parentModelId = parentModelId;
    }
    public SimpleItemGenerator() {
        this(DEFAULT_PARENT_ID);
    }

    protected void generateItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addItemModel(id, model -> model
            .parent(parentModelId)
            .texture("layer0", IdUtils.wrapPath("item/", id))
        );
    }

    @Override
    public void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        this.generateItemModel(pack, id);
    }

    @Override
    public void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack, Identifier id) {

    }
}
