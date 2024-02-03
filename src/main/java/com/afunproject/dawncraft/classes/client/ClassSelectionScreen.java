package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.Constants;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.data.ItemEntry;
import com.afunproject.dawncraft.classes.integration.CuriosIntegration;
import com.afunproject.dawncraft.classes.network.NetworkHandler;
import com.afunproject.dawncraft.classes.network.PickClassMessage;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;
import yesman.epicfight.skill.Skill;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassSelectionScreen extends Screen {
    
    private static final int TEXT_WIDTH = 30;
    protected int imageWidth = 168;
    protected int imageHeight = 180;
    private int page = 0;
    private final List<DCClass> classes;
    private final RemotePlayer player;
    private final List<Button> buttons = Lists.newArrayList();
    private int i;
    protected int leftPos;
    protected int topPos;
    protected final List<Component> description = Lists.newArrayList();
    private final List<ClassSlot> slots = Lists.newArrayList();

    public ClassSelectionScreen(List<DCClass> cache) {
        super(new TranslatableComponent("title.dcclasses.screen"));
        Minecraft minecraft = Minecraft.getInstance();
        classes = cache.stream().sorted(Comparator.comparingInt(DCClass::getIndex)).collect(Collectors.toList());
        player = new RemotePlayer(minecraft.level, minecraft.player.getGameProfile());
        reloadEquipment();
        reloadText();
    }

    @Override
    public void init() {
        buttons.clear();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        buttons.add(new Button(leftPos, topPos - 10, 20, 20, new TextComponent("<"), b -> switchPage(page - 1)));
        buttons.add(new Button(leftPos + imageWidth - 20, topPos -10, 20, 20, new TextComponent(">"), b -> switchPage(page + 1)));
        buttons.add(new Button(leftPos + imageWidth / 2 - 30, topPos + imageHeight, 60, 20, new TranslatableComponent("button.dcclasses.confirm"), b -> confirm()));
        reloadSlots();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderDirtBackground(0);
        for(Widget widget : buttons) widget.render(poseStack, mouseX,mouseY, partialTicks);
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        //title
        drawCenteredString(poseStack, minecraft.font,  new TranslatableComponent(clazz.getTranslationKey()), leftPos + imageWidth/2, topPos -3, 0x9E0CD2);
        //description
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, Constants.loc("textures/gui/classes_description.png"));
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, leftPos - 6, topPos + imageHeight / 2 + 14, 1, 0, 0, 179, 75, 179, 75);
        RenderSystem.disableBlend();
        poseStack.popPose();
        for (int i = 0; i < description.size(); i ++) {
            Component component = description.get(i);
            drawString(poseStack, minecraft.font,  component, leftPos, topPos + imageHeight / 2 + 20 + i * 8, 0xFFFFFF);
        }
        //player
        int entityX = leftPos + imageWidth / 2;
        int entityY = topPos + imageHeight / 2 + 10;
        InventoryScreen.renderEntityInInventory(entityX, entityY, 38, entityX - mouseX, entityY + (player.getEyeHeight()) - mouseY, player);
        //attributes
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, Constants.loc("textures/gui/attribute/health.png"));
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, leftPos + 60, topPos + 14, 1, 0, 0, 9, 9, 9, 9);
        RenderSystem.disableBlend();
        poseStack.popPose();
        MutableComponent text = new TextComponent(String.valueOf(clazz.getHealthMult()));
        drawString(poseStack, minecraft.font,  text, leftPos + 60 - minecraft.font.width(text) - 2, topPos + 15, ChatFormatting.DARK_RED.getColor());
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, Constants.loc("textures/gui/attribute/stamina.png"));
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, leftPos + imageWidth - 69, topPos + 14, 1, 0, 0, 9, 9, 9, 9);
        RenderSystem.disableBlend();
        poseStack.popPose();
        drawString(poseStack, minecraft.font,  new TextComponent(String.format("%.2f", clazz.getStaminaMult())), leftPos + imageWidth - 58, topPos + 15, ChatFormatting.GREEN.getColor());
        //items and skills
        drawCenteredString(poseStack, minecraft.font,  new TranslatableComponent("text.dcclasses.items"), leftPos - 10, topPos + 15, 0xFFFFFF);
        drawCenteredString(poseStack, minecraft.font,  new TranslatableComponent("text.dcclasses.skills"), leftPos + imageWidth + 10, topPos + 15, 0xFFFFFF);
        ClassSlot hoveredSlot = null;
        for (ClassSlot slot : slots) {
            slot.render(poseStack, mouseX, mouseY, partialTicks);
            if (hoveredSlot == null && slot.isMouseOver(mouseX, mouseY)) hoveredSlot = slot;
        }
        if (hoveredSlot != null) renderTooltip(poseStack, hoveredSlot.getTooltip(), Optional.empty(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_95587_) {
        for (Button button : buttons) if (button.isMouseOver(mouseX, mouseY)) return button.mouseClicked(mouseX, mouseY, p_95587_);
        return false;
    }

    private void switchPage(int page) {
        this.page = Math.floorMod(page, classes.size());
        reloadEquipment();
        reloadText();
        reloadSlots();
    }

    private void reloadEquipment() {
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) player.setItemSlot(slot, ItemStack.EMPTY);
        player.getInventory().clearContent();
        if (ModList.get().isLoaded("curios")) CuriosIntegration.clear(player);
        clazz.setVisualEquipment(player);
    }
    
    private void reloadText() {
        description.clear();
        String str = new TranslatableComponent(getSelectedClass().getTranslationKey() + ".desc").getString();
        int position = 0;
        while (position < str.length()) {
            int size = Math.min(TEXT_WIDTH, str.length() - position);
            int newPos = position + size;
            if (str.substring(position, newPos).contains("\n")) {
                int i = str.substring(position, newPos).indexOf("\n");
                description.add(new TextComponent(str.substring(position, position + i)));
                position = position + i + 1;
                continue;
            }
            if (size < TEXT_WIDTH || newPos >= str.length()) {
                description.add(new TextComponent(str.substring(position)));
                break;
            }
            for (int i = 0; i <= size; i++) {
                if (i == size) {
                    description.add(new TextComponent(str.substring(position, newPos + 1)));
                    position = newPos;
                    break;
                } else if (str.charAt(newPos - i) == ' ') {
                    description.add(new TextComponent(str.substring(position, newPos - i + 1)));
                    position = newPos - i + 1;
                    break;
                }
            }
        }
    }
    
    private void reloadSlots() {
        slots.clear();
        DCClass clazz = getSelectedClass();
        for (int i = 0; i < clazz.getItems().size(); i++) slots.add(new ItemSlot(clazz.getItems().get(i),leftPos - 18 - i/4 * 18, topPos + 28 + (i % 4) * 18));
        List<Skill> skills = clazz.getSkills();
        for (int i = 0; i < skills.size(); i++) slots.add(new SkillSlot(skills.get(i), leftPos + imageWidth + 2 + i/4 * 18, topPos + 28 + (i % 4) * 18));
    }
    
    private void confirm() {
        NetworkHandler.NETWORK_INSTANCE.sendTo(new PickClassMessage(getSelectedClass().getRegistryName()),
                minecraft.player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
        onClose();
    }

    @Override
    public void onClose() {
        classes.clear();
        super.onClose();
    }

    public DCClass getSelectedClass() {
        if (classes.size() == 0) return null;
        return classes.get(page);
    }

}
