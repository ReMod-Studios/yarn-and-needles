package com.remodstudios.yaneedles.test;

import net.fabricmc.api.ModInitializer;

public class Test implements ModInitializer {
    @Override
    public void onInitialize() {
        TestBlocksRegistry.init();
        TestItemsRegistry.init();
    }
}
