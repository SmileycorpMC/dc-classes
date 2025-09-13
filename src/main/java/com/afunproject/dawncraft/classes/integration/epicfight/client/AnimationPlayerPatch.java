package com.afunproject.dawncraft.classes.integration.epicfight.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.types.ToolHolderArmature;

public class AnimationPlayerPatch extends AbstractClientPlayerPatch<AbstractClientPlayer> {
    
    public AnimationPlayerPatch(RemotePlayer player) {
        original = player;
        armature = Armatures.getArmatureFor(this);
        animator = new Animator(this);
        animator.getVariables().putDefaultSharedVariable(AttackAnimation.ATTACK_TRIED_ENTITIES);
        animator.getVariables().putDefaultSharedVariable(AttackAnimation.ACTUALLY_HIT_ENTITIES);
        animator.getVariables().putDefaultSharedVariable(ActionAnimation.ACTION_ANIMATION_COORD);
        if (armature instanceof ToolHolderArmature toolArmature) {
            this.setParentJointOfHand(InteractionHand.MAIN_HAND, toolArmature.rightToolJoint());
            this.setParentJointOfHand(InteractionHand.OFF_HAND, toolArmature.leftToolJoint());
        }
        animator.postInit();
    }
    
    public void update() {
        animator.tick();
        getCurrentAnimation().ifPresent(anim -> anim.tick(this));
    }
    
    public AssetAccessor<? extends DynamicAnimation> getCurrentAnimation() {
        return ((Animator)animator).getCurrentAnimation();
    }
    
    public static class Animator extends ClientAnimator {
        public Animator(AnimationPlayerPatch playerPatch) {
            super(playerPatch);
            livingAnimations.put(LivingMotions.IDLE, Animations.BIPED_IDLE);
        }
        
        @Override
        public void tick() {
            playAnimation(getLivingMotion(entitypatch.currentLivingMotion), 0f);
        }
        
        public AssetAccessor<? extends DynamicAnimation> getCurrentAnimation() {
            return baseLayer.animationPlayer.getAnimation();
        }
        
    }
}
