package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.Constants;
import net.minecraftforge.network.simple.SimpleChannel;
import net.smileycorp.atlas.api.network.NetworkUtils;
import net.smileycorp.atlas.api.util.Func;

public class NetworkHandler {

    public static SimpleChannel NETWORK_INSTANCE;

    public static void initPackets() {
        NETWORK_INSTANCE = NetworkUtils.createChannel(Constants.loc("Main"));
        NetworkUtils.registerMessage(NETWORK_INSTANCE,0, OpenClassGUIMessage.class, (pkt, ctx) -> ctx.get().setPacketHandled(true));
    }

}
