package com.crazerium.gtnhbhop.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;

import com.crazerium.gtnhbhop.Config;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class BunnyhopHandler {

    public static final KeyBinding TOGGLE_KEY = new KeyBinding(
        "key.gtnhbhop.toggle",
        Keyboard.KEY_K,
        "key.categories.gtnhbhop");

    private static boolean active;

    private double motionXAtTickStart;
    private double motionZAtTickStart;
    private boolean onGroundAtTickStart;
    private boolean capturedTickStart;

    private double recentAirMotionX;
    private double recentAirMotionZ;
    private int ticksSinceAirMomentum = 1000;

    public static void initializeFromConfig() {
        active = Config.enabledByDefault;
    }

    public static boolean isActive() {
        return Config.enabled && active;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen != null || !TOGGLE_KEY.isPressed()) {
            return;
        }

        if (!Config.enabled) {
            mc.thePlayer
                .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Bunnyhop is disabled in gtnhbhop.cfg"));
            return;
        }

        active = !active;
        clearMomentumState();

        mc.thePlayer.addChatMessage(
            new ChatComponentText(
                (active ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + "Bunnyhop: " + (active ? "ON" : "OFF")));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player == null || event.player != player) {
            return;
        }

        if (!isActive()) {
            capturedTickStart = false;
            clearMomentumState();
            return;
        }

        if (event.phase == TickEvent.Phase.START) {
            motionXAtTickStart = player.motionX;
            motionZAtTickStart = player.motionZ;
            onGroundAtTickStart = player.onGround;
            capturedTickStart = true;

            if (!player.onGround && canUseBunnyhop(player)) {
                recentAirMotionX = player.motionX;
                recentAirMotionZ = player.motionZ;
                ticksSinceAirMomentum = 0;
            } else if (ticksSinceAirMomentum < 1000) {
                ticksSinceAirMomentum++;
            }
            return;
        }

        if (!capturedTickStart) {
            return;
        }
        capturedTickStart = false;

        if (!canUseBunnyhop(player)) {
            clearMomentumState();
            return;
        }

        boolean jumpHeld = player.movementInput != null && player.movementInput.jump;

        if (player.onGround) {
            if (Config.autoBhop && jumpHeld) {
                preserveLandingMomentum(player);
                player.motionY = 0.41999998688697815D;
                player.onGround = false;
                player.isAirBorne = true;
            }
            return;
        }

        applyAirMovement(player);
    }

    private static boolean canUseBunnyhop(EntityClientPlayerMP player) {
        return !player.capabilities.isFlying && !player.isRiding()
            && !player.isInWater()
            && !player.handleLavaMovement()
            && !player.isOnLadder()
            && !player.isSneaking();
    }

    private void preserveLandingMomentum(EntityClientPlayerMP player) {
        if (player.isCollidedHorizontally) {
            return;
        }

        double preserveX = motionXAtTickStart;
        double preserveZ = motionZAtTickStart;
        double preserveSpeedSq = preserveX * preserveX + preserveZ * preserveZ;

        if (ticksSinceAirMomentum <= Config.landingGraceTicks) {
            double recentAirSpeedSq = recentAirMotionX * recentAirMotionX + recentAirMotionZ * recentAirMotionZ;
            if (recentAirSpeedSq > preserveSpeedSq) {
                preserveX = recentAirMotionX;
                preserveZ = recentAirMotionZ;
                preserveSpeedSq = recentAirSpeedSq;
            }
        }

        double currentSpeedSq = player.motionX * player.motionX + player.motionZ * player.motionZ;
        if (preserveSpeedSq > currentSpeedSq) {
            player.motionX = preserveX;
            player.motionZ = preserveZ;
        }
    }

    private void applyAirMovement(EntityClientPlayerMP player) {
        double velocityX;
        double velocityZ;

        if (onGroundAtTickStart || player.isCollidedHorizontally) {
            velocityX = player.motionX;
            velocityZ = player.motionZ;
        } else {
            velocityX = motionXAtTickStart;
            velocityZ = motionZAtTickStart;
        }

        float forward = player.moveForward;
        float strafe = player.moveStrafing;
        double inputLength = Math.sqrt(forward * forward + strafe * strafe);

        if (inputLength > 0.0001D) {
            if (inputLength > 1.0D) {
                forward /= inputLength;
                strafe /= inputLength;
            }

            double yaw = player.rotationYaw * Math.PI / 180.0D;
            double sin = MathHelper.sin((float) yaw);
            double cos = MathHelper.cos((float) yaw);

            double wishX = strafe * cos - forward * sin;
            double wishZ = forward * cos + strafe * sin;
            double wishLength = Math.sqrt(wishX * wishX + wishZ * wishZ);

            if (wishLength > 0.0001D) {
                wishX /= wishLength;
                wishZ /= wishLength;

                double currentSpeed = velocityX * wishX + velocityZ * wishZ;
                double addSpeed = Config.airWishSpeed - currentSpeed;

                if (addSpeed > 0.0D) {
                    double accelerateSpeed = Config.airAcceleration * Config.airWishSpeed * 0.05D;
                    if (accelerateSpeed > addSpeed) {
                        accelerateSpeed = addSpeed;
                    }

                    velocityX += wishX * accelerateSpeed;
                    velocityZ += wishZ * accelerateSpeed;
                }
            }
        }

        double maxSpeed = Config.maxSpeedMps / 20.0D;
        double horizontalSpeed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);

        if (horizontalSpeed > maxSpeed && horizontalSpeed > 0.0D) {
            double scale = maxSpeed / horizontalSpeed;
            velocityX *= scale;
            velocityZ *= scale;
        }

        player.motionX = velocityX;
        player.motionZ = velocityZ;
    }

    private void clearMomentumState() {
        recentAirMotionX = 0.0D;
        recentAirMotionZ = 0.0D;
        ticksSinceAirMomentum = 1000;
    }
}
