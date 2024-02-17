package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.client.AttributeProperties;
import com.afunproject.dawncraft.classes.integration.ParaglidersIntegration;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeEntry {

    private final Attribute attribute;
    private final double value;
    private MutableComponent text;
    
    public AttributeEntry(ResourceLocation attribute, double value) throws Exception {
        this.attribute = ForgeRegistries.ATTRIBUTES.getValue(attribute);
        if (this.attribute == null) throw new NullPointerException("Attribute " + attribute + " not registered.");
        this.value = value;
    }

    public void apply(Player player) {
        ClassesLogger.logInfo("Applying attribute " + attribute.getRegistryName() + " with value " + value + " to player " + player.getDisplayName().getString());
        if (ModList.get().isLoaded("paraglider") && ParaglidersIntegration.isStamina(attribute)) ParaglidersIntegration.apply(player, value);
        else player.getAttribute(attribute).setBaseValue(value);
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public double getValue() {
        return value;
    }
    
    public ResourceLocation getName() {
        return attribute.getRegistryName();
    }
    
    public MutableComponent getText() {
        if (text == null) text = AttributeProperties.INSTANCE.getText(attribute, value);
        return text;
    }
    
    public TextColor getTextColour() {
        return AttributeProperties.INSTANCE.getTextColour(attribute);
    }
    
    @Override
    public String toString() {
        return getName() + " " + value;
    }

}
