package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.integration.CuriosIntegration;
import com.google.gson.JsonObject;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemEntry {

    private final ItemStack stack;
    private final String slot;

    public ItemEntry(JsonObject obj) throws Exception {
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("id").getAsString())), count);
        if (obj.has("nbt")) stack.setTag(TagParser.parseTag(obj.get("nbt").getAsString()));
        slot = obj.has("slot") ? obj.get("slot").getAsString() : "mainhand";
    }

    public void apply(Player player) {
        ClassesLogger.logInfo("set " + stack + " to slot " + slot);
        if (slot.contains("curios:") && ModList.get().isLoaded("curios"))
            CuriosIntegration.addItem(player, slot.replace("curios:", ""), stack.copy());
        else {
            EquipmentSlot slot;
            try {
                slot = EquipmentSlot.byName(this.slot);
            } catch (Exception e) {
                slot = EquipmentSlot.MAINHAND;
            }
            if (slot == EquipmentSlot.MAINHAND) {
                player.getInventory().add(stack.copy());
            } else player.setItemSlot(slot, stack.copy());
        }
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        obj.addProperty("count", stack.getCount());
        if (stack.hasTag()) obj.addProperty("nbt", stack.getTag().toString());
        if (slot != null) obj.addProperty("slot", slot);
        return obj;
    }

}
