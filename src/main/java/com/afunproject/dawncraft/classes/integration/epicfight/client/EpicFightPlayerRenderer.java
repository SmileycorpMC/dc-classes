package com.afunproject.dawncraft.classes.integration.epicfight.client;

import com.afunproject.dawncraft.classes.data.DCClass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private final int x, y;
    private float xOffset = 0, yOffset = 0;

    public EpicFightPlayerRenderer(RemotePlayer player, int x, int y) {
        this.player = player;
        playerpatch = new AnimationPlayerPatch(player);
        renderer = (PPlayerRenderer) ClientEngine.getInstance().renderEngine.getEntityRenderer(EntityType.PLAYER);
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        playerpatch.update();
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher renderDispatcher = mc.getEntityRenderDispatcher();
        float f1 = (float)Math.atan(0 / 40f);
        PoseStack posestack = gui.pose();
        posestack.pushPose();
        posestack.translate(x + xOffset, y + yOffset, 1050);
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

    public void setClass(DCClass selectedClass) {
        xOffset = selectedClass.getXOffset();
        yOffset = selectedClass.getYOffset();;
        AnimationManager.AnimationAccessor<StaticAnimation> anim = AnimationManager.getInstance().byKey(new ResourceLocation(selectedClass.getAnimation()));
        if (anim != null) {
            ClientAnimator animator = playerpatch.getClientAnimator();
            animator.resetMotion(true);
            animator.playAnimationInstantly(anim);
            anim.get().begin(playerpatch);
        }

    }

}
