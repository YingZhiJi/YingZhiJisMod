package com.example.examplemod;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RepairKitRecipe implements CraftingRecipe {

    public static class TargetEntry {
        public final Ingredient ingredient;
        public final int ratio;

        public TargetEntry(Ingredient ingredient, int ratio) {
            this.ingredient = ingredient;
            this.ratio = ratio;
        }
    }

    private final ResourceLocation id;
    private final Ingredient kitIngredient;
    private final List<TargetEntry> targets;

    public RepairKitRecipe(ResourceLocation id, Ingredient kit, List<TargetEntry> targets) {
        this.id = id;
        this.kitIngredient = kit;
        this.targets = targets;
    }

    public Ingredient getKitIngredient() {
        return this.kitIngredient;
    }

    public List<TargetEntry> getTargets() {
        return this.targets;
    }

    /**
     * 遍历目标列表，找到匹配的入口
     */
    private TargetEntry findTarget(ItemStack stack) {
        for (TargetEntry entry : this.targets) {
            if (entry.ingredient.test(stack)) return entry;
        }
        return null;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack kit = ItemStack.EMPTY;
        ItemStack tool = ItemStack.EMPTY;
        TargetEntry matched = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (this.kitIngredient.test(stack)) {
                if (!kit.isEmpty()) return false;
                kit = stack;
            } else {
                if (!tool.isEmpty()) return false;
                TargetEntry entry = findTarget(stack);
                if (entry == null) return false;
                tool = stack;
                matched = entry;
            }
        }

        if (kit.isEmpty() || tool.isEmpty() || matched == null) return false;
        if (kit.getDamageValue() >= kit.getMaxDamage()) return false;
        if (tool.getDamageValue() <= 0) return false;
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack kit = ItemStack.EMPTY;
        ItemStack tool = ItemStack.EMPTY;
        TargetEntry matched = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (this.kitIngredient.test(stack)) {
                kit = stack;
            } else {
                TargetEntry entry = findTarget(stack);
                if (entry != null) {
                    tool = stack;
                    matched = entry;
                }
            }
        }

        if (kit.isEmpty() || tool.isEmpty() || matched == null) return ItemStack.EMPTY;

        int missing = tool.getDamageValue();
        int kitAvailable = kit.getMaxDamage() - kit.getDamageValue();
        int ratio = matched.ratio;
        // 向上取整：剩 1 点也消耗 1 点工具包
        int kitCost = Math.min((missing + ratio - 1) / ratio, kitAvailable);
        int heal = Math.min(missing, kitCost * ratio);

        ItemStack result = tool.copy();
        result.setCount(1);
        result.setDamageValue(tool.getDamageValue() - heal);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        int kitSlot = -1;
        ItemStack kit = ItemStack.EMPTY;
        ItemStack tool = ItemStack.EMPTY;
        TargetEntry matched = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (this.kitIngredient.test(stack)) {
                kitSlot = i;
                kit = stack;
            } else {
                TargetEntry entry = findTarget(stack);
                if (entry != null) {
                    tool = stack;
                    matched = entry;
                }
            }
        }

        if (kitSlot < 0 || tool.isEmpty() || matched == null) return list;

        int missing = tool.getDamageValue();
        int kitAvailable = kit.getMaxDamage() - kit.getDamageValue();
        int ratio = matched.ratio;
        int kitCost = Math.min((missing + ratio - 1) / ratio, kitAvailable);

        if (kitCost > 0) {
            int newDamage = kit.getDamageValue() + kitCost;
            if (newDamage < kit.getMaxDamage()) {
                ItemStack remainingKit = kit.copy();
                remainingKit.setCount(1);
                remainingKit.setDamageValue(newDamage);
                list.set(kitSlot, remainingKit);
            }
        }

        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ExampleMod.REPAIR_KIT_SERIALIZER.get();
    }
}