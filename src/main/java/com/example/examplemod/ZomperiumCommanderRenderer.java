package com.example.examplemod;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ZomperiumCommanderRenderer extends HumanoidMobRenderer<ZomperiumCommander, ZomperiumCommanderModel<ZomperiumCommander>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("yingzhiji_s_mod", "textures/entity/zomperium_commander.png");

    public ZomperiumCommanderRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context, new ZomperiumCommanderModel<>(context.bakeLayer(layer)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ZomperiumCommander entity) {
        return TEXTURE;
    }
}