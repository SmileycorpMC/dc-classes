package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.ClassHandler;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.smileycorp.atlas.api.network.SimpleAbstractMessage;

public class OpenClassGUIMessage extends SimpleAbstractMessage {

    public OpenClassGUIMessage() {}

    @Override
    public void read(FriendlyByteBuf buf) {
        ClassHandler.clear();
        while (buf.isReadable()) {
            try {
                ClassHandler.addClass(new DCClass(new ResourceLocation(buf.readUtf()), JsonParser.parseString(buf.readUtf()).getAsJsonObject()));
            } catch (Exception e) {}
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        for (DCClass clazz : ClassHandler.getClasses()) {
            buf.writeUtf(clazz.getRegistryName().toString());
            buf.writeUtf(clazz.serialize().toString());
        }
    }

    @Override
    public void handle(PacketListener listener) {}

}
