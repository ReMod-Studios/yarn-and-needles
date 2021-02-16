package com.remodstudios.yaneedles.datagen.generators.item;

import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.minecraft.util.Identifier;

public class SimpleItemGenerator implements ResourceGenerator {

    private final Identifier parentModelId;

    public SimpleItemGenerator(Identifier parentModelId) {
        this.parentModelId = parentModelId;
    }
    public SimpleItemGenerator() {
        this(new Identifier("item/generated"));
    }

    protected void generateItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addItemModel(id, model -> model
            .parent(new Identifier("item/generated"))
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
