package com.example.examplemod.mixin;

import com.example.examplemod.BreachBladeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(
            method = "canEnchant(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void yzj$allowBreachBladeEnchantments(ItemStack stack,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BreachBladeItem) {
            Enchantment self = (Enchantment) (Object) this;
            EnchantmentCategory cat = self.category;
            if (cat == EnchantmentCategory.WEAPON
                    || cat == EnchantmentCategory.DIGGER
                    || cat == EnchantmentCategory.BREAKABLE
                    || cat == EnchantmentCategory.VANISHABLE) {
                cir.setReturnValue(true);
            }
        }
    }
}