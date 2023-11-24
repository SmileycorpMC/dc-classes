package com.afunproject.dawncraft.classes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosSupport {

    public static void addItem(Player player, String slot, ItemStack stack) {
        CuriosApi.getCuriosHelper().setEquippedCurio(player, slot, 0, stack);
    }

}
