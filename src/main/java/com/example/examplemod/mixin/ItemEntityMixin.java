package com.example.examplemod.mixin;

import com.example.examplemod.EndFlashDropHandler;
import com.example.examplemod.ExampleMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private static final String YZJ_RESCUING = "yzj_end_flash_rescuing";

    /**
     * 阻止末地闪光因 age >= 6000 而被原版 discard
     */
    @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void yzj$preventDespawn(ItemEntity self) {
        if (self.getItem().is(ExampleMod.END_FLASH.get())) {
            // 末地闪光：不 discard
            return;
        }
        self.discard();
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void yzj$endFlashHurtImmunity(DamageSource source, float amount,
                                          CallbackInfoReturnable<Boolean> cir) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.getItem().is(ExampleMod.END_FLASH.get())) return;

        if (source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void yzj$endFlashTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.getItem().is(ExampleMod.END_FLASH.get())) return;

        // 虚空救援仅服务端，且不再碰 age
        if (self.level().isClientSide()) return;

        int minY = self.level().getMinBuildHeight();
        boolean inVoid = self.getY() < minY - 5;
        boolean rescuing = self.getPersistentData().getBoolean(YZJ_RESCUING);

        if (!inVoid && !rescuing) return;

        if (!self.getPersistentData().hasUUID(EndFlashDropHandler.NBT_OWNER)) {
            yzj$recycleToTop(self);
            return;
        }
        UUID ownerUUID = self.getPersistentData().getUUID(EndFlashDropHandler.NBT_OWNER);

        Player owner = null;
        if (self.level() instanceof ServerLevel sl) {
            ServerPlayer sp = sl.getServer().getPlayerList().getPlayer(ownerUUID);
            if (sp != null) owner = sp;
        }

        if (owner == null || !owner.isAlive()) {
            yzj$recycleToTop(self);
            return;
        }

        self.getPersistentData().putBoolean(YZJ_RESCUING, true);
        self.setNoGravity(true);
        self.setGlowingTag(true);

        double dx = owner.getX() - self.getX();
        double dy = (owner.getY() + 1.0) - self.getY();
        double dz = owner.getZ() - self.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist < 2.0) {
            if (yzj$tryGiveToPlayer(owner, self)) return;
            self.setDeltaMovement(0, 0, 0);
            self.setPos(owner.getX(), owner.getY() + 1.0, owner.getZ());
            return;
        }

        double speed;
        if (dist < 4.0) speed = 0.35;
        else if (dist < 8.0) speed = 0.7;
        else speed = Math.min(1.5, 0.5 + dist * 0.05);

        self.setDeltaMovement(dx / dist * speed, dy / dist * speed, dz / dist * speed);
        self.hurtMarked = true;
    }

    @Unique
    private static void yzj$recycleToTop(ItemEntity self) {
        self.setPos(self.getX(), self.level().getMaxBuildHeight() - 10, self.getZ());
        self.setDeltaMovement(0, 0, 0);
        self.setNoGravity(false);
        self.setGlowingTag(false);
        self.getPersistentData().putBoolean(YZJ_RESCUING, false);
    }

    @Unique
    private static boolean yzj$tryGiveToPlayer(Player player, ItemEntity entity) {
        ItemStack stack = entity.getItem();
        Inventory inv = player.getInventory();

        int selected = inv.selected;
        if (inv.getItem(selected).isEmpty()) {
            inv.setItem(selected, stack.copy());
            entity.discard();
            return true;
        }

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).isEmpty()) {
                inv.setItem(i, stack.copy());
                entity.discard();
                return true;
            }
        }

        return false;
    }
}