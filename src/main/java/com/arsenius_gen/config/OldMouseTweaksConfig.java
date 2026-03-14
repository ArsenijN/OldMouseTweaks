package com.arsenius_gen.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class OldMouseTweaksConfig {

    public enum MoveAllMode {
        CONTEXT_ONLY,
        COMBINED
    }

    public MoveAllMode moveAllMode = MoveAllMode.CONTEXT_ONLY;

    // --- static singleton ---
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("old-mouse-tweaks.json");

    private static OldMouseTweaksConfig instance = new OldMouseTweaksConfig();

    public static OldMouseTweaksConfig get() { return instance; }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
                instance = GSON.fromJson(r, OldMouseTweaksConfig.class);
            } catch (IOException e) {
                instance = new OldMouseTweaksConfig();
            }
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(instance, w);
        } catch (IOException e) {
            // ignore
        }
    }
}