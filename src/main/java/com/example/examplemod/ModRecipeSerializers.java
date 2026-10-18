package com.example.examplemod;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExampleMod.MODID);

    public static final RegistryObject<RecipeSerializer<CrystallizerCrystallizingRecipe>> CRYSTALLIZER_CRYSTALLIZING =
            SERIALIZERS.register("crystallizer_crystallizing",
                    () -> new CrystallizerCrystallizingRecipe.Serializer());
}