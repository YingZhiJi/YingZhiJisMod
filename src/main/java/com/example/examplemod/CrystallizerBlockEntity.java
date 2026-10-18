package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrystallizerBlockEntity extends BlockEntity implements MenuProvider {

    // 槽位：0 = 燃灰/龙息萃取物输入，1 = 物品输入，2 = 成品输出
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final FluidTank fluidTank = new FluidTank(2400) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(),
                        Block.UPDATE_ALL);
            }
        }
    };

    private int meltProgress = 0;
    private int meltMaxProgress = 40;

    private int crystallizeProgress = 0;
    private int crystallizeMaxProgress = 160;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> meltProgress;
                case 1 -> meltMaxProgress;
                case 2 -> crystallizeProgress;
                case 3 -> crystallizeMaxProgress;
                case 4 -> fluidTank.getFluidAmount();
                case 5 -> fluidTank.getCapacity();
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> meltProgress = value;
                case 1 -> meltMaxProgress = value;
                case 2 -> crystallizeProgress = value;
                case 3 -> crystallizeMaxProgress = value;
            }
        }
        @Override public int getCount() { return 6; }
    };

    public CrystallizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTALLIZER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  CrystallizerBlockEntity be) {
        boolean changed = false;

        // ===== 互斥：物品槽有东西 → 冻结融化 =====
        boolean itemSlotOccupied = !be.itemHandler.getStackInSlot(1).isEmpty();

        if (!itemSlotOccupied && be.canMelt()) {
            be.meltProgress++;
            if (be.meltProgress >= be.meltMaxProgress) {
                be.meltProgress = 0;
                be.doMelt();
                changed = true;
            }
        } else if (be.meltProgress > 0) {
            be.meltProgress = 0;
            changed = true;
        }

        // ===== 修复优先于结晶 =====
        if (be.canRepair()) {
            be.crystallizeProgress++;
            if (be.crystallizeProgress >= be.crystallizeMaxProgress) {
                be.crystallizeProgress = 0;
                be.doRepair();
                changed = true;
            }
        } else if (be.canCrystallize()) {
            be.crystallizeProgress++;
            if (be.crystallizeProgress >= be.crystallizeMaxProgress) {
                be.crystallizeProgress = 0;
                be.doCrystallize();
                changed = true;
            }
        } else if (be.crystallizeProgress > 0) {
            be.crystallizeProgress = 0;
            changed = true;
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    // ===== 融化逻辑 =====
    /** 返回该物品对应的熔融产物流体，无法融化返回 null */
    private Fluid getMeltResult(ItemStack stack) {
        if (stack.is(ModMaterials.ASHBLAZITE.get())
                || stack.is(ModBlocks.ASHBLAZITE_BLOCK.get().asItem())) {
            return ModFluids.MOLTEN_ASHBLAZITE.get();
        }
        if (stack.is(ModMaterials.DRAGON_BREATH_EXTRACT.get())) {
            return ModFluids.MOLTEN_DRAGON_BREATH.get();
        }
        return null;
    }

    private int getMeltAmount(ItemStack stack) {
        if (stack.is(ModMaterials.ASHBLAZITE.get())) return 25;
        if (stack.is(ModBlocks.ASHBLAZITE_BLOCK.get().asItem())) return 225;
        if (stack.is(ModMaterials.DRAGON_BREATH_EXTRACT.get())) return 200;
        return 0;
    }

    private int getMeltTime(ItemStack stack) {
        if (stack.is(ModMaterials.ASHBLAZITE.get())) return 40;
        if (stack.is(ModBlocks.ASHBLAZITE_BLOCK.get().asItem())) return 120;
        if (stack.is(ModMaterials.DRAGON_BREATH_EXTRACT.get())) return 60;
        return 40;
    }

    private boolean canMelt() {
        ItemStack input = itemHandler.getStackInSlot(0);
        if (input.isEmpty()) return false;

        int amount = getMeltAmount(input);
        if (amount <= 0) return false;
        if (fluidTank.getFluidAmount() + amount > fluidTank.getCapacity()) return false;

        Fluid result = getMeltResult(input);
        if (result == null) return false;

        // 储罐必须为空，或已经是同种流体（不同晶体不能混炼）
        FluidStack existing = fluidTank.getFluid();
        if (!existing.isEmpty() && existing.getFluid() != result) return false;

        this.meltMaxProgress = getMeltTime(input);
        return true;
    }

    private void doMelt() {
        ItemStack input = itemHandler.getStackInSlot(0);
        int amount = getMeltAmount(input);
        if (amount <= 0) return;

        Fluid result = getMeltResult(input);
        if (result == null) return;

        FluidStack toAdd = new FluidStack(result, amount);
        fluidTank.fill(toAdd, IFluidHandler.FluidAction.EXECUTE);
        itemHandler.extractItem(0, 1, false);
    }

    // ===== 修复逻辑 =====
    // 燃灰工具 + 熔融燃灰：1 mB = 2 耐久
    // 末地闪光 + 熔融龙息：1 mB = 1 耐久
    private boolean canRepair() {
        ItemStack input = itemHandler.getStackInSlot(1);
        if (input.isEmpty()) return false;

        int damage = input.getDamageValue();
        if (damage <= 0) return false;

        Fluid requiredFluid;
        int needed;

        if (AshblaziteTools.isAshblaziteTool(input)) {
            requiredFluid = ModFluids.MOLTEN_ASHBLAZITE.get();
            needed = (damage + 1) / 2;
        } else if (input.is(ExampleMod.END_FLASH.get())) {
            requiredFluid = ModFluids.MOLTEN_DRAGON_BREATH.get();
            needed = damage;
        } else {
            return false;
        }

        if (fluidTank.getFluid().getFluid() != requiredFluid) return false;
        if (fluidTank.getFluidAmount() < needed) return false;

        // 输出槽必须是空的（修复不合并）
        ItemStack existing = itemHandler.getStackInSlot(2);
        if (!existing.isEmpty()) return false;

        this.crystallizeMaxProgress = 160;
        return true;
    }

    private void doRepair() {
        ItemStack input = itemHandler.getStackInSlot(1);
        int damage = input.getDamageValue();

        Fluid requiredFluid;
        int needed;

        if (AshblaziteTools.isAshblaziteTool(input)) {
            requiredFluid = ModFluids.MOLTEN_ASHBLAZITE.get();
            needed = (damage + 1) / 2;
        } else if (input.is(ExampleMod.END_FLASH.get())) {
            requiredFluid = ModFluids.MOLTEN_DRAGON_BREATH.get();
            needed = damage;
        } else {
            return;
        }

        fluidTank.drain(needed, IFluidHandler.FluidAction.EXECUTE);

        // copy() 保留 NBT（附魔、名字等），setDamageValue(0) 恢复满耐久
        ItemStack output = input.copy();
        output.setCount(1);
        output.setDamageValue(0);

        itemHandler.extractItem(1, 1, false);
        itemHandler.setStackInSlot(2, output);
    }

    // ===== 结晶逻辑 =====
    private boolean canCrystallize() {
        ItemStack input = itemHandler.getStackInSlot(1);
        if (input.isEmpty()) return false;

        Optional<CrystallizerCrystallizingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.CRYSTALLIZER_CRYSTALLIZING.get(),
                        new SimpleContainer(input), level);
        if (recipe.isEmpty()) return false;

        CrystallizerCrystallizingRecipe r = recipe.get();
        FluidStack required = r.getInputFluid();
        if (fluidTank.getFluid().getFluid() != required.getFluid()) return false;
        if (fluidTank.getFluidAmount() < required.getAmount()) return false;

        ItemStack output = r.getOutputItem();
        ItemStack existing = itemHandler.getStackInSlot(2);
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(existing, output)) return false;
            if (existing.getCount() + output.getCount() > existing.getMaxStackSize()) return false;
        }

        this.crystallizeMaxProgress = r.getDuration();
        return true;
    }

    private void doCrystallize() {
        ItemStack input = itemHandler.getStackInSlot(1);
        Optional<CrystallizerCrystallizingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.CRYSTALLIZER_CRYSTALLIZING.get(),
                        new SimpleContainer(input), level);
        if (recipe.isEmpty()) return;

        CrystallizerCrystallizingRecipe r = recipe.get();

        fluidTank.drain(r.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        // 输出物品：满耐久 + 继承输入的附魔/NBT
        ItemStack output = r.getOutputItem().copy();
        output.setCount(1);
        output.setDamageValue(0);

        CompoundTag inputTag = input.getTag();
        if (inputTag != null) {
            CompoundTag outTag = inputTag.copy();
            // 去掉耐久和铁砧惩罚，其他（附魔、名字、Unbreakable 等）都保留
            outTag.remove("Damage");
            outTag.remove("RepairCost");
            output.setTag(outTag);
            output.setDamageValue(0);
        }

        itemHandler.extractItem(1, 1, false);

        ItemStack existing = itemHandler.getStackInSlot(2);
        if (existing.isEmpty()) {
            itemHandler.setStackInSlot(2, output);
        } else {
            existing.grow(output.getCount());
        }
    }

    // ===== MenuProvider =====
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.yingzhiji_s_mod.crystallizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CrystallizerMenu(id, inv, this, this.data);
    }

    // ===== Capability =====
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    // ===== NBT 保存 =====
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.put("FluidTank", fluidTank.writeToNBT(new CompoundTag()));
        tag.putInt("MeltProgress", meltProgress);
        tag.putInt("MeltMaxProgress", meltMaxProgress);
        tag.putInt("CrystallizeProgress", crystallizeProgress);
        tag.putInt("CrystallizeMaxProgress", crystallizeMaxProgress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("FluidTank")) {
            fluidTank.readFromNBT(tag.getCompound("FluidTank"));
        }
        if (tag.contains("MeltProgress")) {
            meltProgress = tag.getInt("MeltProgress");
        }
        if (tag.contains("MeltMaxProgress")) {
            meltMaxProgress = tag.getInt("MeltMaxProgress");
        }
        if (tag.contains("CrystallizeProgress")) {
            crystallizeProgress = tag.getInt("CrystallizeProgress");
        }
        if (tag.contains("CrystallizeMaxProgress")) {
            crystallizeMaxProgress = tag.getInt("CrystallizeMaxProgress");
        }
    }

    // ===== 方块实体网络同步 =====
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("FluidTank", fluidTank.writeToNBT(new CompoundTag()));
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("FluidTank")) {
            fluidTank.readFromNBT(tag.getCompound("FluidTank"));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ===== Getter =====
    public ItemStackHandler getItemHandler() { return itemHandler; }
    public FluidTank getFluidTank() { return fluidTank; }
    public int getMeltProgress() { return meltProgress; }
    public int getMeltMaxProgress() { return meltMaxProgress; }
    public int getCrystallizeProgress() { return crystallizeProgress; }
    public int getCrystallizeMaxProgress() { return crystallizeMaxProgress; }
}