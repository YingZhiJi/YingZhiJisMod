package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IronCoolingArrowRenderer extends ArrowRenderer<IronCoolingArrow> {

    public IronCoolingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronCoolingArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/iron_cooling_arrow.png");
    }
}