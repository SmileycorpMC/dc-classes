package com.afunproject.dawncraft.classes.integration;

import com.afunproject.dawncraft.classes.ClassesLogger;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.ModCfg;
import tictim.paraglider.contents.Contents;

import java.lang.reflect.Method;

public class ParaglidersIntegration {
    
    private static Method startingStamina;
    
    public static boolean isStamina(Attribute attribute) {
        return attribute == Contents.MAX_STAMINA.get();
    }
    
    //this function sucks, but we have to do it to keep the mod compatible with paragliders and double stamina paragliders
    public static void apply(Player player, double value) {
        double max = 0;
        try {
            if (startingStamina == null) startingStamina = ModCfg.class.getMethod("startingStamina");
            max = (double) startingStamina.invoke(null);
        } catch (Exception e) {
            ClassesLogger.logError("error adding stamina ", e);
        }
        ClassesLogger.logInfo("Attribute is paraglider:max_stamina, removing " + max + " points as default.");
        player.getAttribute(Contents.MAX_STAMINA.get()).setBaseValue(value - max);
    }
    
}
