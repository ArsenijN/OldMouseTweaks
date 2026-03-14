package com.arsenius_gen.mixin.client;

import com.arsenius_gen.config.OldMouseTweaksConfig;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class CtrlClickMixin<T extends AbstractContainerMenu> {

    @Shadow protected Slot hoveredSlot;
    @Shadow protected T menu;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void oldMouseTweaks_onMouseClicked(double mouseX, double mouseY,
                                               int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        if (!isCtrlHeld()) return;
        if (hoveredSlot == null) return; // remove the hasItem() check here

        Item targetItem = hoveredSlot.getItem().getItem();

        List<Slot> allSlots = menu.slots;
        int total = allSlots.size();

        int playerSideStart;
        int hotbarStart;

        boolean isInventoryScreen = (menu instanceof InventoryMenu);
        if (isInventoryScreen) {
            playerSideStart = 9;  // after armor(4) + offhand(1) + crafting(4)
            hotbarStart = 36;
        } else {
            playerSideStart = Math.max(0, total - 36);
            hotbarStart = playerSideStart + 27;
        }

        boolean clickedInContainer = (hoveredSlot.index < playerSideStart);
        boolean clickedInHotbar    = (hoveredSlot.index >= hotbarStart);

        // Debug: print key values so we can verify detection in the log
        com.arsenius_gen.OldMouseTweaks.LOGGER.info(
            "[OMT] click slot={} total={} playerSideStart={} hotbarStart={} " +
            "inContainer={} inHotbar={} isInvScreen={} menuClass={}",
            hoveredSlot.index, total, playerSideStart, hotbarStart,
            clickedInContainer, clickedInHotbar, isInventoryScreen,
            menu.getClass().getName()
        );

        boolean moveAll = !hoveredSlot.hasItem(); // clicked empty slot = move everything

        List<Integer> slotIndices = new ArrayList<>();
        for (Slot slot : allSlots) {
            if (!slot.hasItem()) continue;

            boolean slotInContainer = (slot.index < playerSideStart);
            boolean slotInHotbar    = (slot.index >= hotbarStart);
            boolean slotInInvRows   = !slotInContainer && !slotInHotbar;

            boolean include;
            if (moveAll) {
                if (clickedInContainer) {
                    include = slotInContainer;
                } else {
                    boolean combined = OldMouseTweaksConfig.get().moveAllMode
                                        == OldMouseTweaksConfig.MoveAllMode.COMBINED;
                    if (combined) {
                        include = !slotInContainer;
                    } else {
                        // CONTEXT_ONLY: respect zone boundaries on all screens
                        if (clickedInHotbar) {
                            include = slotInHotbar;
                        } else {
                            include = slotInInvRows;
                        }
                    }
                }
            } else {
                // per-item logic (unchanged)
                if (clickedInContainer) {
                    include = slotInContainer;
                } else if (isInventoryScreen) {
                    if (clickedInHotbar) {
                        include = slotInHotbar;
                    } else {
                        include = slotInInvRows;
                    }
                } else {
                    include = !slotInContainer;
                }
            }

            if (include) slotIndices.add(slot.index);
        }

        com.arsenius_gen.OldMouseTweaks.LOGGER.info("[OMT] will move slots: {}", slotIndices);

        if (slotIndices.isEmpty()) return;

        AbstractContainerScreenInvoker invoker = (AbstractContainerScreenInvoker)(Object) this;

        for (int idx : slotIndices) {
            Slot slot = allSlots.get(idx);
            if (!slot.hasItem()) continue;
            if (!moveAll && slot.getItem().getItem() != targetItem) continue; // skip filter for moveAll
            invoker.invokeSlotClicked(slot, slot.index, 0, ClickType.QUICK_MOVE);
        }

        cir.setReturnValue(true);
    }

    private static boolean isCtrlHeld() {
        long window = net.minecraft.client.Minecraft.getInstance().getWindow().getWindow();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL)  == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }
}
