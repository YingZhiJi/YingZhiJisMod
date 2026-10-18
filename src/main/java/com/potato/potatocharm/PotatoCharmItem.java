package com.potato.potatocharm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class PotatoCharmItem extends Item {

    private static final Set<Block> RESPAWN_BLACKLIST = Set.of(
            Blocks.END_PORTAL,
            Blocks.END_GATEWAY,
            Blocks.END_PORTAL_FRAME
    );

    private static final int BLACKLIST_RADIUS = 4;

    public PotatoCharmItem(Properties properties) {
        super(properties);
    }

    private static boolean isNearRespawnBlacklist(ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(
                center.offset(-BLACKLIST_RADIUS, -BLACKLIST_RADIUS, -BLACKLIST_RADIUS),
                center.offset(BLACKLIST_RADIUS, BLACKLIST_RADIUS, BLACKLIST_RADIUS))) {
            Block b = player.level().getBlockState(p).getBlock();
            if (RESPAWN_BLACKLIST.contains(b)) return true;
        }
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        // 尊重铁砧自定义名
        if (stack.hasCustomHoverName()) {
            return stack.getHoverName();
        }

        String raw = Component.translatable(this.getDescriptionId(stack)).getString();
        MutableComponent result = Component.empty();
        for (int i = 0; i < raw.length(); i++) {
            String ch = String.valueOf(raw.charAt(i));
            int color = PotatoCharmHelper.rainbowColor(i * 40);
            result.append(Component.literal(ch)
                    .withStyle(Style.EMPTY.withColor(color)));
        }
        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel serverLevel = player.serverLevel();
        BlockPos pos = player.blockPosition();

        if (pos.getY() < -64) {
            player.teleportTo(serverLevel,
                    pos.getX() + 0.5, 312, pos.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
            player.setDeltaMovement(0, 0, 0);
            player.fallDistance = 0;
        }

        if (!isNearRespawnBlacklist(player)) {
            BlockPos respawnPos = pos.getY() < -64
                    ? new BlockPos(pos.getX(), 312, pos.getZ())
                    : pos;

            player.setRespawnPosition(
                    serverLevel.dimension(),
                    respawnPos,
                    player.getYRot(),
                    true,
                    false
            );
        }

        player.invulnerableTime = 60;

        serverLevel.getGameRules()
                .getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN)
                .set(true, player.server);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            PotatoCharmNetwork.CHANNEL.sendToServer(new C2SRequestDimList());
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}