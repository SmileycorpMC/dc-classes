package com.afunproject.dawncraft.classes.integration.epicfight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightPlayerRenderer {
    
    private final RemotePlayer player;
    private final AnimationPlayerPatch playerpatch;
    private final PPlayerRenderer renderer = new PPlayerRenderer();
    
    public EpicFightPlayerRenderer(RemotePlayer player) {
        this.player = player;
        playerpatch = new AnimationPlayerPatch(player);
    }
    
    public void render(PoseStack posestack, int x, int y, float partialTicks, String animation) {
        ClientAnimator animator = playerpatch.getClientAnimator();
        StaticAnimation anim = EpicFightMod.getInstance().animationManager.findAnimationByPath(animation);
        if (anim == null) anim = Animations.BIPED_IDLE;
        if (playerpatch.getCurrentAnimation() != anim) {
            animator.resetMotion();
            animator.playAnimation(anim, 0.5f);
            anim.begin(playerpatch);
        }
        playerpatch.update();
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher renderDispatcher = mc.getEntityRenderDispatcher();
        float f1 = (float)Math.atan(0 / 40.0F);
        posestack.pushPose();
        posestack.translate(x, y, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        posestack.pushPose();
        posestack.translate(0.0D, 0.0D, 1000.0D);
        posestack.scale(38, 38, 38);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        Quaternion quaternion2 = Vector3f.YP.rotationDegrees(180.0F);
        quaternion.mul(quaternion1);
        quaternion.mul(quaternion2);
        posestack.mulPose(quaternion);
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        renderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() ->
        renderer.render(player, playerpatch,
                (LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) renderDispatcher.getRenderer(player),
                buffers, posestack, 15728880, partialTicks));
        buffers.endBatch();
        renderDispatcher.setRenderShadow(true);
        posestack.popPose();
        posestack.popPose();
    }
    
}
