package com.example.examplemod;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LibrarianData {

    public static Map<String, DialogueNode> dialogues = new HashMap<>();
    public static List<ShopEntry> shop = new ArrayList<>();
    public static Map<String, QuestDefinition> quests = new LinkedHashMap<>();

    public static class DialogueNode {
        public String speaker;
        public String text;
        public String action;
        public List<DialogueOption> options = new ArrayList<>();
    }

    public static class DialogueOption {
        public String text;
        public String next;
    }

    public static class ShopEntry {
        public List<ItemStack> inputs;
        public ItemStack output;
        public int maxUses;
    }

    public static class QuestDefinition {
        public String id;
        public String name;
        public String description;
        public List<QuestRequirement> requirements;
        public QuestReward reward;
        public boolean repeatable;
        public int maxCompletions;
    }

    public static class QuestRequirement {
        public String type;
        public String item;
        public String entity;
        public String tag;
        public int count;
    }

    public static class QuestReward {
        public List<RewardItem> items;
        public int experience;
    }

    public static class RewardItem {
        public String item;
        public int count;
        public List<String> random;
        public List<EnchantEntry> enchantments;
    }

    public static class EnchantEntry {
        public String id;
        public int level;
    }
}