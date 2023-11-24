package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.DCClassLoader;
import com.afunproject.dawncraft.classes.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MODID)
public class DawnCraftClasses {

    public DawnCraftClasses() {
        MinecraftForge.EVENT_BUS.register(this);
        NetworkHandler.initPackets();
    }

    @SubscribeEvent
    public void addResourceReload(AddReloadListenerEvent event ) {
        event.addListener(DCClassLoader.INSTANCE);
    }

}
