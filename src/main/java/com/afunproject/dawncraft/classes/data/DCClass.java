package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DCClass {

    private final ResourceLocation name;
    private final int index;
    private final List<String> skills = Lists.newArrayList();
    private final List<ItemEntry> items = Lists.newArrayList();
    private final List<AttributeEntry> attributes = Lists.newArrayList();
    private final String animation;
    
    private final float xOffset, yOffset;

    public DCClass(ResourceLocation name, JsonObject obj) throws Exception {
        this.name = name;
        index = obj.has("index") ? obj.get("index").getAsInt() : 99;
        if (obj.has("starting_skills")) for (JsonElement element : obj.getAsJsonArray("starting_skills"))
            skills.add(element.getAsString());
        if (obj.has("items")) for (JsonElement element : obj.getAsJsonArray("items")) {
            try {
                items.add(new ItemEntry(element.getAsJsonObject()));
            } catch (Exception e) {
                ClassesLogger.logError("Error adding item " + element.toString(), e);
            }
        }
        JsonObject aobj = obj.getAsJsonObject("attributes");
        if (obj.has("attributes")) for (String element : aobj.keySet()) {
            try {
               attributes.add(new AttributeEntry(new ResourceLocation(element), aobj.get(element).getAsDouble()));
            } catch (Exception e) {
                ClassesLogger.logError("Error adding attribute " + element, e);
            }
        }
        animation = obj.has("animation") ? obj.get("animation").getAsString()
                : "epicfight:biped/combat/sword_auto1";
        if (obj.has("offset")) {
            JsonArray offset = obj.getAsJsonArray("offset");
            if (offset.size() >= 2) {
                xOffset = offset.get(0).getAsFloat();
                yOffset = offset.get(1).getAsFloat();
                return;
            }
        }
        xOffset = yOffset = 0;
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public String toString() {
        return getRegistryName().toString();
    }

    public String getTranslationKey() {
        return "class." + name.toString().replace(":", ".");
    }

    public void applyStatModifiers(Player player) {
        ClassesLogger.logInfo("Applying " + this + " modififers to player " + player.getDisplayName().getString());
        for (AttributeEntry entry : attributes) entry.apply(player);
    }

    public void addItems(Player player) {
        for (ItemEntry item : items) item.apply(player);
    }

    public int getIndex() {
        return index;
    }

    public List<ItemEntry> getItems() {
        return items;
    }
    
    public List<AttributeEntry> getAttributes() {
        return attributes;
    }
    
    public List<String> getSkills() {
        return skills;
    }

    public void setVisualEquipment(Player player) {
        for (ItemEntry item : items) item.apply(player);
    }

    public String getAnimation() {
        return animation;
    }
    
    public float getXOffset() {
        return xOffset;
    }
    
    public float getYOffset() {
        return yOffset;
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("index", index);
        JsonArray skills = new JsonArray();
        for (String skill : this.skills) skills.add(new JsonPrimitive(skill));
        obj.add("starting_skills", skills);
        JsonArray items = new JsonArray();
        for (ItemEntry item : this.items) items.add(item.serialize());
        obj.add("items", items);
        JsonObject attributes = new JsonObject();
        for (AttributeEntry entry : this.attributes) attributes.addProperty(entry.getName().toString(), entry.getValue());
        obj.add("attributes", attributes);
        obj.addProperty("animation", animation);
        JsonArray offset = new JsonArray();
        offset.add(xOffset);
        offset.add(yOffset);
        obj.add("offset", offset);
        ClassesLogger.logInfo("Serialized class " + name + " as " + obj);
        return obj;
    }

}
