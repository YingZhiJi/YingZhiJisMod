package com.example.examplemod;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class CrystallizerJEIPlugin implements IModPlugin {

    public static final ResourceLocation UID =
            new ResourceLocation("yingzhiji_s_mod", "jei_plugin");

    public static final RecipeType<CrystallizerCrystallizingRecipe> CRYSTALLIZING_TYPE =
            RecipeType.create("yingzhiji_s_mod", "crystallizer_crystallizing",
                    CrystallizerCrystallizingRecipe.class);

    public static IJeiRuntime runtime;

    @Override
    public ResourceLocation getPluginUid() { return UID; }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CrystallizerCrystallizingCategory(helper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Minecraft.getInstance().level == null
                ? null : Minecraft.getInstance().level.getRecipeManager();
        if (rm == null) return;

        List<CrystallizerCrystallizingRecipe> recipes = rm.getAllRecipesFor(
                ModRecipeTypes.CRYSTALLIZER_CRYSTALLIZING.get());
        registration.addRecipes(CRYSTALLIZING_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRYSTALLIZER.get()),
                CRYSTALLIZING_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrystallizerScreen.class,
                52, 43, 78, 6, CRYSTALLIZING_TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }
}