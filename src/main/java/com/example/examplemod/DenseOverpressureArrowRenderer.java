package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DenseOverpressureArrowRenderer extends ArrowRenderer<DenseOverpressureArrow> {

    public DenseOverpressureArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DenseOverpressureArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/dense_overpressure_arrow.png");
    }
}