package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.ClassHandler;
import com.afunproject.dawncraft.classes.DCClasses;
import com.afunproject.dawncraft.classes.PickedClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.smileycorp.atlas.api.network.SimpleAbstractMessage;

public class PickClassMessage extends SimpleAbstractMessage {

    private ResourceLocation loc;

    public PickClassMessage() {}

    public PickClassMessage(ResourceLocation loc) {
        this.loc = loc;
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        loc = buf.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(loc);
    }

    @Override
    public void handle(PacketListener listener) {}

    public void apply(ServerPlayer sender) {
        LazyOptional<PickedClass> optional = sender.getCapability(DCClasses.PICKED_CLASS);
        if (optional.isPresent()) {
            PickedClass cap = optional.orElseGet(null);
            cap.setDCClass(ClassHandler.getClass(loc));
            cap.applyEffect(sender, true);
            cap.setGUIOpen(false);
        }
    }

}
