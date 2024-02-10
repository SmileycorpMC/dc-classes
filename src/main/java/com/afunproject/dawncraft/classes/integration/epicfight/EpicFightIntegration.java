package com.afunproject.dawncraft.classes.integration.epicfight;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;
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

public class EpicFightIntegration {
    
    public static List<String> getVerifiedSkills(DCClass clazz) {
        List<String> list = Lists.newArrayList();
        for (String name : clazz.getSkills()) {
            Skill skill = SkillManager.getSkill(name);
            if (skill == null) ClassesLogger.logError("Skill " + name + " is null ", new NullPointerException());
            else list.add(name);
        }
        return list;
    }
    
    public static void applySkills(DCClass clazz, ServerPlayer player) {
        ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
        if (patch != null) {
            CapabilitySkill skills = patch.getSkillCapability();
            for (String name : clazz.getSkills()) {
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
    }
    
}
