package com.afunproject.dawncraft.classes.client;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class ClassSlot implements Widget {
    
    protected final int x, y, width, height;
    
    public ClassSlot(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean isMouseOver(int mouse_x, int mouse_y) {
        return mouse_x >= x && mouse_x <= x + width && mouse_y >= y && mouse_y <= y + height;
    }
    
    public abstract List<Component> getTooltip();
    
}
