package com.example.examplemod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModTags {

    public static class Entities {
        // Z 帝国阵营标签
        public static final TagKey<EntityType<?>> ZOMPERIUM =
                TagKey.create(Registries.ENTITY_TYPE,
                        new ResourceLocation("yingzhiji_s_mod", "zomperium"));
    }
}