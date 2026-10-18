package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FlyingZombieHeadRenderer extends EntityRenderer<FlyingZombieHeadEntity> {

    private static final ItemStack STACK = new ItemStack(Items.ZOMBIE_HEAD);

    public FlyingZombieHeadRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FlyingZombieHeadEntity entity) {
        return new ResourceLocation("minecraft", "textures/item/zombie_head.png");
    }

    @Override
    public void render(FlyingZombieHeadEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.25, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.tickCount * 12.0F));

        Minecraft.getInstance().getItemRenderer().renderStatic(
                STACK,
                ItemDisplayContext.FIXED,
                0xF000F0,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();
    }
}