package com.remodstudios.yaneedles.datagen;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import net.minecraft.util.Identifier;

/**
 * Represent a generator of assets based on an identifier.
 * @implNote for generators that need additional parameters, please also declare a constructor that
 * takes a single {@code Map<String, String>} in order to be used in conjunction with {@code @ResGen}.
 */
public interface ResourceGenerator {
    void generateAssets(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier id);
    void generateData(ArtificeResourcePack.ServerResourcePackBuilder pack, Identifier id);
}
