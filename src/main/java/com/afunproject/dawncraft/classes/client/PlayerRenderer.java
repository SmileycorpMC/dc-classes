package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.integration.epicfight.client.EpicFightPlayerRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

public class PlayerRenderer extends AbstractWidget {

    private final RemotePlayer player;
    private final EpicFightPlayerRenderer epicFightRenderer;

    public PlayerRenderer(RemotePlayer player, int x, int y) {
        super(x, y, 0, 0, Component.empty());
        this.player = player;
        epicFightRenderer = ModList.get().isLoaded("epicfight") ? new EpicFightPlayerRenderer(player, x, y) : null;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        if (epicFightRenderer != null) epicFightRenderer.render(gui, mouseX, mouseY, partialTicks);
        else InventoryScreen.renderEntityInInventoryFollowsMouse(gui, getX(), getY(), 38, getX() - mouseX, getY() + (player.getEyeHeight()) - mouseY, player);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    public void setClass(DCClass selectedClass) {
        if (epicFightRenderer != null) epicFightRenderer.setClass(selectedClass);
    }

}
