package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.Constants;
import com.afunproject.dawncraft.classes.data.AttributeEntry;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.data.ItemEntry;
import com.afunproject.dawncraft.classes.integration.CuriosIntegration;
import com.afunproject.dawncraft.classes.integration.epicfight.EpicFightIntegration;
import com.afunproject.dawncraft.classes.integration.epicfight.client.SkillSlot;
import com.afunproject.dawncraft.classes.network.NetworkHandler;
import com.afunproject.dawncraft.classes.network.PickClassMessage;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassSelectionScreen extends Screen {
    
    public static final ResourceLocation TEXTURE = Constants.loc("textures/gui/class_selection.png");
    
    private static final int TEXT_WIDTH = 40;
    protected int guiWidth = 168;
    protected int guiHeight = 180;
    private int page = 0;
    private final List<DCClass> classes;
    private final RemotePlayer player;
    private PlayerRenderer playerRenderer;
    private final List<AbstractButton> buttons = Lists.newArrayList();
    protected int leftPos;
    protected int topPos;
    protected final List<Component> description = Lists.newArrayList();
    private final List<ClassSlot> slots = Lists.newArrayList();
    private int itemX, itemWidth, itemHeight, skillX, skillWidth, skillHeight;

    public ClassSelectionScreen(List<DCClass> cache) {
        super(Component.translatable("title.dcclasses.screen"));
        Minecraft mc = Minecraft.getInstance();
        player = new RemotePlayer(mc.level, mc.player.getGameProfile());
        if (cache.isEmpty()) {
            ClassesLogger.logError("no enabled classes ", new Exception());
            classes = null;
            return;
        }
        classes = cache.stream().sorted(Comparator.comparingInt(DCClass::getIndex)).collect(Collectors.toList());
        reloadEquipment();
        reloadText();
    }

    @Override
    public void init() {
        buttons.clear();
        leftPos = (width - guiWidth) / 2;
        topPos = (height - guiHeight) / 2;
        buttons.add(new ClassSwitchButton(this, leftPos + 4, topPos - 10, true));
        buttons.add(new ClassSwitchButton(this, leftPos + guiWidth - 16, topPos - 10, false));
        buttons.add(new ConfirmButton(this, leftPos + guiWidth / 2 - 30, topPos + guiHeight));
        reloadSlots();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        if (width == 0 || height == 0) return;
        renderDirtBackground(gui);
        gui.blitWithBorder(TEXTURE, leftPos + 10, topPos + 10, 0, 0, 148, 106, 32, 42, 19, 9, 9, 9);
        for(Renderable widget : buttons) widget.render(gui, mouseX, mouseY, partialTicks);
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        //title
        drawBox(gui, leftPos + 18, topPos - 10, 131, 17);
        gui.drawCenteredString(minecraft.font,  Component.translatable(clazz.getTranslationKey()), leftPos + guiWidth /2, topPos -6, 0x9E0CD2);
        //description
        int offset = (int)((float)(description.size() * 9)/2f);
        drawBox(gui, leftPos - 6, topPos + guiHeight / 2 + 48 - offset, 179, description.size() * 9 + 8);
        for (int i = 0; i < description.size(); i ++) {
            Component component = description.get(i);
            gui.drawCenteredString(minecraft.font,  component, leftPos + guiWidth / 2, topPos + guiHeight / 2 + 52 + i * 9 - offset, 0xFFFFFF);
        }
        //player
        if (playerRenderer == null) {
            playerRenderer = new PlayerRenderer(player, leftPos + guiWidth / 2, topPos + guiHeight / 2 + 13);
            playerRenderer.setClass(getSelectedClass());
            playerRenderer.setClass(getSelectedClass());
        }
        playerRenderer.render(gui, mouseX, mouseY, partialTicks);
        //items, skills and attributes
        if (itemHeight > 0) {
            drawBox(gui, itemX, topPos + 17, itemWidth, itemHeight);
            gui.drawCenteredString(minecraft.font, Component.translatable("text.dcclasses.items"), leftPos - 12, topPos + 21, 0xFFFFFF);
        }
        if (skillHeight > 0) {
            drawBox(gui, skillX, topPos + 17, skillWidth, skillHeight);
            gui.drawCenteredString(minecraft.font, Component.translatable("text.dcclasses.skills"), leftPos + guiWidth + 12, topPos + 21, 0xFFFFFF);
        }
        ClassSlot hoveredSlot = null;
        for (ClassSlot slot : slots) {
            slot.render(gui, mouseX, mouseY, partialTicks);
            if (hoveredSlot == null && slot.isMouseOver(mouseX, mouseY)) hoveredSlot = slot;
        }
        if (hoveredSlot != null) gui.renderTooltip(minecraft.font, hoveredSlot.getTooltip(), Optional.empty(), mouseX, mouseY);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_95587_) {
        for (AbstractButton button : buttons) if (button.isMouseOver(mouseX, mouseY)) return button.mouseClicked(mouseX, mouseY, p_95587_);
        return false;
    }
    
    @Override
    public void onClose() {
        if (classes != null) classes.clear();
        super.onClose();
    }

    public void switchPage(int page) {
        this.page = Math.floorMod(page + this.page, classes.size());
        if (playerRenderer != null) playerRenderer.setClass(getSelectedClass());
        reloadEquipment();
        reloadText();
        reloadSlots();
    }
    
    public void confirm() {
        NetworkHandler.NETWORK_INSTANCE.sendTo(new PickClassMessage(getSelectedClass().getRegistryName()),
                minecraft.player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
        onClose();
    }
    
    public DCClass getSelectedClass() {
        if (classes == null) {
            onClose();
            return null;
        }
        if (classes.size() == 0) return null;
        return classes.get(page);
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
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        String str = Component.translatable(clazz.getTranslationKey() + ".desc").getString();
        int position = 0;
        while (position < str.length()) {
            if (description.size() >= 7) break;
            int size = Math.min(TEXT_WIDTH, str.length() - position);
            while (Minecraft.getInstance().font.width(str.substring(position, position + size)) > 169) size--;
            int newPos = position + size;
            if (str.substring(position, newPos).contains("\n")) {
                int i = str.substring(position, newPos).indexOf("\n");
                description.add(Component.literal(str.substring(position, position + i)));
                position = position + i + 1;
                continue;
            }
            if (newPos >= str.length()) {
                description.add(Component.literal(str.substring(position)));
                break;
            }
            for (int i = 0; i <= size; i++) {
                if (i == size) {
                    description.add(Component.literal(str.substring(position, newPos + 1)));
                    position = newPos;
                    break;
                } else if (str.charAt(newPos - i) == ' ') {
                    description.add(Component.literal(str.substring(position, newPos - i + 1)));
                    position = newPos - i + 1;
                    break;
                }
            }
        }
    }
    
    private void reloadSlots() {
        slots.clear();
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        List <AttributeEntry> attributes = clazz.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeEntry attribute = attributes.get(i);
            int width = minecraft.font.width(attribute.getText()) + 11;
            slots.add(new AttributeSlot(attribute, width, leftPos + 11 + (int)((((float)guiWidth - 22f) * (float) (i + 1)) / (attributes.size() + 1f)) - (int)((float)width * 0.5f), topPos + 13));
        }
        List<ItemEntry> items = clazz.getItems();
        int itemRows = (int)(((float)items.size() -1) / 3f) + 1;
        itemWidth = Math.max(itemRows * 18, minecraft.font.width(Component.translatable("text.dcclasses.items"))) + 8;
        itemHeight = items.isEmpty() ? 0 : 20 + (int)Math.ceil((float)items.size()/(float)itemRows) * 18;
        itemX = leftPos - 12 - (int)((float)itemWidth / 2f);
        for (int i = 0; i < items.size(); i++) slots.add(new ItemSlot(items.get(i),leftPos - 28 + itemRows * 8 - i % itemRows * 18, topPos + 32 + (i / itemRows) * 18));
        if (!ModList.get().isLoaded("epicfight")) return;
        List<String> skills = EpicFightIntegration.getVerifiedSkills(clazz);
        int skillRows = (int)((float)(skills.size() -1) / 3f) + 1;
        skillWidth = Math.max(skillRows * 18, minecraft.font.width(Component.translatable("text.dcclasses.skills"))) + 8;
        skillHeight = skills.isEmpty() ? 0 : 20 + (int)Math.ceil((float)skills.size()/(float)skillRows) * 18;
        skillX = leftPos + guiWidth + 12 - (int)((float)skillWidth / 2f);
        for (int i = 0; i < skills.size(); i++) slots.add(new SkillSlot(skills.get(i), leftPos + guiWidth + 12 - skillRows * 8 + i % skillRows * 18, topPos + 32 + (i / skillRows) * 18));
    }
    
    private void drawBox(GuiGraphics gui, int x, int y, int width, int height) {
        gui.blitWithBorder(TEXTURE, x, y, 0, 42, width, height, 32, 32, 4, 4, 4, 4);
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

}
