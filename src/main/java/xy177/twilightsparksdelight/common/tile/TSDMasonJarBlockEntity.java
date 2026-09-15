package xy177.twilightsparksdelight.common.tile;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import twilightforest.init.TFBlocks;
import xy177.twilightsparksdelight.common.block.TSDMasonJarBlock;
import xy177.twilightsparksdelight.common.item.TSDMasonJarItem;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TSDMasonJarBlockEntity extends BlockEntity {
    private boolean loading;
    private ItemStack lid = ItemStack.EMPTY;
    private int rotation;
    private net.minecraft.network.chat.Component customName;
    public long wobbleStarted;
    public boolean positiveWobble;
    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !(stack.getItem() instanceof TSDMasonJarItem)
                    && !(stack.getItem() instanceof BlockItem block
                    && block.getBlock() instanceof ShulkerBoxBlock)
                    && stack.getItem().canFitInsideContainerItems();
        }

        @Override
        protected void onContentsChanged(int slot) {
            sync();
        }
    };
    private LazyOptional<net.minecraftforge.items.IItemHandler> capability = LazyOptional.of(() -> items);

    public TSDMasonJarBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.MASON_JAR.get(), pos, state);
    }

    public ItemStack content() {
        return items.getStackInSlot(0);
    }

    public ItemStack lid() {
        return lid.isEmpty() ? TFBlocks.TWILIGHT_OAK_LOG.get().asItem().getDefaultInstance() : lid;
    }

    public int rotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation & 15;
        sync();
    }

    public void setLid(ItemStack stack) {
        lid = stack.copyWithCount(1);
        sync();
    }

    public void setCustomName(net.minecraft.network.chat.Component name) {
        customName = name;
        sync();
    }

    public ItemStack insert(ItemStack stack) {
        return items.insertItem(0, stack, false);
    }

    public ItemStack extract() {
        return items.extractItem(0, content().getCount(), false);
    }

    public ItemStack asItem() {
        var stack = new ItemStack(TSDItems.MASON_JAR.get());
        CompoundTag tag = saveWithoutMetadata();
        // Orientation belongs to the placed jar, not its stack identity.
        tag.remove("Rotation");
        if (!content().isEmpty() || !lid.isEmpty()) stack.addTagElement("BlockEntityTag", tag);
        if (customName != null) stack.setHoverName(customName);
        return stack;
    }

    public void wobble(boolean positive) {
        if (level != null) level.blockEvent(worldPosition, getBlockState().getBlock(), 1, positive ? 1 : 0);
    }

    @Override
    public boolean triggerEvent(int id, int value) {
        if (id != 1) return super.triggerEvent(id, value);
        wobbleStarted = level == null ? 0 : level.getGameTime();
        positiveWobble = value == 1;
        return true;
    }

    private void sync() {
        if (loading) return;
        setChanged();
        if (level != null && !level.isClientSide) {
            int light = content().getItem() instanceof BlockItem block
                    ? block.getBlock().defaultBlockState().getLightEmission() : 0;
            BlockState state = getBlockState();
            if (state.getValue(TSDMasonJarBlock.LIGHT) != light) {
                level.setBlock(worldPosition, state.setValue(TSDMasonJarBlock.LIGHT, light), 3);
            }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!content().isEmpty()) tag.put("Item", content().save(new CompoundTag()));
        if (!lid.isEmpty()) tag.put("Lid", lid.save(new CompoundTag()));
        tag.putInt("Rotation", rotation);
        if (customName != null) tag.putString("CustomName", net.minecraft.network.chat.Component.Serializer.toJson(customName));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loading = true;
        items.setStackInSlot(0, ItemStack.of(tag.getCompound("Item")));
        lid = ItemStack.of(tag.getCompound("Lid"));
        rotation = tag.getInt("Rotation") & 15;
        customName = tag.contains("CustomName") ? net.minecraft.network.chat.Component.Serializer.fromJson(tag.getString("CustomName")) : null;
        loading = false;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER && (side == null || side == Direction.UP)
                ? capability.cast() : super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        capability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        capability = LazyOptional.of(() -> items);
    }
}
