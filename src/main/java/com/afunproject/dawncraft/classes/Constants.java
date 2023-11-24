package com.afunproject.dawncraft.classes;

import net.minecraft.resources.ResourceLocation;

public class Constants {

    public static final String MODID = "dawncraftclasses";

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
    public static String locStr(String path) {
        return loc(path).toString();
    }

}
