package com.example.examplemod;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ZomperiumScoutRenderer extends HumanoidMobRenderer<ZomperiumScout, ZomperiumScoutModel<ZomperiumScout>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("yingzhiji_s_mod", "textures/entity/zomperium_scout.png");

    public ZomperiumScoutRenderer(EntityRendererProvider.Context context) {
        super(context, new ZomperiumScoutModel<>(context.bakeLayer(ZomperiumScoutModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ZomperiumScout entity) {
        return TEXTURE;
    }
}