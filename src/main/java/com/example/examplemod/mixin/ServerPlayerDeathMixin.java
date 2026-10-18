package com.example.examplemod.mixin;

import com.potato.potatocharm.PotatoCharmHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public class ServerPlayerDeathMixin {

    /**
     * 原版在 ServerPlayer.die() 里调用 PlayerList.broadcastSystemMessage 广播死亡消息。
     * 这里拦截这个调用：持有土豆护符时直接跳过广播，死亡本身不受影响。
     */
    @Redirect(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
            )
    )
    private void yingzhiji$suppressDeathMessage(PlayerList playerList, Component message, boolean bypassHiddenChat) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (PotatoCharmHelper.hasPotatoCharm(self)) {
            return; // 持有护符：不广播死亡消息
        }
        playerList.broadcastSystemMessage(message, bypassHiddenChat);
    }
}