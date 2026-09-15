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
        ingredient = stack.getOrDefault(TSDComponents.NAGA_INGREDIENT, "");
        cooked = stack.getOrDefault(TSDComponents.COOKED_ADVANCEMENT, false);
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public ItemStack writeItem(ItemStack stack, boolean wholeFeast) {
        if (!ingredient.isEmpty()) stack.set(TSDComponents.NAGA_INGREDIENT, ingredient);
        if (wholeFeast && cooked) stack.set(TSDComponents.COOKED_ADVANCEMENT, true);
        return stack;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ingredient = tag.getString("Ingredient");
        cooked = tag.getBoolean("Cooked");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Ingredient", ingredient);
        tag.putBoolean("Cooked", cooked);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
