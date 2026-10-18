package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IronSpectralArrowRenderer extends ArrowRenderer<IronSpectralArrow> {

    public IronSpectralArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronSpectralArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/iron_spectral_arrow.png");
    }
}