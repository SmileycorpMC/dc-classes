package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.Constants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID)
public class ClientHandler {

    private static boolean RENDER_SCREEN = false;

    @SubscribeEvent
    public static void render(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (!RENDER_SCREEN || event.phase != TickEvent.Phase.START || mc.screen != null) return;
        RENDER_SCREEN = false;
        mc.setScreen(new ClassSelectionScreen());
    }

    public static void displayGUI() {
        RENDER_SCREEN = true;
    }

}
