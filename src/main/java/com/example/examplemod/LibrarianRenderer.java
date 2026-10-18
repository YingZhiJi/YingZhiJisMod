package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LibrarianRenderer extends HumanoidMobRenderer<LibrarianEntity, LibrarianModel<LibrarianEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("yingzhiji_s_mod", "textures/entity/librarian.png");

    private static final float SCALE = 0.9375F;
    private static final float SQUISH_AMOUNT = 0.1F;

    public LibrarianRenderer(EntityRendererProvider.Context context) {
        super(context, new LibrarianModel<>(context.bakeLayer(LibrarianModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    protected void scale(LibrarianEntity entity, PoseStack poseStack, float partialTick) {
        int sq = entity.getSquishTicks();
        if (sq > 0) {
            float t = 1.0F - sq / (float) LibrarianEntity.SQUISH_DURATION;
            float curve = (float) Math.sin(Math.PI * Math.pow(t, 0.6));
            float hMul = 1.0F - SQUISH_AMOUNT * curve;
            poseStack.scale(1.0F, hMul, 1.0F);
        }
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(LibrarianEntity entity) {
        return TEXTURE;
    }
}