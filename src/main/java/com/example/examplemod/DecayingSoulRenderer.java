package com.example.examplemod;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DecayingSoulRenderer extends EntityRenderer<DecayingSoulEntity> {

    public DecayingSoulRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DecayingSoulEntity entity) {
        return new ResourceLocation("minecraft", "textures/particle/generic_0.png");
    }
}