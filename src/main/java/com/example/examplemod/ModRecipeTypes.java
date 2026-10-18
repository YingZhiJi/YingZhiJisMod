package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ExampleMod.MODID);

    public static final RegistryObject<RecipeType<CrystallizerCrystallizingRecipe>> CRYSTALLIZER_CRYSTALLIZING =
            RECIPE_TYPES.register("crystallizer_crystallizing",
                    () -> RecipeType.simple(
                            new ResourceLocation(ExampleMod.MODID, "crystallizer_crystallizing")));
}