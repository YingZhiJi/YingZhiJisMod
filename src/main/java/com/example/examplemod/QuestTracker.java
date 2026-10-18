package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class QuestTracker {

    public static class Progress {
        public boolean accepted;
        public int[] counters;
        public int completions;
    }

    public static Progress read(ServerPlayer player, String questId, int reqCount) {
        CompoundTag quests = player.getPersistentData().getCompound("yzj_quests");
        Progress p = new Progress();
        p.counters = new int[reqCount];
        if (quests.contains(questId)) {
            CompoundTag t = quests.getCompound(questId);
            p.accepted = t.getBoolean("accepted");
            p.completions = t.getInt("completions");
            int[] arr = t.getIntArray("counters");
            if (arr.length == reqCount) p.counters = arr;
        }
        return p;
    }

    public static void write(ServerPlayer player, String questId, Progress p) {
        CompoundTag root = player.getPersistentData();
        CompoundTag quests = root.getCompound("yzj_quests");
        CompoundTag t = new CompoundTag();
        t.putBoolean("accepted", p.accepted);
        t.putInt("completions", p.completions);
        t.putIntArray("counters", p.counters);
        quests.put(questId, t);
        root.put("yzj_quests", quests);
    }
}