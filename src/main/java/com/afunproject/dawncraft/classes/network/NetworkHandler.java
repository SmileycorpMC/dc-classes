package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.Constants;
import com.afunproject.dawncraft.classes.client.ClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.smileycorp.atlas.api.network.NetworkUtils;

public class NetworkHandler {

    public static SimpleChannel NETWORK_INSTANCE;

    public static void initPackets() {
        NETWORK_INSTANCE = NetworkUtils.createChannel(Constants.loc("main"));
        NetworkUtils.registerMessage(NETWORK_INSTANCE,0, OpenClassGUIMessage.class, (pkt, ctx) -> {
            ctx.get().enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientHandler::displayGUI));
            ctx.get().setPacketHandled(true);
        });
        NetworkUtils.registerMessage(NETWORK_INSTANCE,1, PickClassMessage.class, (pkt, ctx) -> {
            ctx.get().enqueueWork(() -> pkt.apply(ctx.get().getSender()));
            ctx.get().setPacketHandled(true);
        });
    }

}
