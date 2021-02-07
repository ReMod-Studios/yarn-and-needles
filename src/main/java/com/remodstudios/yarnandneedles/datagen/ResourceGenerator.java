package com.remodstudios.yarnandneedles.datagen;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import net.minecraft.util.Identifier;

public interface ResourceGenerator {
    void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id);
    void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack, Identifier id);
}
