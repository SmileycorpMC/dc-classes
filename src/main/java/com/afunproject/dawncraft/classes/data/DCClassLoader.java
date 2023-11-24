package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class DCClassLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static DCClassLoader INSTANCE = new DCClassLoader();

    public DCClassLoader() {
        super(GSON, "classes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiller) {
        ClassHandler.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            try {
               ClassHandler.addClass(new DCClass(entry.getKey(), (JsonObject) entry.getValue()));
            } catch (Exception e) {}
        }
    }

}
