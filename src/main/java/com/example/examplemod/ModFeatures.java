package com.example.examplemod;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, ExampleMod.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ASHBLAZITE_ORE =
            FEATURES.register("ashblazite_ore",
                    () -> new AshblaziteOreFeature(NoneFeatureConfiguration.CODEC));
}