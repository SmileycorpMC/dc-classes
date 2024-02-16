package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.data.AttributeEntry;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.data.ItemEntry;
import com.afunproject.dawncraft.classes.integration.CuriosIntegration;
import com.afunproject.dawncraft.classes.integration.epicfight.EpicFightIntegration;
import com.afunproject.dawncraft.classes.integration.epicfight.client.EpicFightPlayerRenderer;
import com.afunproject.dawncraft.classes.integration.epicfight.client.SkillSlot;
import com.afunproject.dawncraft.classes.network.NetworkHandler;
import com.afunproject.dawncraft.classes.network.PickClassMessage;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassSelectionScreen extends Screen {
    
    private static final int TEXT_WIDTH = 31;
    protected int guiWidth = 168;
    protected int guiHeight = 180;
    private int page = 0;
    private final List<DCClass> classes;
    private final RemotePlayer player;
    private final EpicFightPlayerRenderer playerRenderer;
    private final List<Button> buttons = Lists.newArrayList();
    private int i;
    protected int leftPos;
    protected int topPos;
    protected final List<Component> description = Lists.newArrayList();
    private final List<ClassSlot> slots = Lists.newArrayList();
    private int itemX, itemWidth, itemHeight, skillX, skillWidth, skillHeight;

    public ClassSelectionScreen(List<DCClass> cache) {
        super(new TranslatableComponent("title.dcclasses.screen"));
        Minecraft minecraft = Minecraft.getInstance();
        player = new RemotePlayer(minecraft.level, minecraft.player.getGameProfile());
        playerRenderer = ModList.get().isLoaded("epicfight") ? new EpicFightPlayerRenderer(player) : null;
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
        buttons.add(new Button(leftPos, topPos - 10, 20, 20, new TextComponent("<"), b -> switchPage(page - 1)));
        buttons.add(new Button(leftPos + guiWidth - 20, topPos -10, 20, 20, new TextComponent(">"), b -> switchPage(page + 1)));
        buttons.add(new Button(leftPos + guiWidth / 2 - 30, topPos + guiHeight, 60, 20, new TranslatableComponent("button.dcclasses.confirm"), b -> confirm()));
        reloadSlots();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderDirtBackground(0);
        for(Widget widget : buttons) widget.render(poseStack, mouseX,mouseY, partialTicks);
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        //title
        drawBox(poseStack, leftPos + 18, topPos - 10, 131, 20);
        drawCenteredString(poseStack, minecraft.font,  new TranslatableComponent(clazz.getTranslationKey()), leftPos + guiWidth /2, topPos -4, 0x9E0CD2);
        //description
        int offset = (int)((float)(description.size() * 9)/2f);
        drawBox(poseStack, leftPos - 6, topPos + guiHeight / 2 + 48 - offset, 179, description.size() * 9 + 8);
        for (int i = 0; i < description.size(); i ++) {
            Component component = description.get(i);
            drawCenteredString(poseStack, minecraft.font,  component, leftPos + guiWidth / 2, topPos + guiHeight / 2 + 52 + i * 9 - offset, 0xFFFFFF);
        }
        //player
        int entityX = leftPos + guiWidth / 2;
        int entityY = topPos + guiHeight / 2 + 13;
        if (playerRenderer != null) playerRenderer.render(poseStack, entityX, entityY, partialTicks, clazz.getAnimation());
        else InventoryScreen.renderEntityInInventory(entityX, entityY, 38, entityX - mouseX, entityY + (player.getEyeHeight()) - mouseY, player);
        //items, skills and attributes
        if (itemHeight > 0) {
            drawBox(poseStack, itemX, topPos + 17, itemWidth, itemHeight);
            drawCenteredString(poseStack, minecraft.font, new TranslatableComponent("text.dcclasses.items"), leftPos - 10, topPos + 21, 0xFFFFFF);
        }
        if (skillHeight > 0) {
            drawBox(poseStack, skillX, topPos + 17, skillWidth, skillHeight);
            drawCenteredString(poseStack, minecraft.font, new TranslatableComponent("text.dcclasses.skills"), leftPos + guiWidth + 10, topPos + 21, 0xFFFFFF);
        }
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
    
    @Override
    public void onClose() {
        if (classes != null) classes.clear();
        super.onClose();
    }

    private void switchPage(int page) {
        this.page = Math.floorMod(page, classes.size());
        reloadEquipment();
        reloadText();
        reloadSlots();
    }
    
    private void confirm() {
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
        String str = new TranslatableComponent(clazz.getTranslationKey() + ".desc").getString();
        int position = 0;
        while (position < str.length()) {
            if (description.size() >= 7) break;
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
        if (clazz == null) return;
        List <AttributeEntry> attributes = clazz.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeEntry attribute = attributes.get(i);
            int width = minecraft.font.width(attribute.getText()) + 11;
            slots.add(new AttributeSlot(attribute, width, leftPos + 11 + (int)(((float)(guiWidth - 11) * (i + 0.25f) - width * 0.5f) / (float)attributes.size()), topPos + 11));
        }
        List<ItemEntry> items = clazz.getItems();
        int itemRows = (int)(((float)items.size() -1) / 3f) + 1;
        itemWidth = Math.max(itemRows * 18, minecraft.font.width(new TranslatableComponent("text.dcclasses.items"))) + 8;
        itemHeight = items.isEmpty() ? 0 : 21 + (int)Math.ceil((float)items.size()/(float)itemRows) * 18;
        itemX = leftPos - 10 - (int)((float)itemWidth / 2f);
        for (int i = 0; i < items.size(); i++) slots.add(new ItemSlot(items.get(i),leftPos - 26 + itemRows * 8 - i % itemRows * 18, topPos + 34 + (i / itemRows) * 18));
        if (!ModList.get().isLoaded("epicfight")) return;
        List<String> skills = EpicFightIntegration.getVerifiedSkills(clazz);
        int skillRows = (int)((float)(skills.size() -1) / 3f) + 1;
        skillWidth = Math.max(skillRows * 18, minecraft.font.width(new TranslatableComponent("text.dcclasses.skills"))) + 8;
        skillHeight = skills.isEmpty() ? 0 : 21 + (int)Math.ceil((float)skills.size()/(float)skillRows) * 18;
        skillX = leftPos + guiWidth + 10 - (int)((float)skillWidth / 2f);
        for (int i = 0; i < skills.size(); i++) slots.add(new SkillSlot(skills.get(i), leftPos + guiWidth + 10 - skillRows * 8 + i % skillRows * 18, topPos + 34 + (i / skillRows) * 18));
    }
    
    private void drawBox(PoseStack poseStack, int x, int y, int width, int height) {
        GuiUtils.drawContinuousTexturedBox(poseStack, new ResourceLocation("textures/gui/recipe_book.png"), x, y, 82, 208, width, height, 32, 32, 4, 4, 4, 4, 1);
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
