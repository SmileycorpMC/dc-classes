package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.data.ItemEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemSlot extends ClassSlot {
    
    private final ItemEntry item;
    
    public ItemSlot(ItemEntry item, int x, int y) {
        super(x, y, 16, 16);
        this.item = item;
    }
    
    @Override
    public List<Component> getTooltip() {
        Minecraft minecraft = Minecraft.getInstance();
        List<Component> tooltip = item.getStack().getTooltipLines(minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        tooltip.add(1, new TranslatableComponent("text.dcclasses.slot", item.getSlot()));
        return tooltip;
    }
    
    @Override
    public void render(PoseStack p_94669_, int p_94670_, int p_94671_, float p_94672_) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        renderer.renderGuiItem(item.getStack(), x, y);
        renderer.renderGuiItemDecorations(minecraft.font, item.getStack(), x, y);
    }
    
}
