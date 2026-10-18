package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DensePiercingArrowRenderer extends ArrowRenderer<DensePiercingArrow> {

    public DensePiercingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DensePiercingArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/dense_piercing_arrow.png");
    }
}