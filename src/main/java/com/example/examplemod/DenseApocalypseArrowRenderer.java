package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DenseApocalypseArrowRenderer extends ArrowRenderer<DenseApocalypseArrow> {

    public DenseApocalypseArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DenseApocalypseArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/dense_apocalypse_arrow.png");
    }
}