package com.afunproject.dawncraft.classes.integration.epicfight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.gameasset.Animations;

public class EpicFightPlayerRenderer {
    
    private final RemotePlayer player;
    private final AnimationPlayerPatch playerpatch;
    private final PPlayerRenderer renderer;
    
    public EpicFightPlayerRenderer(RemotePlayer player) {
        this.player = player;
        playerpatch = new AnimationPlayerPatch(player);
        renderer = (PPlayerRenderer) ClientEngine.getInstance().renderEngine.getEntityRenderer(EntityType.PLAYER);
    }
    
    public void render(PoseStack posestack, float x, float y, float partialTicks, String animation) {
        ClientAnimator animator = playerpatch.getClientAnimator();
        AnimationManager.AnimationAccessor<StaticAnimation> anim = AnimationManager.getInstance().byKey(new ResourceLocation(animation));
        if (anim == null) anim = Animations.BIPED_IDLE;
        if (playerpatch.getCurrentAnimation() != anim) {
            animator.resetMotion(true);
            animator.playAnimation(anim, 0.5f);
            anim.get().begin(playerpatch);
        }
        playerpatch.update();
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher renderDispatcher = mc.getEntityRenderDispatcher();
        float f1 = (float)Math.atan(0 / 40f);
        posestack.pushPose();
        posestack.translate(x, y, 1050);
        posestack.scale(1, 1, -1);
        RenderSystem.applyModelViewMatrix();
        posestack.pushPose();
        posestack.translate(0.0D, 0.0D, 1000);
        posestack.scale(38, 38, 38);
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20f);
        Quaternionf quaternion2 = Axis.YP.rotationDegrees(180);
        quaternion.mul(quaternion1);
        quaternion.mul(quaternion2);
        posestack.mulPose(quaternion);
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        renderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() ->
        renderer.render(player, playerpatch, (PlayerRenderer) renderDispatcher.getRenderer(player),
                buffers, posestack, 15728880, partialTicks));
        buffers.endBatch();
        renderDispatcher.setRenderShadow(true);
        posestack.popPose();
        posestack.popPose();
    }
    
}
