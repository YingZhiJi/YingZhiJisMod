package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class LibrarianEntity extends PathfinderMob implements Merchant {

    public static final int SQUISH_DURATION = 10;

    private static final EntityDataAccessor<Integer> DATA_SQUISH =
            SynchedEntityData.defineId(LibrarianEntity.class, EntityDataSerializers.INT);

    @Nullable
    private Player tradingPlayer;

    @Nullable
    private MerchantOffers offers;

    @Nullable
    private BlockPos bedPos;
    @Nullable
    private BlockPos workPos;
    @Nullable
    private BlockPos spawnPos;
    private boolean recordedSpawn = false;
    private long lastRestockDay = -1;

    public LibrarianEntity(EntityType<? extends LibrarianEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SQUISH, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(0.6F, 1.8F);
    }

    // ===== Merchant =====

    @Override
    public void setTradingPlayer(@Nullable Player player) { this.tradingPlayer = player; }

    @Nullable
    @Override
    public Player getTradingPlayer() { return this.tradingPlayer; }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
        }
        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) { this.offers = offers; }

    private void updateTrades() {
        for (LibrarianData.ShopEntry entry : LibrarianData.shop) {
            List<ItemStack> inputs = entry.inputs;
            if (inputs.size() == 1) {
                this.offers.add(new MerchantOffer(
                        inputs.get(0).copy(), entry.output.copy(),
                        entry.maxUses, 0, 0.0F));
            } else if (inputs.size() == 2) {
                this.offers.add(new MerchantOffer(
                        inputs.get(0).copy(), inputs.get(1).copy(), entry.output.copy(),
                        entry.maxUses, 0, 0.0F));
            }
        }
        if (this.offers.isEmpty()) {
            this.offers.add(new MerchantOffer(
                    new ItemStack(Items.BOOK, 8), new ItemStack(Items.EMERALD, 1),
                    16, 2, 0.05F));
        }
    }

    @Override
    public void notifyTrade(MerchantOffer offer) { offer.increaseUses(); }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {}

    @Override
    public int getVillagerXp() { return 0; }

    @Override
    public void overrideXp(int xp) {}

    @Override
    public boolean showProgressBar() { return false; }

    @Override
    public SoundEvent getNotifyTradeSound() { return SoundEvents.PLAYER_LEVELUP; }

    @Override
    public boolean isClientSide() { return this.level().isClientSide(); }

    // ===== 右键 =====

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!this.level().isClientSide()) {
                this.triggerSquish();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (this.level().isClientSide()) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!this.isAlive()) return InteractionResult.PASS;
        if (this.tradingPlayer != null) return InteractionResult.PASS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new S2COpenMenu(this.getId()));

        return InteractionResult.sidedSuccess(false);
    }

    public void triggerSquish() {
        this.entityData.set(DATA_SQUISH, SQUISH_DURATION);
        this.level().playSound(null,
                this.getX(), this.getY(), this.getZ(),
                ModSounds.LIBRARIAN_SQUISH.get(),
                SoundSource.NEUTRAL,
                1.0F, 1.0F);
    }

    // ===== Tick =====

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) return;

        if (!this.recordedSpawn) {
            this.spawnPos = this.blockPosition();
            this.recordedSpawn = true;
        }

        int sq = this.entityData.get(DATA_SQUISH);
        if (sq > 0) {
            this.entityData.set(DATA_SQUISH, sq - 1);
        }

        if (this.tickCount % 20 == 0) {
            this.scanAttachments();
        }

        this.handleSleep();
        this.handleRestock();
    }

    // ===== 床/工作方块绑定 =====

    private void scanAttachments() {
        if (this.bedPos == null) {
            BlockPos found = findNearbyBed(this.blockPosition(), 15);
            if (found != null) this.bedPos = found;
        }

        if (this.bedPos != null && this.workPos == null) {
            BlockPos bedHead = getBedHeadPos(this.bedPos);
            if (bedHead != null) {
                BlockPos found = findNearbyLectern(bedHead, 15);
                if (found != null) this.workPos = found;
            }
        }
    }

    @Nullable
    private BlockPos findNearbyBed(BlockPos center, int radius) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    m.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = this.level().getBlockState(m);
                    if (state.getBlock() instanceof BedBlock
                            && state.getValue(BedBlock.PART) == BedPart.HEAD) {
                        double d = center.distSqr(m);
                        if (d < bestDist) { bestDist = d; best = m.immutable(); }
                    }
                }
            }
        }
        return best;
    }

    @Nullable
    private BlockPos findNearbyLectern(BlockPos center, int radius) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    m.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (this.level().getBlockState(m).is(Blocks.LECTERN)) {
                        double d = center.distSqr(m);
                        if (d < bestDist) { bestDist = d; best = m.immutable(); }
                    }
                }
            }
        }
        return best;
    }

    @Nullable
    private BlockPos getBedHeadPos(BlockPos bedPos) {
        BlockState state = this.level().getBlockState(bedPos);
        if (!(state.getBlock() instanceof BedBlock)) return null;
        if (state.getValue(BedBlock.PART) == BedPart.HEAD) return bedPos;
        Direction facing = state.getValue(BedBlock.FACING);
        return bedPos.relative(facing);
    }

    // ===== 夜晚睡眠 =====

    private void handleSleep() {
        if (this.bedPos == null) return;

        boolean night = !this.level().isDay();

        if (night && !this.isSleeping()) {
            BlockState state = this.level().getBlockState(this.bedPos);
            if (!(state.getBlock() instanceof BedBlock)) {
                this.bedPos = null;
                return;
            }

            Vec3 bedCenter = Vec3.atCenterOf(this.bedPos);
            double dist = this.distanceToSqr(bedCenter);

            if (dist < 4.0) {
                try {
                    this.startSleeping(this.bedPos);
                } catch (Throwable ignored) { }
            } else if (dist < 900.0) {
                this.getNavigation().moveTo(
                        this.bedPos.getX() + 0.5,
                        this.bedPos.getY(),
                        this.bedPos.getZ() + 0.5,
                        1.0);
            }
        } else if (!night && this.isSleeping()) {
            this.stopSleeping();
        }
    }

    // ===== 补货 =====

    private void handleRestock() {
        if (this.bedPos == null || this.workPos == null) return;
        if (this.tradingPlayer != null) return;

        BlockState bedState = this.level().getBlockState(this.bedPos);
        if (!(bedState.getBlock() instanceof BedBlock)) {
            this.bedPos = null;
            return;
        }
        if (!this.level().getBlockState(this.workPos).is(Blocks.LECTERN)) {
            this.workPos = null;
            return;
        }

        long day = this.level().getDayTime() / 24000L;
        if (this.lastRestockDay < 0) {
            this.lastRestockDay = day;
            return;
        }
        if (day > this.lastRestockDay) {
            this.lastRestockDay = day;
            this.offers = null;
        }
    }

    // ===== 死亡复活 =====

    public void respawnAtHome() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        ServerLevel overworld = serverLevel.getServer().overworld();

        BlockPos target;
        if (this.bedPos != null) {
            BlockState state = overworld.getBlockState(this.bedPos);
            if (state.getBlock() instanceof BedBlock) {
                target = this.bedPos;
            } else {
                target = getFallbackSpawn(overworld);
            }
        } else {
            target = getFallbackSpawn(overworld);
        }

        if (this.level() != overworld) {
            this.changeDimension(overworld);
        }

        this.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        this.setHealth(this.getMaxHealth());
        this.clearFire();
        this.invulnerableTime = 60;
        this.setDeltaMovement(0, 0, 0);
        this.hurtTime = 0;
        this.deathTime = 0;
    }

    private BlockPos getFallbackSpawn(ServerLevel overworld) {
        if (this.spawnPos != null) {
            return this.spawnPos;
        }
        return overworld.getSharedSpawnPos();
    }

    // ===== 保存/读取 =====

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.bedPos != null) tag.putLong("HomeBed", this.bedPos.asLong());
        if (this.workPos != null) tag.putLong("WorkLectern", this.workPos.asLong());
        if (this.spawnPos != null) tag.putLong("SpawnPos", this.spawnPos.asLong());
        tag.putBoolean("RecordedSpawn", this.recordedSpawn);
        tag.putLong("LastRestockDay", this.lastRestockDay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HomeBed")) this.bedPos = BlockPos.of(tag.getLong("HomeBed"));
        if (tag.contains("WorkLectern")) this.workPos = BlockPos.of(tag.getLong("WorkLectern"));
        if (tag.contains("SpawnPos")) this.spawnPos = BlockPos.of(tag.getLong("SpawnPos"));
        this.recordedSpawn = tag.getBoolean("RecordedSpawn");
        this.lastRestockDay = tag.getLong("LastRestockDay");
    }

    // ===== 对话 =====

    public void sendDialogueNode(ServerPlayer player, String nodeId) {
        LibrarianData.DialogueNode node = LibrarianData.dialogues.get(nodeId);
        if (node == null) return;

        List<String> optTexts = new ArrayList<>();
        List<String> optNexts = new ArrayList<>();
        for (LibrarianData.DialogueOption opt : node.options) {
            optTexts.add(opt.text);
            optNexts.add(opt.next);
        }

        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new S2COpenDialogue(this.getId(), node.speaker, node.text,
                        node.action, optTexts, optNexts));
    }

    public void openTradeFor(ServerPlayer serverPlayer) {
        if (!this.isAlive()) return;
        if (this.tradingPlayer != null) return;

        this.setTradingPlayer(serverPlayer);
        this.offers = null;
        this.getOffers();

        OptionalInt containerId = serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (id, inv, p) -> new MerchantMenu(id, inv, this),
                        this.getDisplayName()
                )
        );

        containerId.ifPresent(id -> serverPlayer.sendMerchantOffers(
                id, this.getOffers(), 1, 0, false, false));
    }

    public void sendQuestList(ServerPlayer player) {
        try {
            List<QuestView> views = new ArrayList<>();
            for (LibrarianData.QuestDefinition q : LibrarianData.quests.values()) {
                views.add(buildQuestView(player, q));
            }
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new S2COpenQuests(this.getId(), views));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static int effectiveMax(LibrarianData.QuestDefinition q) {
        if (q.maxCompletions > 0) return q.maxCompletions;
        return q.repeatable ? Integer.MAX_VALUE : 1;
    }

    private QuestView buildQuestView(ServerPlayer player, LibrarianData.QuestDefinition q) {
        QuestTracker.Progress p = QuestTracker.read(player, q.id, q.requirements.size());

        List<QuestView.Line> reqLines = new ArrayList<>();
        boolean allDone = true;
        for (int i = 0; i < q.requirements.size(); i++) {
            LibrarianData.QuestRequirement req = q.requirements.get(i);

            int cur;
            String iconType;
            String iconId = null;
            List<String> alts = null;
            String text;

            if ("submit_item".equals(req.type)) {
                cur = countInInventory(player, req);
                if (req.item != null) {
                    iconType = "item";
                    iconId = req.item;
                    text = displayItemName(req.item);
                } else if (req.tag != null) {
                    iconType = "tag";
                    alts = pickItemsFromTag(req.tag);
                    if (!alts.isEmpty()) iconId = alts.get(0);
                    text = displayTagName(req.tag);
                } else {
                    iconType = "none";
                    text = "未知物品";
                }
            } else if ("kill_entity".equals(req.type)) {
                cur = p.counters.length > i ? p.counters[i] : 0;
                if (req.entity != null) {
                    iconType = "entity";
                    iconId = req.entity;
                    text = displayEntityName(req.entity);
                } else {
                    iconType = "none";
                    text = displayTagName(req.tag);
                }
            } else {
                cur = 0;
                iconType = "none";
                text = req.type;
            }

            reqLines.add(new QuestView.Line(iconType, iconId, text, cur, req.count, alts));
            if (cur < req.count) allDone = false;
        }

        List<QuestView.Line> rewLines = new ArrayList<>();
        if (q.reward != null) {
            if (q.reward.experience > 0) {
                rewLines.add(new QuestView.Line("exp", null,
                        "经验", 0, q.reward.experience, null));
            }
            if (q.reward.items != null) {
                for (LibrarianData.RewardItem ri : q.reward.items) {
                    boolean isRandom = ri.random != null && !ri.random.isEmpty();
                    if (isRandom) {
                        String firstId = ri.random.get(0);
                        String first = displayItemName(firstId);
                        String text = first + "（随机）";
                        rewLines.add(new QuestView.Line("tag", firstId, text,
                                0, ri.count, new ArrayList<>(ri.random)));
                    } else {
                        String name = displayItemName(ri.item);
                        rewLines.add(new QuestView.Line("item", ri.item, name,
                                0, ri.count, null));
                    }
                }
            }
        }

        int effMax = effectiveMax(q);
        boolean maxed = p.completions >= effMax;

        String status;
        if (maxed) status = "completed";
        else if (!p.accepted) status = "unaccepted";
        else if (allDone) status = "completable";
        else status = "in_progress";

        return new QuestView(q.id, q.name, q.description,
                reqLines, rewLines, status,
                q.repeatable, p.completions, q.maxCompletions, effMax);
    }

    private List<String> pickItemsFromTag(String tag) {
        List<String> result = new ArrayList<>();
        try {
            ResourceLocation tagRL = new ResourceLocation(tag);
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagRL);
            var holders = ForgeRegistries.ITEMS.tags().getTag(tagKey);
            if (holders == null || holders.isEmpty()) return result;
            for (Item it : holders) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(it);
                if (id != null) result.add(id.toString());
            }
        } catch (Throwable t) { }
        return result;
    }

    private String displayTagName(String tag) {
        if (tag == null) return "?";
        try {
            String ns = "minecraft";
            String path = tag;
            if (tag.contains(":")) {
                ns = tag.substring(0, tag.indexOf(':'));
                path = tag.substring(tag.indexOf(':') + 1);
            }
            String key = "tag.item." + ns + "." + path.replace('/', '.');
            String translated = Component.translatable(key).getString();
            if (!translated.equals(key)) return "任意 " + translated;
        } catch (Throwable t) { }
        String t = tag;
        if (t.startsWith("minecraft:")) t = t.substring("minecraft:".length());
        return "任意 #" + t;
    }

    private String displayItemName(String id) {
        if (id == null || id.isEmpty() || id.startsWith("#")) {
            return id == null ? "?" : id;
        }
        try {
            var rl = new ResourceLocation(id);
            var item = ForgeRegistries.ITEMS.getValue(rl);
            if (item == null || item == Items.AIR) return id;
            return new ItemStack(item).getHoverName().getString();
        } catch (Throwable t) {
            return id;
        }
    }

    private String displayEntityName(String id) {
        if (id == null) return "?";
        try {
            var rl = new ResourceLocation(id);
            var type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
            if (type == null) return id;
            return type.getDescription().getString();
        } catch (Throwable t) {
            return id;
        }
    }

    private int countInInventory(ServerPlayer player, LibrarianData.QuestRequirement req) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (matchesReq(stack, req)) total += stack.getCount();
        }
        return total;
    }

    public void handleQuestAction(ServerPlayer player, String questId, String action) {
        LibrarianData.QuestDefinition q = LibrarianData.quests.get(questId);
        if (q == null) return;

        QuestTracker.Progress p = QuestTracker.read(player, questId, q.requirements.size());
        int effMax = effectiveMax(q);

        if ("accept".equals(action)) {
            if (p.accepted) return;
            if (p.completions >= effMax) return;
            p.accepted = true;
            p.counters = new int[q.requirements.size()];
            QuestTracker.write(player, questId, p);
            sendQuestList(player);
            return;
        }

        if ("submit".equals(action)) {
            if (!p.accepted) return;

            for (int i = 0; i < q.requirements.size(); i++) {
                LibrarianData.QuestRequirement req = q.requirements.get(i);
                if ("submit_item".equals(req.type)) {
                    if (countInInventory(player, req) < req.count) return;
                } else {
                    int cur = p.counters.length > i ? p.counters[i] : 0;
                    if (cur < req.count) return;
                }
            }

            if (!consumeItems(player, q)) return;
            grantRewards(player, q);

            p.completions++;
            p.accepted = false;
            p.counters = new int[q.requirements.size()];
            QuestTracker.write(player, questId, p);
            sendQuestList(player);
        }
    }

    private boolean consumeItems(ServerPlayer player, LibrarianData.QuestDefinition q) {
        for (LibrarianData.QuestRequirement req : q.requirements) {
            if (!"submit_item".equals(req.type)) continue;
            int remaining = req.count;
            for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty()) continue;
                if (matchesReq(stack, req)) {
                    int take = Math.min(remaining, stack.getCount());
                    stack.shrink(take);
                    remaining -= take;
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    private boolean matchesReq(ItemStack stack, LibrarianData.QuestRequirement req) {
        if (req.item != null) {
            var expected = ForgeRegistries.ITEMS.getValue(new ResourceLocation(req.item));
            return expected != null && stack.is(expected);
        }
        if (req.tag != null) {
            var tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(req.tag));
            return stack.is(tagKey);
        }
        return false;
    }

    private void grantRewards(ServerPlayer player, LibrarianData.QuestDefinition q) {
        if (q.reward == null) return;
        if (q.reward.experience > 0) {
            player.giveExperiencePoints(q.reward.experience);
        }
        if (q.reward.items != null) {
            for (LibrarianData.RewardItem ri : q.reward.items) {
                String id = ri.item;
                if (ri.random != null && !ri.random.isEmpty()) {
                    id = ri.random.get(this.random.nextInt(ri.random.size()));
                }
                var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                if (item == null) continue;
                ItemStack reward = new ItemStack(item, ri.count);

                if (ri.enchantments != null && !ri.enchantments.isEmpty()) {
                    CompoundTag tag = reward.getOrCreateTag();
                    ListTag list = new ListTag();
                    for (LibrarianData.EnchantEntry ee : ri.enchantments) {
                        CompoundTag one = new CompoundTag();
                        one.putString("id", ee.id);
                        one.putShort("lvl", (short) ee.level);
                        list.add(one);
                    }
                    tag.put("Enchantments", list);
                }

                if (!player.getInventory().add(reward)) {
                    player.drop(reward, false);
                }
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.PLAYER_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.PLAYER_DEATH; }

    @Override
    public Component getDisplayName() { return Component.literal("图书管理员"); }

    public int getSquishTicks() {
        return this.entityData.get(DATA_SQUISH);
    }
}