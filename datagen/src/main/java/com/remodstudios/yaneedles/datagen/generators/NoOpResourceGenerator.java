package com.remodstudios.yaneedles.datagen.generators;

import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import net.minecraft.util.Identifier;

/**
 * Does what it says on the tin: precisely nothing.
 */
public class NoOpResourceGenerator implements ResourceGenerator {

    @Override
    public void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id) {}

    @Override
    public void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack, Identifier id) {}

}