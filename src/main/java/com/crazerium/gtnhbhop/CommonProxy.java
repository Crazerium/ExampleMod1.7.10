package com.crazerium.gtnhbhop;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        GTNHBhop.LOG.info("Loading {} {}", GTNHBhop.NAME, Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {}
}
