package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ZomperiumSpawnHandler {

    private static final Random RNG = new Random();
    private static int tickCounter = 0;

    private static final int CHECK_INTERVAL = 1200;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            trySpawn(level);
        }
    }

    private static void trySpawn(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) return;

        ZomperiumSpawnData data = ZomperiumSpawnData.get(level);

        long currentDay = level.getDayTime() / 24000L;
        if (data.lastCheckedDay < 0) {
            data.lastCheckedDay = currentDay;
        } else if (currentDay > data.lastCheckedDay) {
            data.daysWithoutSpawn += (int) (currentDay - data.lastCheckedDay);
            data.lastCheckedDay = currentDay;
        }

        float privateChance = 0.02F
                + data.daysWithoutSpawn * 0.01F
                + data.zombieKills * 0.001F;

        float scoutChance = 0.005F
                + data.daysWithoutSpawn * 0.005F
                + data.zombieKills * 0.0005F
                + data.scoutBonus;

        privateChance = Math.min(privateChance, 0.5F);
        scoutChance = Math.min(scoutChance, 0.5F);

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;
        ServerPlayer anchor = players.get(RNG.nextInt(players.size()));

        if (RNG.nextFloat() < scoutChance) {
            if (spawnScoutGroup(level, anchor)) {
                data.daysWithoutSpawn = 0;
                data.zombieKills = 0;
                data.scoutBonus = 0.0F;
                data.setDirty();
                return;
            }
        }

        if (RNG.nextFloat() < privateChance) {
            if (spawnPrivate(level, anchor)) {
                data.daysWithoutSpawn = 0;
                data.zombieKills = 0;
                data.setDirty();
            }
        }
    }

    private static boolean spawnPrivate(ServerLevel level, Player anchor) {
        BlockPos pos = findSpawnPos(level, anchor);
        if (pos == null) return false;

        ZomperiumPrivate soldier = ExampleMod.ZOMPERIUM_PRIVATE_ENTITY.get().create(level);
        if (soldier == null) return false;
        soldier.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                RNG.nextFloat() * 360.0F, 0.0F);
        soldier.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                MobSpawnType.NATURAL, null, null);
        level.addFreshEntity(soldier);
        return true;
    }

    private static boolean spawnScoutGroup(ServerLevel level, Player anchor) {
        BlockPos pos = findSpawnPos(level, anchor);
        if (pos == null) return false;

        ZomperiumScout scout = ExampleMod.ZOMPERIUM_SCOUT_ENTITY.get().create(level);
        if (scout == null) return false;
        scout.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                RNG.nextFloat() * 360.0F, 0.0F);
        scout.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                MobSpawnType.MOB_SUMMONED, null, null);
        level.addFreshEntity(scout);

        for (int i = 0; i < 5; i++) {
            ZomperiumPrivate soldier = ExampleMod.ZOMPERIUM_PRIVATE_ENTITY.get().create(level);
            if (soldier == null) continue;
            double dx = (RNG.nextDouble() - 0.5) * 6.0;
            double dz = (RNG.nextDouble() - 0.5) * 6.0;
            soldier.moveTo(pos.getX() + 0.5 + dx, pos.getY(), pos.getZ() + 0.5 + dz,
                    RNG.nextFloat() * 360.0F, 0.0F);
            soldier.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                    MobSpawnType.MOB_SUMMONED, null, null);
            level.addFreshEntity(soldier);
        }
        return true;
    }

    private static BlockPos findSpawnPos(ServerLevel level, Player anchor) {
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = RNG.nextDouble() * Math.PI * 2;
            double dist = 24.0 + RNG.nextDouble() * 24.0;
            int x = (int) (anchor.getX() + Math.cos(angle) * dist);
            int z = (int) (anchor.getZ() + Math.sin(angle) * dist);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos pos = new BlockPos(x, y, z);

            if (!level.canSeeSky(pos)) continue;
            if (!level.getBlockState(pos).isAir() && !level.getBlockState(pos).is(Blocks.COBWEB)) continue;
            if (!level.getBlockState(pos.above()).isAir()) continue;
            if (level.getBlockState(pos.below()).isAir()) continue;

            return pos;
        }
        return null;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getSource().getEntity() instanceof Player)) return;
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;

        if (event.getEntity() instanceof Zombie) {
            ZomperiumSpawnData data = ZomperiumSpawnData.get(level);
            data.zombieKills++;
            data.setDirty();
            return;
        }

        if (event.getEntity() instanceof ZomperiumPrivate soldier) {
            // 召唤物不计入侦察小组加成
            if (soldier.getPersistentData().getBoolean(ZomperiumPrivate.NBT_SUMMONED)) {
                return;
            }
            ZomperiumSpawnData data = ZomperiumSpawnData.get(level);
            data.scoutBonus += 0.1F;
            data.setDirty();
        }
    }
}