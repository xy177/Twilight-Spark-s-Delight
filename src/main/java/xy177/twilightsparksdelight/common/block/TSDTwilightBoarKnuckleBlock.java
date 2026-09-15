package xy177.twilightsparksdelight.common.block;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModSounds;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TSDTwilightBoarKnuckleBlock extends TSDLargeStageFeastBlock {
    public TSDTwilightBoarKnuckleBlock(Properties properties, Supplier<TSDStructurePartBlock> part) {
        super(properties, "stage", 6, TSDItems.PLATE_OF_TWILIGHT_BOAR_KNUCKLE, true, 2, 1, part);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Items.BOWL)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (!player.isCreative()) held.shrink(1);
            ItemStack serving = new ItemStack(TSDItems.PLATE_OF_TWILIGHT_BOAR_KNUCKLE.get());
            if (!player.getInventory().add(serving)) player.drop(serving, false);
            if (currentStage(state) >= 5) {
                for (ItemStack drop : finalDrops()) popResource(level, pos, drop);
                level.removeBlock(pos, false);
            } else {
                level.setBlock(pos, state.setValue(STAGE, currentStage(state) + 1), UPDATE_ALL);
            }
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected List<ItemStack> finalDrops() {
        return List.of(new ItemStack(Items.BOWL), new ItemStack(Items.BONE_MEAL, 7));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return cellShape(state.getValue(FACING));
    }

    public static VoxelShape cellShape(net.minecraft.core.Direction facing) {
        return switch (facing) {
            case NORTH -> box(0, 0, 1, 16, 9, 16);
            case EAST -> box(0, 0, 0, 15, 9, 16);
            case SOUTH -> box(0, 0, 0, 16, 9, 15);
            case WEST -> box(1, 0, 0, 16, 9, 16);
            default -> throw new IllegalArgumentException("Expected horizontal facing");
        };
    }
}
