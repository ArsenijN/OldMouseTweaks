package com.arsenius_gen.config;

public class OldMouseTweaksConfig {
    public enum MoveAllMode {
        CONTEXT_ONLY,   // hotbar and inv rows move independently
        COMBINED        // clicking inv moves hotbar too, and vice versa
    }

    // Default value — change here until GUI is added
    public static MoveAllMode moveAllMode = MoveAllMode.CONTEXT_ONLY;
}