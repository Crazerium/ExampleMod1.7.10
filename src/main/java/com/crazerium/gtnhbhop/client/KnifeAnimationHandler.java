package com.crazerium.gtnhbhop.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import com.crazerium.gtnhbhop.GTNHBhop;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Click-only butterfly flourish trigger.
 *
 * Stage 4.1.2 is authored to the Shorts as a sequence of key poses, so the
 * handler only owns timing and input. Rendering stays entirely in KnifeRenderer.
 */
public final class KnifeAnimationHandler {

    // Stage 4.1.2 keeps the accepted 1.8 s cadence; only the authored trajectory
    // changes in KnifeRenderer.
    private static final long TRICK_DURATION_MS = 1800L;

    private static boolean holdingKnife;
    private static boolean previousLeftMouseDown;
    private static long trickStartedAt = -1L;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        if (player == null) {
            reset();
            return;
        }

        ItemStack held = player.getHeldItem();
        boolean holdingNow = held != null && held.getItem() == GTNHBhop.bhopKnife;
        boolean leftMouseDown = mc.currentScreen == null && Mouse.isButtonDown(0);

        if (holdingNow && leftMouseDown && !previousLeftMouseDown && !isTrickRunning()) {
            trickStartedAt = Minecraft.getSystemTime();
        }

        holdingKnife = holdingNow;
        previousLeftMouseDown = leftMouseDown;

        if (!holdingKnife) {
            trickStartedAt = -1L;
        }
    }

    public static float getTrickProgress() {
        if (!holdingKnife || trickStartedAt < 0L) {
            return 0.0F;
        }

        long elapsed = Minecraft.getSystemTime() - trickStartedAt;
        if (elapsed >= TRICK_DURATION_MS) {
            trickStartedAt = -1L;
            return 0.0F;
        }

        return clamp01(elapsed / (float) TRICK_DURATION_MS);
    }

    public static boolean isTrickRunning() {
        return holdingKnife && trickStartedAt >= 0L
            && Minecraft.getSystemTime() - trickStartedAt < TRICK_DURATION_MS;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static void reset() {
        holdingKnife = false;
        previousLeftMouseDown = false;
        trickStartedAt = -1L;
    }
}
