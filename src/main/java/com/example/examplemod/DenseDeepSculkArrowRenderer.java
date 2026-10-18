package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DenseDeepSculkArrowRenderer extends ArrowRenderer<DenseDeepSculkArrow> {

    public DenseDeepSculkArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DenseDeepSculkArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/dense_deepsculk_arrow.png");
    }
}