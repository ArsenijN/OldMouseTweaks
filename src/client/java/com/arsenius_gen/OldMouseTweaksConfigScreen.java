package com.arsenius_gen;

import com.arsenius_gen.config.OldMouseTweaksConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class OldMouseTweaksConfigScreen extends Screen {

    private final Screen parent;

    public OldMouseTweaksConfigScreen(Screen parent) {
        super(Component.literal("Old Mouse Tweaks Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Cycle button for MoveAllMode
        addRenderableWidget(
            CycleButton.<OldMouseTweaksConfig.MoveAllMode>builder(mode ->
                    Component.literal(switch (mode) {
                        case CONTEXT_ONLY -> "Move All: Context Only";
                        case COMBINED     -> "Move All: Combined";
                    }))
                .withValues(OldMouseTweaksConfig.MoveAllMode.values())
                .withInitialValue(OldMouseTweaksConfig.get().moveAllMode)
                .create(width / 2 - 150, height / 2 - 20, 300, 20,
                    Component.literal("Empty Slot Behaviour"),
                    (btn, value) -> OldMouseTweaksConfig.get().moveAllMode = value)
        );

        // Done button
        addRenderableWidget(
            Button.builder(Component.literal("Done"), btn -> {
                OldMouseTweaksConfig.save();
                minecraft.setScreen(parent);
            }).bounds(width / 2 - 75, height / 2 + 10, 150, 20).build()
        );
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics g, int mx, int my, float delta) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        super.render(g, mx, my, delta);
    }

    @Override
    public void onClose() {
        OldMouseTweaksConfig.save();
        minecraft.setScreen(parent);
    }
}