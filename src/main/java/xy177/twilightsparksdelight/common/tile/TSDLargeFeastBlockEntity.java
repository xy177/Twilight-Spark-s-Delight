package xy177.twilightsparksdelight.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDComponents;

public final class TSDLargeFeastBlockEntity extends BlockEntity {
    private String ingredient = "";
    private boolean cooked;

    public TSDLargeFeastBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.LARGE_FEAST.get(), pos, state);
    }

    public void readItem(ItemStack stack) {
        ingredient = TSDComponents.NAGA_INGREDIENT.getOrDefault(stack, "");
        cooked = TSDComponents.COOKED_ADVANCEMENT.getOrDefault(stack, false);
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public ItemStack writeItem(ItemStack stack, boolean wholeFeast) {
        if (!ingredient.isEmpty()) TSDComponents.NAGA_INGREDIENT.set(stack, ingredient);
        if (wholeFeast && cooked) TSDComponents.COOKED_ADVANCEMENT.set(stack, true);
        return stack;
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return ((xy177.twilightsparksdelight.common.block.TSDLargeStageFeastBlock) getBlockState().getBlock())
                .structureBounds(worldPosition, getBlockState()).inflate(0.25);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ingredient = tag.getString("Ingredient");
        cooked = tag.getBoolean("Cooked");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Ingredient", ingredient);
        tag.putBoolean("Cooked", cooked);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
