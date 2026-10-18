package com.example.examplemod.mixin;

import com.potato.potatocharm.PotatoCharmHelper;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerDimensionsMixin {

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void yingzhiji$smallDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Player self = (Player) (Object) this;
        if (self.getInventory() == null) return;
        if (!PotatoCharmHelper.hasPotatoCharm(self)) return;

        cir.setReturnValue(EntityDimensions.fixed(0.6f, 0.4f));
    }
}