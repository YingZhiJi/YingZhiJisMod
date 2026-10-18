package com.example.examplemod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class EndFlashDropHandler {

    /** 存在 ItemEntity 的 PersistentData 里，键名跟 Mixin 共享 */
    public static final String NBT_OWNER = "yzj_end_flash_owner";

    /**
     * 玩家丢出末地闪光时绑定 owner
     */
    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemEntity entity = event.getEntity();
        if (entity.getItem().is(ExampleMod.END_FLASH.get())) {
            entity.getPersistentData().putUUID(NBT_OWNER, event.getPlayer().getUUID());
        }
    }

    /**
     * 兜底：非玩家丢出的末地闪光（箱子爆掉、怪物掉落、命令等）
     * 第一次 tick 时找最近玩家绑定。
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ItemEntity entity)) return;
        if (!entity.getItem().is(ExampleMod.END_FLASH.get())) return;
        if (entity.getPersistentData().hasUUID(NBT_OWNER)) return;

        if (event.getLevel() instanceof ServerLevel sl) {
            Player nearest = sl.getNearestPlayer(entity, 32.0);
            if (nearest != null) {
                entity.getPersistentData().putUUID(NBT_OWNER, nearest.getUUID());
            }
        }
    }
}