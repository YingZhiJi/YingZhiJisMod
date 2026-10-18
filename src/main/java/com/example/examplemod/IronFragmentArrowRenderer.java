package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IronFragmentArrowRenderer extends ArrowRenderer<IronFragmentArrow> {

    public IronFragmentArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronFragmentArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/iron_fragment_arrow.png");
    }
}