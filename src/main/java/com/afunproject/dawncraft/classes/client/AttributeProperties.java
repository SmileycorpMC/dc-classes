package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AttributeProperties extends SimplePreparableReloadListener<List<JsonObject>> {

    private final Map<Attribute, AttributeProperty> properties = Maps.newHashMap();
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static AttributeProperties INSTANCE = new AttributeProperties();
    
    @Override
    protected List<JsonObject> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<JsonObject> properties = Lists.newArrayList();
        try {
            for (String domain : manager.getNamespaces()) {
                ResourceLocation loc = new ResourceLocation(domain, "attributes.json");
                if (!manager.hasResource(loc)) continue;
                for (Resource resource : manager.getResources(loc)) {
                    Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                    JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    if (!json.isJsonObject()) continue;
                    properties.add(json.getAsJsonObject());
                }
            }
        } catch (Exception e) {
            ClassesLogger.logErrorCompletable("Exception Loading properties", e);
        }
        return properties;
    }
    
    @Override
    protected void apply(List<JsonObject> objects, ResourceManager manager, ProfilerFiller profiler) {
        properties.clear();
        for (JsonObject obj : objects) {
            for (String attribute : obj.keySet()) {
                try {
                    properties.put(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attribute)), new AttributeProperty(obj.get(attribute).getAsJsonObject()));
                    ClassesLogger.logInfo("added property " + attribute + " as " + obj.get(attribute).getAsJsonObject());
                } catch (Exception e) {
                    ClassesLogger.logErrorCompletable("Exception Loading properties", e);
                }
            }
        }
    }
    
    public MutableComponent getText(Attribute attribute, double value) {
        if (properties.containsKey(attribute)) {
            AttributeProperty property = properties.get(attribute);
            double scaledValue = value / property.getBase();
            return new TextComponent(property.getMode() == AttributeProperty.DisplayMode.PERCENTAGE ? (String.format("%.2f", scaledValue * 100f) + "%") : String.format("%.2f", scaledValue));
        }
        return new TextComponent(String.format("%.2f",value));
    }
    
    public TextColor getTextColour(Attribute attribute) {
        return properties.containsKey(attribute) ? properties.get(attribute).getTextColour() : TextColor.fromRgb(0xFFFFFF);
    }
    
}
