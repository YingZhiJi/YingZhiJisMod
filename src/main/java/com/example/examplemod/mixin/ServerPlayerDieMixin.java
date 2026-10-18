package com.example.examplemod.mixin;

import com.potato.potatocharm.PotatoCharmHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerDieMixin {

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void yingzhiji$cancelDie(DamageSource source, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (!PotatoCharmHelper.hasPotatoCharm(self)) return;

        // 虚空：放过，走超级重生弹回 Y312
        // 1.20.1 里虚空伤害的 msgId 就是 "outOfWorld"
        if ("outOfWorld".equals(source.getMsgId())) return;

        // 其余全部取消，包括 /kill 和任何走 die() 的致死
        self.setHealth(self.getMaxHealth());
        self.clearFire();
        self.invulnerableTime = 60;
        self.deathTime = 0;
        self.hurtTime = 0;
        self.fallDistance = 0;
        ci.cancel();
    }
}