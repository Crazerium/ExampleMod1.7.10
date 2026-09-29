package com.crazerium.gtnhbhop;

import net.minecraft.item.Item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crazerium.gtnhbhop.item.ItemBhopKnife;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = GTNHBhop.MODID, version = Tags.VERSION, name = GTNHBhop.NAME, acceptedMinecraftVersions = "[1.7.10]")
public class GTNHBhop {

    public static final String MODID = "gtnhbhop";
    public static final String NAME = "GTNH Bunnyhop";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static Item bhopKnife;

    @SidedProxy(clientSide = "com.crazerium.gtnhbhop.ClientProxy", serverSide = "com.crazerium.gtnhbhop.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        bhopKnife = new ItemBhopKnife();
        GameRegistry.registerItem(bhopKnife, "bhop_knife");

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
