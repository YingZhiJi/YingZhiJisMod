package com.example.examplemod;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;

public class LibrarianDataLoader extends SimpleJsonResourceReloadListener {

    public static final LibrarianDataLoader INSTANCE = new LibrarianDataLoader();

    public LibrarianDataLoader() {
        super(new Gson(), "librarian");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller profiler) {
        LibrarianData.dialogues.clear();
        LibrarianData.shop.clear();
        LibrarianData.quests.clear();

        JsonElement d = map.get(new ResourceLocation(ExampleMod.MODID, "dialogue"));
        if (d != null) parseDialogue(d.getAsJsonObject());

        JsonElement s = map.get(new ResourceLocation(ExampleMod.MODID, "shop"));
        if (s != null) parseShop(s.getAsJsonObject());

        JsonElement q = map.get(new ResourceLocation(ExampleMod.MODID, "quests"));
        if (q != null) parseQuests(q.getAsJsonObject());
    }

    private void parseDialogue(JsonObject root) {
        for (Map.Entry<String, JsonElement> e : root.entrySet()) {
            JsonObject obj = e.getValue().getAsJsonObject();
            LibrarianData.DialogueNode node = new LibrarianData.DialogueNode();
            node.speaker = getStr(obj, "speaker", "图书管理员");
            node.text = getStr(obj, "text", "");
            node.action = obj.has("action") ? obj.get("action").getAsString() : null;
            if (obj.has("options")) {
                for (JsonElement o : obj.getAsJsonArray("options")) {
                    JsonObject oo = o.getAsJsonObject();
                    LibrarianData.DialogueOption opt = new LibrarianData.DialogueOption();
                    opt.text = getStr(oo, "text", "");
                    opt.next = getStr(oo, "next", "");
                    node.options.add(opt);
                }
            }
            LibrarianData.dialogues.put(e.getKey(), node);
        }
    }

    private void parseShop(JsonObject root) {
        if (!root.has("entries")) return;
        for (JsonElement e : root.getAsJsonArray("entries")) {
            JsonObject o = e.getAsJsonObject();
            LibrarianData.ShopEntry entry = new LibrarianData.ShopEntry();
            entry.inputs = new ArrayList<>();
            for (JsonElement in : o.getAsJsonArray("inputs")) {
                entry.inputs.add(parseItem(in.getAsJsonObject()));
            }
            entry.output = parseItem(o.getAsJsonObject("output"));
            entry.maxUses = o.has("maxUses") ? o.get("maxUses").getAsInt() : 12;
            LibrarianData.shop.add(entry);
        }
    }

    private void parseQuests(JsonObject root) {
        if (!root.has("quests")) return;
        for (JsonElement e : root.getAsJsonArray("quests")) {
            JsonObject o = e.getAsJsonObject();
            LibrarianData.QuestDefinition quest = new LibrarianData.QuestDefinition();
            quest.id = getStr(o, "id", "");
            quest.name = getStr(o, "name", "");
            quest.description = getStr(o, "description", "");
            quest.repeatable = o.has("repeatable") && o.get("repeatable").getAsBoolean();
            quest.maxCompletions = o.has("maxCompletions")
                    ? o.get("maxCompletions").getAsInt()
                    : (quest.repeatable ? -1 : 1);

            quest.requirements = new ArrayList<>();
            if (o.has("requirements")) {
                for (JsonElement r : o.getAsJsonArray("requirements")) {
                    JsonObject ro = r.getAsJsonObject();
                    LibrarianData.QuestRequirement req = new LibrarianData.QuestRequirement();
                    req.type = getStr(ro, "type", "");
                    req.item = ro.has("item") ? ro.get("item").getAsString() : null;
                    req.entity = ro.has("entity") ? ro.get("entity").getAsString() : null;
                    req.tag = ro.has("tag") ? ro.get("tag").getAsString() : null;
                    req.count = ro.has("count") ? ro.get("count").getAsInt() : 1;
                    quest.requirements.add(req);
                }
            }

            if (o.has("rewards")) {
                JsonObject rew = o.getAsJsonObject("rewards");
                LibrarianData.QuestReward reward = new LibrarianData.QuestReward();
                reward.experience = rew.has("experience") ? rew.get("experience").getAsInt() : 0;
                reward.items = new ArrayList<>();
                if (rew.has("items")) {
                    for (JsonElement it : rew.getAsJsonArray("items")) {
                        JsonObject io = it.getAsJsonObject();
                        LibrarianData.RewardItem ri = new LibrarianData.RewardItem();
                        ri.item = getStr(io, "item", "minecraft:stone");
                        ri.count = io.has("count") ? io.get("count").getAsInt() : 1;
                        if (io.has("random")) {
                            ri.random = new ArrayList<>();
                            for (JsonElement rr : io.getAsJsonArray("random")) {
                                ri.random.add(rr.getAsString());
                            }
                        }
                        if (io.has("enchantments")) {
                            ri.enchantments = new ArrayList<>();
                            for (JsonElement ee : io.getAsJsonArray("enchantments")) {
                                JsonObject eo = ee.getAsJsonObject();
                                LibrarianData.EnchantEntry entry = new LibrarianData.EnchantEntry();
                                entry.id = eo.get("id").getAsString();
                                entry.level = eo.get("level").getAsInt();
                                ri.enchantments.add(entry);
                            }
                        }
                        reward.items.add(ri);
                    }
                }
                quest.reward = reward;
            }

            LibrarianData.quests.put(quest.id, quest);
        }
    }

    private static ItemStack parseItem(JsonObject o) {
        String id = getStr(o, "item", "minecraft:stone");
        int count = o.has("count") ? o.get("count").getAsInt() : 1;
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) item = Items.STONE;
        ItemStack stack = new ItemStack(item, count);

        if (o.has("enchantments")) {
            CompoundTag tag = stack.getOrCreateTag();
            ListTag list = new ListTag();
            for (JsonElement el : o.getAsJsonArray("enchantments")) {
                JsonObject eo = el.getAsJsonObject();
                CompoundTag one = new CompoundTag();
                one.putString("id", eo.get("id").getAsString());
                one.putShort("lvl", (short) eo.get("level").getAsInt());
                list.add(one);
            }
            tag.put("Enchantments", list);
        }
        return stack;
    }

    private static String getStr(JsonObject o, String key, String def) {
        return o.has(key) ? o.get(key).getAsString() : def;
    }
}