package com.example.examplemod.mixin;

import com.example.examplemod.ZomperiumCommander;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityTotemMixin {

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"))
    private void yingzhiji$afterTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && (Object) this instanceof ZomperiumCommander commander) {
            commander.onTotemUsed();
        }
    }
}