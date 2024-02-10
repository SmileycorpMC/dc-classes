package com.afunproject.dawncraft.classes.client;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.TextColor;

import java.util.Locale;

public class AttributeProperty {
    
    private final double base;
    private final DisplayMode mode;
    private final TextColor textColour;
    
    public AttributeProperty(JsonObject obj) {
        base = obj.get("base_value").getAsDouble();
        mode = DisplayMode.fromString(obj.get("display_mode").getAsString());
        textColour = obj.has("text_colour") ? TextColor.parseColor(obj.get("text_colour").getAsString()) : null;
    }
    
    public double getBase() {
        return base;
    }
    
    public DisplayMode getMode() {
        return mode;
    }
    
    public TextColor getTextColour() {
        return textColour == null ? TextColor.fromRgb(0xFFFFFF) : textColour;
    }
    
    public enum DisplayMode {
        DECIMAL,
        PERCENTAGE;
        
        public static DisplayMode fromString(String name) {
            return valueOf(name.toUpperCase(Locale.US));
        }
        
    }
    
}
