package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.contents.Contents;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPAddLearnedSkill;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DCClass {

    private static final UUID HEALTH_MOD = UUID.fromString("7c72b3e9-f0d1-4505-9581-ae5115765736");
    private static final UUID STAMINA_MOD = UUID.fromString("24303c22-8b8d-445f-907e-c6f8e6b20d97");

    private final ResourceLocation name;
    private final int index;
    private final float health;
    private final int stamina;
    private final List<String> skills = Lists.newArrayList();
    private final List<ItemEntry> items = Lists.newArrayList();
    private final String animation;

    public DCClass(ResourceLocation name, JsonObject obj) throws Exception {
        this.name = name;
        index = obj.has("index") ? obj.get("index").getAsInt() : 99;
        health = obj.has("health") ? obj.get("health").getAsFloat() : 20;
        stamina = obj.has("stamina") ? obj.get("stamina").getAsInt() : 28;
        if (obj.has("starting_skills")) for (JsonElement element : obj.getAsJsonArray("starting_skills"))
            skills.add(element.getAsString());
        if (obj.has("items")) for (JsonElement element : obj.getAsJsonArray("items")) {
            try {
                items.add(new ItemEntry(element.getAsJsonObject()));
            } catch (Exception e) {
                ClassesLogger.logError("Error adding item " + element.toString(), e);
            }
        }
        animation = obj.has("animation") ? obj.get("animation").getAsString()
                : "epicfight:biped/combat/sword_auto1";
    }

    public float getHealthMult() {
        return health * 0.5f;
    }

    public float getStaminaMult() {
        return ((float)stamina) / 28f;
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

    public void apply(ServerPlayer player) {
        ClassesLogger.logInfo("Applying " + this + " modififers to player " + player.getDisplayName().getString());
        player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HEALTH_MOD,
                "class_health", health - 20, AttributeModifier.Operation.ADDITION));
        player.setHealth(player.getMaxHealth());
        player.getAttribute(Contents.MAX_STAMINA.get()).addPermanentModifier(new AttributeModifier(STAMINA_MOD,
                "class_stamina", stamina - 28, AttributeModifier.Operation.ADDITION));
        ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
        if (patch != null) {
            CapabilitySkill skills = patch.getSkillCapability();
            for (String name : this.skills) {
                ClassesLogger.logInfo("Applying skill " + name + " to player " + player.getDisplayName().getString());
                Skill skill = SkillManager.getSkill(name);
                if (skill == null) {
                    ClassesLogger.logError("Skill " + name + " is null", new NullPointerException());
                    continue;
                }
                SkillSlot slot = skills.getSkillContainersFor(skill.getCategory())
                        .iterator().next().getSlot();
                if (skill.getCategory().learnable()) skills.addLearnedSkill(skill);
                SkillContainer container = patch.getSkill(slot.universalOrdinal());
                container.setSkill(skill);
                EpicFightNetworkManager.sendToPlayer(new SPAddLearnedSkill(skill.toString()), player);
                EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(slot, skill.toString(), SPChangeSkill.State.ENABLE), player);
            }
        } else ClassesLogger.logInfo("Patch is null");
        for (ItemEntry item : items) item.apply(player);
        ClassesLogger.logInfo("Set player " + player.getDisplayName().getString() + " to class " + this);
    }

    public int getIndex() {
        return index;
    }

    public List<ItemEntry> getItems() {
        return items;
    }

    public List<Skill> getSkills() {
        return skills.stream().map(SkillManager::getSkill).collect(Collectors.toList());
    }

    public void setVisualEquipment(Player player) {
        for (ItemEntry item : items) item.apply(player);
    }

    public String getAnimation() {
        return animation;
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("index", index);
        obj.addProperty("health", health);
        obj.addProperty("stamina", stamina);
        JsonArray skills = new JsonArray();
        for (String skill : this.skills) skills.add(new JsonPrimitive(skill));
        obj.add("starting_skills", skills);
        JsonArray items = new JsonArray();
        for (ItemEntry item : this.items) items.add(item.serialize());
        obj.add("items", items);
        obj.addProperty("animation", animation);
        return obj;
    }

}
