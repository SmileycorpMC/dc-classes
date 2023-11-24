package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MODID)
public class DCClasses {

    public static Capability<PickedClass> PICKED_CLASS = CapabilityManager.get(new CapabilityToken<>(){});

    public DCClasses() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkHandler.initPackets();
    }

}
