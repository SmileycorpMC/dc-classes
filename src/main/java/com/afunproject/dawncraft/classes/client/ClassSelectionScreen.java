package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.integration.CuriosIntegration;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClassSelectionScreen extends Screen {

    private int page = 0;
    private final List<DCClass> classes;
    private final RemotePlayer player;
    private final List<Button> buttons = Lists.newArrayList();
    private int i;

    public ClassSelectionScreen(List<DCClass> cache) {
        super(new TranslatableComponent("title.dcclasses.screen"));
        Minecraft minecraft = Minecraft.getInstance();
        classes = cache.stream().sorted(Comparator.comparingInt(DCClass::getIndex)).collect(Collectors.toList());
        player = new RemotePlayer(minecraft.level, minecraft.player.getGameProfile());
        reloadEquipment();
        buttons.add(new Button(width/2 + 150, height / 2 + 50, 20, 20, new TextComponent("<"), b -> switchPage(page - 1)));
        buttons.add(new Button(width/2 + 350, height / 2 + 50, 20, 20, new TextComponent(">"), b -> switchPage(page + 1)));
        buttons.add(new Button(width/2 + 230, height / 2 + 180, 60, 20, new TranslatableComponent("button.dawncraft.confirm"), b -> confirm()));
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderDirtBackground(0);
        for(Widget widget : buttons) widget.render(poseStack, mouseX,mouseY, partialTicks);
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        drawCenteredString(poseStack, Minecraft.getInstance().font,  new TranslatableComponent(clazz.getTranslationKey()), width/2, height/2 - 75, 0x9E0CD2);
        int entityX = width/2;
        int entityY = height/2 + 50;
        if (i++ % 40 == 0) {
            LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
            patch.getAnimator().playAnimation(EpicFightMod.getInstance().animationManager
                    .findAnimationByPath(getSelectedClass().getAnimation()), 5);
        }
        InventoryScreen.renderEntityInInventory(entityX, entityY, 40, entityX - mouseX, entityY + (player.getEyeHeight()) - mouseY, player);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_95587_) {
        for (Button button : buttons) if (button.isMouseOver(mouseX, mouseY)) return button.mouseClicked(mouseX, mouseY, p_95587_);
        return false;
    }

    private void switchPage(int page) {
        this.page = Math.floorMod(page, classes.size());
        reloadEquipment();
    }

    private void reloadEquipment() {
        DCClass clazz = getSelectedClass();
        if (clazz == null) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) player.setItemSlot(slot, ItemStack.EMPTY);
        player.getInventory().clearContent();
        if (ModList.get().isLoaded("curios")) CuriosIntegration.clear(player);
        clazz.setVisualEquipment(player);
    }

    private void confirm() {
        NetworkHandler.NETWORK_INSTANCE.sendTo(new PickClassMessage(getSelectedClass().getRegistryName()),
                Minecraft.getInstance().player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
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
