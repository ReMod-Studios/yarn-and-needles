package com.remodstudios.yaneedles.datagen.generators.item;

import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.IdUtils;
import net.java.games.input.Component;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockItemGenerator implements ResourceGenerator {

    private final Identifier parentModelId;

    public BlockItemGenerator(Map<String, String> arguments) {
        this.parentModelId = new Identifier(arguments.get("parentModelId"));
    }

    // use block id
    public BlockItemGenerator() {
        this.parentModelId = null;
    }

    protected void generateItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {
        pack.addItemModel(id, model -> model
            .parent(IdUtils.wrapPath("block/", parentModelId == null ? id : parentModelId))
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
