package com.crazerium.gtnhbhop;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class Config {

    public static boolean enabled = true;
    public static boolean enabledByDefault = false;
    public static boolean autoBhop = true;
    public static boolean showSpeedHud = true;

    public static double airAcceleration = 12.0D;
    public static double airWishSpeed = 0.25D;
    public static double maxSpeedMps = 40.0D;

    public static int landingGraceTicks = 2;
    public static int hudYOffset = 34;

    private Config() {}

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        enabled = configuration
            .getBoolean("enabled", Configuration.CATEGORY_GENERAL, enabled, "Master switch for bunnyhop movement.");

        enabledByDefault = configuration.getBoolean(
            "enabledByDefault",
            Configuration.CATEGORY_GENERAL,
            enabledByDefault,
            "Start each game session with bunnyhop enabled. The in-game toggle key can still change it.");

        autoBhop = configuration.getBoolean(
            "autoBhop",
            Configuration.CATEGORY_GENERAL,
            autoBhop,
            "Jump immediately when landing while the jump key is held.");

        showSpeedHud = configuration.getBoolean(
            "showSpeedHud",
            Configuration.CATEGORY_GENERAL,
            showSpeedHud,
            "Show horizontal movement speed at the bottom center while bunnyhop mode is enabled.");

        airAcceleration = configuration
            .get(
                "movement",
                "airAcceleration",
                airAcceleration,
                "Source-style air acceleration strength.",
                0.0D,
                100.0D)
            .getDouble(airAcceleration);

        airWishSpeed = configuration
            .get(
                "movement",
                "airWishSpeed",
                airWishSpeed,
                "Maximum velocity component added in the current air-strafe direction, in blocks per tick.",
                0.01D,
                2.0D)
            .getDouble(airWishSpeed);

        maxSpeedMps = configuration
            .get(
                "movement",
                "maxSpeedMps",
                maxSpeedMps,
                "Maximum horizontal bunnyhop speed in blocks per second. One Minecraft block is treated as one meter.",
                1.0D,
                200.0D)
            .getDouble(maxSpeedMps);

        landingGraceTicks = configuration.getInt(
            "landingGraceTicks",
            "movement",
            landingGraceTicks,
            0,
            5,
            "How many ticks of recently captured air momentum may be restored on an immediate auto-bhop landing.");

        hudYOffset = configuration.getInt(
            "hudYOffset",
            "hud",
            hudYOffset,
            0,
            300,
            "Distance of the speed counter from the bottom of the screen, in scaled GUI pixels.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
