package com.crazerium.gtnhbhop.client;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.crazerium.gtnhbhop.Config;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class SpeedHud {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!Config.showSpeedHud || !BunnyhopHandler.isActive()
            || event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player == null || mc.gameSettings.showDebugInfo) {
            return;
        }

        double horizontalSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ) * 20.0D;
        String text = String.format(Locale.ROOT, "%.2f m/s", horizontalSpeed);

        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        FontRenderer font = mc.fontRenderer;
        int x = (resolution.getScaledWidth() - font.getStringWidth(text)) / 2;
        int y = resolution.getScaledHeight() - Config.hudYOffset;

        font.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }
}
