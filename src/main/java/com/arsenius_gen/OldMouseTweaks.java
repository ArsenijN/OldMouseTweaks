package com.arsenius_gen;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OldMouseTweaks implements ModInitializer {
    public static final String MOD_ID = "old-mouse-tweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[OldMouseTweaks] Initialized.");
    }
}
