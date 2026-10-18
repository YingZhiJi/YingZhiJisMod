package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CrystallizerMenu extends AbstractContainerMenu {

    private final CrystallizerBlockEntity blockEntity;
    private final ContainerData data;

    // 客户端构造器
    public CrystallizerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (CrystallizerBlockEntity) inv.player.level()
                .getBlockEntity(buf.readBlockPos()), new SimpleContainerData(6));
    }

    // 服务端构造器
    public CrystallizerMenu(int id, Inventory inv, CrystallizerBlockEntity be, ContainerData data) {
        super(ModMenus.CRYSTALLIZER.get(), id);
        this.blockEntity = be;
        this.data = data;

        IItemHandler handler = be.getItemHandler();

        // 0 = 晶体槽（燃灰 / 燃灰块 / 龙息萃取物）
        this.addSlot(new SlotItemHandler(handler, 0, 26, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isMeltable(stack);
            }
        });

        // 1 = 物品输入
        this.addSlot(new SlotItemHandler(handler, 1, 26, 53));

        // 2 = 成品输出（与物品槽同一水平线）
        this.addSlot(new SlotItemHandler(handler, 2, 134, 53) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        // 玩家背包
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // 快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    /** 是否是可熔炼的晶体 */
    private static boolean isMeltable(ItemStack stack) {
        return stack.is(ModMaterials.ASHBLAZITE.get())
                || stack.is(ModBlocks.ASHBLAZITE_BLOCK.get().asItem())
                || stack.is(ModMaterials.DRAGON_BREATH_EXTRACT.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 3) {
            // 机器槽 → 玩家背包
            if (!this.moveItemStackTo(stack, 3, 39, true)) return ItemStack.EMPTY;
        } else {
            // 玩家背包 → 机器槽
            if (isMeltable(stack)) {
                // 晶体 → 优先槽 0
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // 其他物品 → 只尝试槽 1
                if (!this.moveItemStackTo(stack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(
                        blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, ModBlocks.CRYSTALLIZER.get());
    }

    public int getMeltProgress() { return data.get(0); }
    public int getMeltMaxProgress() { return data.get(1); }
    public int getCrystallizeProgress() { return data.get(2); }
    public int getCrystallizeMaxProgress() { return data.get(3); }
    public int getFluidAmount() { return data.get(4); }
    public int getFluidCapacity() { return data.get(5); }
    public CrystallizerBlockEntity getBlockEntity() { return blockEntity; }
}