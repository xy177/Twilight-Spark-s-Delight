package xy177.twilightsparksdelight.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

public final class TSDDryingRackBlockEntity extends BlockEntity {
    private int progress;
    private boolean loading;
    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return level != null && level.getRecipeManager().getRecipeFor(TSDRecipeSerializers.DRYING_TYPE.get(),
                    new SimpleContainer(stack), level).isPresent();
        }
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!getStackInSlot(slot).isEmpty()) return stack;
            ItemStack remainder = super.insertItem(slot, stack.copyWithCount(1), simulate);
            return remainder.isEmpty() ? stack.copyWithCount(stack.getCount() - 1) : stack;
        }
        @Override
        protected void onContentsChanged(int slot) {
            if (loading) return;
            progress = 0;
            sync();
        }
    };
    private LazyOptional<IItemHandler> capability = LazyOptional.of(() -> items);

    public TSDDryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.DRYING_RACK.get(), pos, state);
    }

    public ItemStack content() { return items.getStackInSlot(0); }
    public ItemStack insert(ItemStack stack) { return items.insertItem(0, stack, false); }
    public ItemStack extract() { return items.extractItem(0, content().getCount(), false); }

    public static void tick(Level level, BlockPos pos, BlockState state, TSDDryingRackBlockEntity rack) {
        if (level.isClientSide || rack.content().isEmpty()) return;
        var recipe = level.getRecipeManager().getRecipeFor(TSDRecipeSerializers.DRYING_TYPE.get(),
                new SimpleContainer(rack.content()), level);
        if (recipe.isEmpty() || level.isRainingAt(pos)) return;
        rack.progress++;
        rack.setChanged();
        if (rack.progress >= recipe.get().dryingTicks()) {
            rack.items.setStackInSlot(0, recipe.get().result().copy());
        }
    }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Item", content().save(new CompoundTag()));
        tag.putInt("Progress", progress);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loading = true;
        items.setStackInSlot(0, ItemStack.of(tag.getCompound("Item")));
        progress = Math.max(0, tag.getInt("Progress"));
        loading = false;
    }
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? capability.cast() : super.getCapability(cap, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); capability.invalidate(); }
    @Override public void reviveCaps() { super.reviveCaps(); capability = LazyOptional.of(() -> items); }
}
