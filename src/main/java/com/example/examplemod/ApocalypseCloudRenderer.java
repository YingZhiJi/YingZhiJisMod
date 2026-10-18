package com.example.examplemod;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ApocalypseCloudRenderer extends EntityRenderer<ApocalypseCloudEntity> {

    public ApocalypseCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ApocalypseCloudEntity entity) {
        return new ResourceLocation("minecraft", "textures/particle/generic_0.png");
    }
}