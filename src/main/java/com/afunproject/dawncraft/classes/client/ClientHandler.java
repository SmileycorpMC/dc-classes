package com.afunproject.dawncraft.classes.client;

import net.minecraft.client.Minecraft;

public class ClientHandler {

    public static void displayGUI() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ClassSelectionScreen());
    }

}
