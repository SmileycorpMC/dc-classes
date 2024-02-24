package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.integration.epicfight.EpicFightIntegration;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

public interface PickedClass {

    DCClass getDCClass();

    void setDCClass(DCClass clazz);

    boolean hasPicked();

    boolean hasEffect();

    void applyEffect(ServerPlayer player, boolean addItems);

    void applyStatModifiers(ServerPlayer player);
    
    void setGUIOpen(boolean GUIOpen);
    
    boolean isGUIOpen();
    
    boolean hasStatModifiers();

    CompoundTag save();

    void load(CompoundTag tag);

    class Implementation implements PickedClass {

        private DCClass clazz;
        private boolean hasEffect;
        private boolean GUIOpen;
        private boolean hasStatModifiers;

        @Override
        public DCClass getDCClass() {
            return clazz;
        }

        @Override
        public void setDCClass(DCClass clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean hasPicked() {
            return clazz != null;
        }

        @Override
        public boolean hasEffect() {
            return hasEffect;
        }

        @Override
        public void applyEffect(ServerPlayer player, boolean addItems) {
            if (clazz == null) return;
            if (ModList.get().isLoaded("epicfight")) EpicFightIntegration.applySkills(clazz, player);
            clazz.applyStatModifiers(player);
            if (addItems) clazz.addItems(player);
            ClassesLogger.logInfo("Set player " + player.getDisplayName().getString() + " to class " + clazz);
            hasEffect = true;
        }

        @Override
        public void applyStatModifiers(ServerPlayer player) {
            if (clazz == null) return;
            clazz.applyStatModifiers(player);
            hasEffect = true;
            hasStatModifiers = true;
        }
        
        @Override
        public void setGUIOpen(boolean GUIOpen) {
            this.GUIOpen = GUIOpen;
        }
    
        @Override
        public boolean isGUIOpen() {
            return GUIOpen;
        }
    
        @Override
        public boolean hasStatModifiers() {
            return hasStatModifiers;
        }
    
        @Override
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            if (clazz != null) tag.putString("class", clazz.toString());
            if (hasEffect) tag.putBoolean("hasEffect", hasEffect);
            return tag;
        }

        @Override
        public void load(CompoundTag tag) {
            if (tag.contains("class")) clazz = ClassHandler.getClass(new ResourceLocation(tag.getString("class")));
            if (tag.contains("hasEffect")) hasEffect = tag.getBoolean("hasEffect");
        }

    }

    class Provider implements ICapabilitySerializable<CompoundTag> {

        private final PickedClass impl;

        public Provider() {
            impl = new Implementation();
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == DCClasses.PICKED_CLASS ? LazyOptional.of(() -> impl).cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return impl.save();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            impl.load(nbt);
        }

    }

}
