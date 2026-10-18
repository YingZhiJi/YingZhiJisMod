package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DenseDecayingArrowRenderer extends ArrowRenderer<DenseDecayingArrow> {

    public DenseDecayingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DenseDecayingArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/dense_decaying_arrow.png");
    }
}