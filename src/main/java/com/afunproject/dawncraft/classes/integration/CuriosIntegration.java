package com.afunproject.dawncraft.classes.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosIntegration {

    @SuppressWarnings("deprecation")
    public static void addItem(Player player, String slot, ItemStack stack) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        if (optional.isPresent()) optional.orElseGet(null).setEquippedCurio(slot, 0, stack);
    }

    @SuppressWarnings("deprecation")
    public static void clear(Player player) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        if (optional.isPresent()) optional.orElseGet(null).reset();
    }
}
