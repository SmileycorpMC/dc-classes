package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.data.ItemEntry;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.TooltipFlag;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
import yesman.epicfight.skill.Skill;

import java.util.List;

public class SkillSlot extends ClassSlot {
    
    private static final int TEXT_WIDTH = 20;
    
    private final Skill skill;
    private final List<Component> tooltip = Lists.newArrayList();
    
    public SkillSlot(Skill skill, int x, int y) {
        super(x, y, 16, 16);
        this.skill = skill;
        tooltip.add(new TranslatableComponent(skill.getTranslationKey()).withStyle(ChatFormatting.AQUA));
        tooltip.add(new TranslatableComponent("skill.epicfight." + skill.getCategory().toString().toLowerCase() + ".category").withStyle(ChatFormatting.BLUE));
        int position = 0;
        String str = new TranslatableComponent(skill.getTranslationKey() + ".tooltip", skill.getTooltipArgsOfScreen(Lists.newArrayList()).toArray(new Object[0])).getString();
        while (position < str.length()) {
            int size = Math.min(TEXT_WIDTH, str.length() - position);
            int newPos = position + size;
            if (str.substring(position, newPos).contains("\n")) {
                int i = str.substring(position, newPos).indexOf("\n");
                tooltip.add(new TextComponent(str.substring(position, position + i)));
                position = position + i + 1;
                continue;
            }
            if (size < TEXT_WIDTH || newPos >= str.length()) {
                tooltip.add(new TextComponent(str.substring(position)));
                break;
            }
            for (int i = 0; i <= size; i++) {
                if (i == size) {
                    tooltip.add(new TextComponent(str.substring(position, newPos + 1)));
                    position = newPos;
                    break;
                } else if (str.charAt(newPos - i) == ' ') {
                    tooltip.add(new TextComponent(str.substring(position, newPos - i + 1)));
                    position = newPos - i + 1;
                    break;
                }
            }
        }
    }
    
    @Override
    public List<Component> getTooltip() {
        return tooltip;
    }
    
    @Override
    public void render(PoseStack poseStack, int p_94670_, int p_94671_, float p_94672_) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, skill.getSkillTexture());
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, x, y, 1, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
    
}
