package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.data.AttributeEntry;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AttributeSlot extends ClassSlot {
    
    private final AttributeEntry attribute;
    
    public AttributeSlot(AttributeEntry attribute, int width, int x, int y) {
        super(x, y, width,  11);
        this.attribute = attribute;
    }
    
    @Override
    public List<Component> getTooltip() {
        return Lists.newArrayList(new TranslatableComponent(attribute.getAttribute().getDescriptionId()).append(new TextComponent(": " + attribute.getValue())));
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        poseStack.pushPose();
        ResourceLocation loc = attribute.getName();
        RenderSystem.setShaderTexture(0, new ResourceLocation(loc.getNamespace(), "textures/attribute/" + loc.getPath() + ".png"));
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, x + 1, y + 1, 1, 0, 0, 9, 9, 9, 9);
        RenderSystem.disableBlend();
        poseStack.popPose();
        GuiComponent.drawString(poseStack, minecraft.font,  attribute.getText(), x + 11, y + 2, attribute.getTextColour().getValue());
    }
    
}
