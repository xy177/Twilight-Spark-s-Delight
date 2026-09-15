package xy177.twilightsparksdelight.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenController;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDBlocks;

/**
 * The regular giant stove has six processing positions; the 4x4 Giant's Stove
 * exposes sixteen positions while retaining Farmer's Delight's stove contract.
 */
public class TSDGiantStoveBlockEntity extends AbstractStoveBlockEntity {
    public TSDGiantStoveBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.GIANT_STOVE.get(), pos, state, RecipeType.CAMPFIRE_COOKING);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TSDBlockEntities.GIANT_STOVE.get();
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return TSDGiantKitchenStructure.bounds(worldPosition, getBlockState()).inflate(0.1);
    }

    @Override
    public boolean shouldDropItems() {
        if (level == null) {
            return false;
        }

        /*
         * Farmer's Delight checks only the controller block.  The migrated
         * giant stove has a cooking surface spanning the complete structure,
         * so every top cell must be clear before cooking can continue.
         */
        int size = getStructureSize();
        Direction facing = TSDGiantKitchenStructure.controllerFacing(getBlockState());
        for (BlockPos top : TSDGiantKitchenStructure.positions(worldPosition, facing, size)) {
            if (top.getY() != worldPosition.getY() + size - 1) {
                continue;
            }
            BlockPos above = top.above();
            VoxelShape collision = level.getBlockState(above).getCollisionShape(level, above);
            if (!collision.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int getInventorySlotCount() {
        return isLarge(getBlockState()) ? 16 : 6;
    }

    @Override
    public int getNextEmptySlot() {
        int slotLimit = isLarge(getBlockState())
                ? Math.min(getItems().getSlots(),
                        Math.max(1, xy177.twilightsparksdelight.TSDConfig.GIANTS_STOVE_MAX_PORTIONS.get()))
                : getItems().getSlots();
        for (int slot = 0; slot < slotLimit; slot++) {
            if (getItems().getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public Vec2 getStoveItemOffset(int slot) {
        int columns = isLarge(getBlockState()) ? 4 : 3;
        int rows = (getInventorySlotCount() + columns - 1) / columns;
        float columnSpacing = isLarge(getBlockState()) ? 0.9F : 0.6F;
        float rowSpacing = 0.8F;
        return new Vec2(
                (columns - 1) * 0.5F * columnSpacing - (slot % columns) * columnSpacing,
                (rows - 1) * 0.5F * rowSpacing - (slot / columns) * rowSpacing
        );
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state,
                                    TSDGiantStoveBlockEntity blockEntity) {
        ItemStackHandler items = blockEntity.getItems();
        RandomSource random = level.random;
        Direction facing = state.getValue(StoveBlock.FACING).getOpposite();
        int size = blockEntity.getStructureSize();
        var center = TSDGiantKitchenStructure.bounds(pos, state).getCenter();
        double centerX = center.x;
        double centerZ = center.z;
        int portionLimit = isLarge(state)
                ? Math.min(items.getSlots(), Math.max(1, xy177.twilightsparksdelight.TSDConfig.GIANTS_STOVE_MAX_PORTIONS.get()))
                : items.getSlots();
        for (int slot = 0; slot < portionLimit; slot++) {
            if (items.getStackInSlot(slot).isEmpty() || random.nextFloat() >= 0.2F) {
                continue;
            }
            Vec2 offset = blockEntity.getStoveItemOffset(slot);
            float xOffset = offset.x;
            float zOffset = offset.y;
            if (facing.getAxis() == Direction.Axis.Z) {
                float swap = xOffset;
                xOffset = zOffset;
                zOffset = swap;
            }
            double x = centerX - facing.getStepX() * zOffset
                    + facing.getClockWise().getStepX() * xOffset;
            double z = centerZ - facing.getStepZ() * zOffset
                    + facing.getClockWise().getStepZ() * xOffset;
            level.addParticle(ParticleTypes.SMOKE, x, pos.getY() + size + 0.02D, z,
                    0.0D, 0.0005D, 0.0D);
        }
    }

    private int getStructureSize() {
        return getBlockState().getBlock() instanceof TSDGiantKitchenController controller
                ? controller.getStructureSize() : 2;
    }

    private static boolean isLarge(BlockState state) {
        return state.is(TSDBlocks.GIANTS_STOVE.get());
    }
}
