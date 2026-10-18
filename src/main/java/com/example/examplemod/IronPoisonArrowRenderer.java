package com.example.examplemod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IronPoisonArrowRenderer extends ArrowRenderer<IronPoisonArrow> {

    public IronPoisonArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronPoisonArrow entity) {
        return new ResourceLocation("yingzhiji_s_mod", "textures/entity/iron_poison_arrow.png");
    }
}