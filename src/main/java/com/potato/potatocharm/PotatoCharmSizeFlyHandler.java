package com.potato.potatocharm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotatoCharmSizeFlyHandler {

    private static final String NBT_FLY_GRANTED = "potato_charm_fly";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        boolean has = PotatoCharmHelper.hasPotatoCharm(player);

        // ---- 小碰撞箱校对：重生后新实体默认大尺寸，主动 refresh ----
        EntityDimensions current = player.getDimensions(player.getPose());
        boolean isSmall = current.height <= 0.4f;
        if (has != isSmall) {
            player.refreshDimensions();
        }

        // ---- 血量归零兜底：防止 setHealth(0) 让玩家卡在 0 血 ----
        if (has && player.getHealth() <= 0f) {
            player.setHealth(player.getMaxHealth());
            player.deathTime = 0;
            player.hurtTime = 0;
            player.invulnerableTime = 60;
        }

        // ---- 飞行权限 ----
        CompoundTag data = player.getPersistentData();
        boolean grantedByCharm = data.getBoolean(NBT_FLY_GRANTED);

        if (has) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
            if (!grantedByCharm) {
                data.putBoolean(NBT_FLY_GRANTED, true);
            }
        } else {
            if (grantedByCharm) {
                if (!player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
                data.remove(NBT_FLY_GRANTED);
            }
        }
    }
}