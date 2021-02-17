package com.remodstudios.yaneedles.test;

import com.swordglowsblue.artifice.api.Artifice;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class Test implements ModInitializer {
    public static final String MOD_ID = "yaneedles_test";

    @Override
    public void onInitialize() {
        Artifice.registerDataPack(new Identifier(MOD_ID, "artifice_rp"), pack -> {
            TestBlocksRegistry.init(pack);
            TestItemsRegistry.init(pack);

            try {
                pack.dumpResources("artifice.dbg", "data");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });
    }
}
