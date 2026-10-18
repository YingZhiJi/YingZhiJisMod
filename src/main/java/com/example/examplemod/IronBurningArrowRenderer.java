package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IronBurningArrowRenderer extends ArrowRenderer<IronBurningArrow> {

    public IronBurningArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronBurningArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/iron_burning_arrow.png");
    }
}