package com.crazerium.gtnhbhop;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.crazerium.gtnhbhop.client.BunnyhopHandler;
import com.crazerium.gtnhbhop.client.KnifeAnimationHandler;
import com.crazerium.gtnhbhop.client.SpeedHud;
import com.crazerium.gtnhbhop.client.render.KnifeRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        BunnyhopHandler.initializeFromConfig();
        ClientRegistry.registerKeyBinding(BunnyhopHandler.TOGGLE_KEY);

        BunnyhopHandler bunnyhopHandler = new BunnyhopHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(bunnyhopHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(new KnifeAnimationHandler());
        MinecraftForgeClient.registerItemRenderer(GTNHBhop.bhopKnife, new KnifeRenderer());
        MinecraftForge.EVENT_BUS.register(new SpeedHud());
    }
}
