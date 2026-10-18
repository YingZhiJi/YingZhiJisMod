package com.example.examplemod;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CrystallizerCrystallizingCategory
        implements IRecipeCategory<CrystallizerCrystallizingRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public CrystallizerCrystallizingCategory(IGuiHelper helper) {
        // 不依赖贴图，用空白背景
        this.background = helper.createBlankDrawable(110, 60);
        this.icon = helper.createDrawableItemStack(
                new ItemStack(ModBlocks.CRYSTALLIZER.get()));
    }

    @Override
    public RecipeType<CrystallizerCrystallizingRecipe> getRecipeType() {
        return CrystallizerJEIPlugin.CRYSTALLIZING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.yingzhiji_s_mod.crystallizer_crystallizing");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                          CrystallizerCrystallizingRecipe recipe,
                          IFocusGroup focuses) {
        // 物品输入
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 22)
                .addIngredients(recipe.getInputItem());

        // 流体输入（用流体槽展示）
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 4)
                .addFluidStack(recipe.getInputFluid().getFluid(),
                        recipe.getInputFluid().getAmount())
                .setFluidRenderer(2400, false, 12, 52);

        // 成品输出
        builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 22)
                .addItemStack(recipe.getOutputItem());
    }

    @Override
    public void draw(CrystallizerCrystallizingRecipe recipe,
                     IRecipeSlotsView slotsView,
                     GuiGraphics gui,
                     double mouseX, double mouseY) {
        // 在中央画一个简单的箭头
        int arrowX = 58;
        int arrowY = 28;

        // 底槽
        gui.fill(arrowX, arrowY, arrowX + 20, arrowY + 4, 0xFF545454);
        // 三角箭头
        for (int i = 0; i < 4; i++) {
            gui.fill(arrowX + 18 + i, arrowY - i, arrowX + 22 + i, arrowY + 4 + i, 0xFF000000);
        }
    }
}