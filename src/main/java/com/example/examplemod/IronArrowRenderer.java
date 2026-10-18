package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IronArrowRenderer extends ArrowRenderer<IronArrow> {

    public IronArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/iron_arrow.png");
    }
}