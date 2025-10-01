package com.afunproject.dawncraft.classes.integration.epicfight.client;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.physics.cloth.ClothSimulatable;
import yesman.epicfight.api.client.physics.cloth.ClothSimulator;
import yesman.epicfight.api.physics.PhysicsSimulator;
import yesman.epicfight.api.physics.SimulatableObject;
import yesman.epicfight.api.physics.SimulationTypes;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Optional;

public class AnimationPlayerPatch extends AbstractClientPlayerPatch implements SimulatableObject, ClothSimulatable {
    
    public AnimationPlayerPatch(RemotePlayer player) {
        original = player;
        armature = Armatures.BIPED.get().deepCopy();
        animator = new ClassPlayerAnimator(this);
    }
    
    public AssetAccessor<? extends DynamicAnimation> getCurrentAnimation() {
        return ((ClassPlayerAnimator)animator).getCurrentAnimation();
    }

    @Nullable
    @Override
    public Animator getSimulatableAnimator() {
        return animator;
    }

    @Override
    public boolean invalid() {
        return false;
    }

    @Override
    public Vec3 getObjectVelocity() {
        return Vec3.ZERO;
    }

    @Override
    public Vec3 getAccurateCloakLocation(float partialFrame) {
        return null;
    }

    @Override
    public Vec3 getAccuratePartialLocation(float partialFrame) {
        return Vec3.ZERO;
    }

    @Override
    public float getAccurateYRot(float partialFrame) {
        return 0;
    }

    @Override
    public float getYRotDelta(float partialFrame) {
        return 0;
    }

    @Override
    public float getScale() {
        return 1;
    }

    @Override
    public float getGravity() {
        return 9.8f;
    }

    @Override
    public void updateMotion(boolean considerInaction) {}

    @Override
    public Faction getFaction() {
        return null;
    }

    @Override
    public boolean isLogicalClient() {
        return true;
    }

    public void update() {
        entityDecorations.tick();
        animator.tick();
    }

    public static class ClassPlayerAnimator extends ClientAnimator {

        public ClassPlayerAnimator(AnimationPlayerPatch playerPatch) {
            super(playerPatch);
            livingAnimations.put(LivingMotions.IDLE, Animations.BIPED_IDLE);
        }
        
        @Override
        public void tick() {
            AssetAccessor<? extends StaticAnimation> animation = getLivingMotion(entitypatch.currentLivingMotion);
            if (getCurrentAnimation() != animation) playAnimationInstantly(animation);
        }
        
        public AssetAccessor<? extends DynamicAnimation> getCurrentAnimation() {
            return baseLayer.animationPlayer.getAnimation();
        }
        
    }
}
