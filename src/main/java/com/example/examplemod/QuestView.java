package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class QuestView {

    /**
     * iconType:
     *   "item"  - 普通物品图标
     *   "tag"   - 标签/随机组（面板显示第一个，hover 时全览）
     *   "entity"- 实体（面板不画图标，hover 时显示旋转模型）
     *   "exp"   - 经验（面板不画图标，纯文字）
     *   "none"  - 无图标
     */
    public static class Line {
        public final String iconType;
        public final String iconId;
        public final String text;
        public final int cur;
        public final int total;
        public final List<String> alternatives;

        public Line(String iconType, String iconId, String text,
                    int cur, int total, List<String> alternatives) {
            this.iconType = iconType;
            this.iconId = iconId;
            this.text = text;
            this.cur = cur;
            this.total = total;
            this.alternatives = alternatives;
        }
    }

    public final String id;
    public final String name;
    public final String description;
    public final List<Line> requirements;
    public final List<Line> rewards;
    public final String status;
    public final boolean repeatable;
    public final int completions;
    public final int maxCompletions;
    public final int effectiveMax;

    public QuestView(String id, String name, String description,
                     List<Line> requirements, List<Line> rewards,
                     String status,
                     boolean repeatable, int completions, int maxCompletions,
                     int effectiveMax) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.rewards = rewards;
        this.status = status;
        this.repeatable = repeatable;
        this.completions = completions;
        this.maxCompletions = maxCompletions;
        this.effectiveMax = effectiveMax;
    }

    public QuestView(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.name = buf.readUtf();
        this.description = buf.readUtf();
        this.requirements = readLines(buf);
        this.rewards = readLines(buf);
        this.status = buf.readUtf();
        this.repeatable = buf.readBoolean();
        this.completions = buf.readVarInt();
        this.maxCompletions = buf.readVarInt();
        this.effectiveMax = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        buf.writeUtf(this.name);
        buf.writeUtf(this.description);
        writeLines(buf, this.requirements);
        writeLines(buf, this.rewards);
        buf.writeUtf(this.status);
        buf.writeBoolean(this.repeatable);
        buf.writeVarInt(this.completions);
        buf.writeVarInt(this.maxCompletions);
        buf.writeVarInt(this.effectiveMax);
    }

    private static void writeLines(FriendlyByteBuf buf, List<Line> lines) {
        buf.writeVarInt(lines.size());
        for (Line l : lines) {
            buf.writeUtf(l.iconType);
            if (l.iconId != null) { buf.writeBoolean(true); buf.writeUtf(l.iconId); }
            else buf.writeBoolean(false);
            buf.writeUtf(l.text);
            buf.writeVarInt(l.cur);
            buf.writeVarInt(l.total);
            if (l.alternatives != null) {
                buf.writeBoolean(true);
                buf.writeVarInt(l.alternatives.size());
                for (String s : l.alternatives) buf.writeUtf(s);
            } else {
                buf.writeBoolean(false);
            }
        }
    }

    private static List<Line> readLines(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<Line> lines = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String type = buf.readUtf();
            String id = buf.readBoolean() ? buf.readUtf() : null;
            String text = buf.readUtf();
            int cur = buf.readVarInt();
            int total = buf.readVarInt();
            List<String> alts = null;
            if (buf.readBoolean()) {
                int m = buf.readVarInt();
                alts = new ArrayList<>(m);
                for (int j = 0; j < m; j++) alts.add(buf.readUtf());
            }
            lines.add(new Line(type, id, text, cur, total, alts));
        }
        return lines;
    }
}