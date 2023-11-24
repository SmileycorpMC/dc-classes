package com.afunproject.dawncraft.classes;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface PickedClass {

    boolean picked();

    void setPicked();

    class Implementation implements PickedClass {

        private boolean picked;

        @Override
        public boolean picked() {
            return picked;
        }

        @Override
        public void setPicked() {
            picked = true;
        }

    }

    class Provider implements ICapabilitySerializable<ByteTag> {

        private final PickedClass impl;

        public Provider() {
            impl = new Implementation();
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == DCClasses.PICKED_CLASS ? LazyOptional.of(() -> impl).cast() : LazyOptional.empty();
        }

        @Override
        public ByteTag serializeNBT() {
            return ByteTag.valueOf(impl.picked());
        }

        @Override
        public void deserializeNBT(ByteTag nbt) {
            if (nbt.getAsByte() > 0) impl.setPicked();
        }

    }

}
