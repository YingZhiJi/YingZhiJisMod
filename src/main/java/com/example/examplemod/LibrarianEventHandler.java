package com.example.examplemod;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class LibrarianEventHandler {

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(LibrarianDataLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        CompoundTag oldData = oldPlayer.getPersistentData();
        if (oldData.contains("yzj_quests")) {
            newPlayer.getPersistentData().put("yzj_quests",
                    oldData.getCompound("yzj_quests").copy());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        ServerPlayer player = null;
        if (event.getSource().getEntity() instanceof ServerPlayer sp) {
            player = sp;
        } else if (event.getSource().getDirectEntity() instanceof ServerPlayer sp2) {
            player = sp2;
        }
        if (player == null) return;

        processKill(player, event.getEntity());
    }

    /**
     * 通用击杀处理：更新任务进度。
     * 从 onLivingDeath 中抽出，以便绕过 LivingDeathEvent 的自定义死亡流程也能触发。
     */
    public static void processKill(ServerPlayer player, LivingEntity dead) {
        if (player == null || dead == null) return;

        EntityType<?> killedType = dead.getType();
        var killedId = ForgeRegistries.ENTITY_TYPES.getKey(killedType);
        if (killedId == null) return;

        for (LibrarianData.QuestDefinition quest : LibrarianData.quests.values()) {
            QuestTracker.Progress p = QuestTracker.read(player, quest.id, quest.requirements.size());
            if (!p.accepted) continue;

            boolean changed = false;
            for (int i = 0; i < quest.requirements.size(); i++) {
                LibrarianData.QuestRequirement req = quest.requirements.get(i);
                if (!"kill_entity".equals(req.type)) continue;

                boolean match = false;
                if (req.entity != null) {
                    match = req.entity.equals(killedId.toString());
                } else if (req.tag != null) {
                    TagKey<EntityType<?>> tagKey = TagKey.create(
                            Registries.ENTITY_TYPE, new ResourceLocation(req.tag));
                    match = dead.getType().is(tagKey);
                }

                if (match && p.counters[i] < req.count) {
                    p.counters[i]++;
                    changed = true;
                }
            }
            if (changed) {
                QuestTracker.write(player, quest.id, p);
            }
        }
    }
}