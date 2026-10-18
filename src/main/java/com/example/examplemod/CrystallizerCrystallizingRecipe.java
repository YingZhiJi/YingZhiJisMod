package com.example.examplemod;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class CrystallizerCrystallizingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final Ingredient inputItem;
    private final FluidStack inputFluid;
    private final ItemStack outputItem;
    private final int duration; // tick

    public CrystallizerCrystallizingRecipe(ResourceLocation id, Ingredient inputItem,
                                           FluidStack inputFluid, ItemStack outputItem,
                                           int duration) {
        this.id = id;
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.duration = duration;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return inputItem.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return outputItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) { return outputItem; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CRYSTALLIZER_CRYSTALLIZING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CRYSTALLIZER_CRYSTALLIZING.get();
    }

    public Ingredient getInputItem() { return inputItem; }
    public FluidStack getInputFluid() { return inputFluid; }
    public ItemStack getOutputItem() { return outputItem; }
    public int getDuration() { return duration; }

    public static class Serializer implements RecipeSerializer<CrystallizerCrystallizingRecipe> {

        @Override
        public CrystallizerCrystallizingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_item"));

            JsonObject fluidJson = GsonHelper.getAsJsonObject(json, "input_fluid");
            String fluidId = GsonHelper.getAsString(fluidJson, "fluid");
            int amount = GsonHelper.getAsInt(fluidJson, "amount", 500);

            FluidStack inputFluid = new FluidStack(
                    ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidId)), amount);

            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack output = new ItemStack(
                    ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation(GsonHelper.getAsString(resultJson, "item"))),
                    GsonHelper.getAsInt(resultJson, "count", 1));

            int duration = GsonHelper.getAsInt(json, "duration", 160);

            return new CrystallizerCrystallizingRecipe(id, input, inputFluid, output, duration);
        }

        @Override
        public CrystallizerCrystallizingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            FluidStack inputFluid = FluidStack.readFromPacket(buffer);
            ItemStack output = buffer.readItem();
            int duration = buffer.readVarInt();
            return new CrystallizerCrystallizingRecipe(id, input, inputFluid, output, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrystallizerCrystallizingRecipe recipe) {
            recipe.inputItem.toNetwork(buffer);
            recipe.inputFluid.writeToPacket(buffer);
            buffer.writeItem(recipe.outputItem);
            buffer.writeVarInt(recipe.duration);
        }
    }
}