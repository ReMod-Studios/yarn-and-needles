package com.remodstudios.yaneedles.test;

import com.swordglowsblue.artifice.api.Artifice;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class TestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Artifice.registerAssetPack(new Identifier(Test.MOD_ID, "artifice_rp"), pack -> {
            TestBlocksRegistry.clientInit(pack);
            TestItemsRegistry.clientInit(pack);

            try {
                pack.dumpResources("artifice.dbg", "assets");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });
    }
}
