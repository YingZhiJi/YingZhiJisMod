package com.example.examplemod.mixin;

import com.potato.potatocharm.PotatoCharmHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class PotatoCharmMinecraftMixin {

    @Unique private float yingzhiji$savedYaw = 0f;
    @Unique private float yingzhiji$savedPitch = 0f;
    @Unique private int yingzhiji$savedSlot = 0;
    @Unique private boolean yingzhiji$hasSnapshot = false;
    @Unique private int yingzhiji$graceTicks = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void yingzhiji$capture(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!PotatoCharmHelper.hasPotatoCharm(mc.player)) {
            yingzhiji$hasSnapshot = false;
            return;
        }

        if (!mc.player.isDeadOrDying()) {
            yingzhiji$savedYaw = mc.player.getYRot();
            yingzhiji$savedPitch = mc.player.getXRot();
            yingzhiji$savedSlot = mc.player.getInventory().selected;
            yingzhiji$hasSnapshot = true;
        } else {
            yingzhiji$graceTicks = 60;
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void yingzhiji$restore(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !yingzhiji$hasSnapshot) return;
        if (!PotatoCharmHelper.hasPotatoCharm(mc.player)) return;

        if (mc.player.isDeadOrDying() || yingzhiji$graceTicks > 0) {
            mc.player.setYRot(yingzhiji$savedYaw);
            mc.player.setXRot(yingzhiji$savedPitch);
            mc.player.yRotO = yingzhiji$savedYaw;
            mc.player.xRotO = yingzhiji$savedPitch;
            mc.player.getInventory().selected = yingzhiji$savedSlot;
            if (yingzhiji$graceTicks > 0) yingzhiji$graceTicks--;
        }
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void yingzhiji$cancelDeathScreen(Screen screen, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (screen instanceof DeathScreen && mc.player != null
                && PotatoCharmHelper.hasPotatoCharm(mc.player)) {
            ci.cancel();
        }
    }
}