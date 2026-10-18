package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class ZomperiumSpawnData extends SavedData {

    public static final String DATA_NAME = "yingzhiji_s_mod_spawn_data";

    // 距离上次成功刷新过了多少天
    public int daysWithoutSpawn = 0;

    // 玩家累计击杀僵尸数
    public int zombieKills = 0;

    // 击杀列兵累积的侦察小组加成（每次 +0.1）
    public float scoutBonus = 0.0F;

    // 上次记录的天数
    public long lastCheckedDay = -1L;

    public ZomperiumSpawnData() {}

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("DaysWithoutSpawn", daysWithoutSpawn);
        tag.putInt("ZombieKills", zombieKills);
        tag.putFloat("ScoutBonus", scoutBonus);
        tag.putLong("LastCheckedDay", lastCheckedDay);
        return tag;
    }

    public static ZomperiumSpawnData load(CompoundTag tag) {
        ZomperiumSpawnData data = new ZomperiumSpawnData();
        data.daysWithoutSpawn = tag.getInt("DaysWithoutSpawn");
        data.zombieKills = tag.getInt("ZombieKills");
        data.scoutBonus = tag.getFloat("ScoutBonus");
        data.lastCheckedDay = tag.getLong("LastCheckedDay");
        return data;
    }

    public static ZomperiumSpawnData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage()
                .computeIfAbsent(ZomperiumSpawnData::load, ZomperiumSpawnData::new, DATA_NAME);
    }
}