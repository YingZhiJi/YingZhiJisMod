package com.example.examplemod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public class RepairKitRecipeSerializer implements RecipeSerializer<RepairKitRecipe> {

    public static final RepairKitRecipeSerializer INSTANCE = new RepairKitRecipeSerializer();

    @Override
    public RepairKitRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient kit = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "kit"));

        List<RepairKitRecipe.TargetEntry> targets = new ArrayList<>();
        JsonArray array = GsonHelper.getAsJsonArray(json, "targets");
        for (JsonElement element : array) {
            JsonObject entryJson = element.getAsJsonObject();
            Ingredient ingredient = Ingredient.fromJson(entryJson);
            int ratio = GsonHelper.getAsInt(entryJson, "ratio", 2);
            targets.add(new RepairKitRecipe.TargetEntry(ingredient, ratio));
        }

        return new RepairKitRecipe(id, kit, targets);
    }

    @Override
    public RepairKitRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        Ingredient kit = Ingredient.fromNetwork(buffer);
        int count = buffer.readVarInt();
        List<RepairKitRecipe.TargetEntry> targets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int ratio = buffer.readVarInt();
            targets.add(new RepairKitRecipe.TargetEntry(ingredient, ratio));
        }
        return new RepairKitRecipe(id, kit, targets);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, RepairKitRecipe recipe) {
        recipe.getKitIngredient().toNetwork(buffer);
        buffer.writeVarInt(recipe.getTargets().size());
        for (RepairKitRecipe.TargetEntry entry : recipe.getTargets()) {
            entry.ingredient.toNetwork(buffer);
            buffer.writeVarInt(entry.ratio);
        }
    }
}