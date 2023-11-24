package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;

public class ClassHandler {

    private static final Map<ResourceLocation, DCClass> CLASSES = Maps.newHashMap();

    public static void clear() {
        CLASSES.clear();
    }

    public static void addClass(DCClass clazz) {
        CLASSES.put(clazz.getRegistryName(), clazz);
    }

    public static DCClass getClass(ResourceLocation loc) {
        return CLASSES.get(loc);
    }

    public static Collection<DCClass> getClasses() {
        return CLASSES.values();
    }

    public static Map<ResourceLocation, DCClass> getClassMap() {
        return CLASSES;
    }

}
