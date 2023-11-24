package com.afunproject.dawncraft.classes.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import tictim.paraglider.contents.Contents;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

import java.util.List;
import java.util.UUID;

public class DCClass {

    private static final UUID HEALTH_MOD = UUID.fromString("7c72b3e9-f0d1-4505-9581-ae5115765736");
    private static final UUID STAMINA_MOD = UUID.fromString("24303c22-8b8d-445f-907e-c6f8e6b20d97");

    private final ResourceLocation name;
    private final int index;
    private final float health;
    private final int stamina;
    private final List<Skill> skills = Lists.newArrayList();
    private final List<ItemEntry> items = Lists.newArrayList();

    public DCClass(ResourceLocation name, JsonObject obj) throws Exception {
        this.name = name;
        index = obj.has("index") ? obj.get("index").getAsInt() : 99;
        health = obj.has("health") ? obj.get("health").getAsFloat() : 20;
        stamina = obj.has("stamina") ? obj.get("stamina").getAsInt() : 28;
        if (obj.has("starting_skills")) for (JsonElement element : obj.getAsJsonArray("starting_skills"))
            skills.add(SkillManager.getSkill(element.getAsString()));
        if (obj.has("items")) for (JsonElement element : obj.getAsJsonArray("items")) {
            try {
                items.add(new ItemEntry(element.getAsJsonObject()));
            } catch (Exception e) {}
        }
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    public String getTranslationKey() {
        return "class." + name.toString().replace(":", ".");
    }

    public void apply(Player player) {
        player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HEALTH_MOD,
                "class_health", health - 20, AttributeModifier.Operation.ADDITION));
        player.getAttribute(Contents.MAX_STAMINA.get()).addPermanentModifier(new AttributeModifier(STAMINA_MOD,
                "class_stamina", health - 28, AttributeModifier.Operation.ADDITION));
        LazyOptional<CapabilitySkill> skillcap = player.getCapability(EpicFightCapabilities.CAPABILITY_SKILL);
        if (skillcap.isPresent()) {
            CapabilitySkill skills = skillcap.orElse(null);
            for (Skill skill : this.skills) skills.addLearnedSkill(skill);
        }
        for (ItemEntry item : items) item.apply(player);
    }

    public int getIndex() {
        return index;
    }

    public List<ItemEntry> getItems() {
        return items;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("index", index);
        obj.addProperty("health", health);
        obj.addProperty("stamina", stamina);
        JsonArray skills = new JsonArray();
        for (Skill skill : this.skills) skills.add(skill.toString());
        obj.add("skills", skills);
        JsonArray items = new JsonArray();
        for (ItemEntry item : this.items) items.add(item.serialize());
        obj.add("items", items);
        return obj;
    }


}
