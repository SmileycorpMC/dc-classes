package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.ClassHandler;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.smileycorp.atlas.api.network.SimpleAbstractMessage;

import java.util.List;

public class OpenClassGUIMessage extends SimpleAbstractMessage {

    private List<DCClass> cache = Lists.newArrayList();

    public OpenClassGUIMessage() {}

    @Override
    public void read(FriendlyByteBuf buf) {
        while (buf.isReadable()) {
            try {
               cache.add(new DCClass(new ResourceLocation(buf.readUtf()), JsonParser.parseString(buf.readUtf()).getAsJsonObject()));
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

    public List<DCClass> getCache() {
        return cache;
    }

    @Override
    public void handle(PacketListener listener) {}

}
