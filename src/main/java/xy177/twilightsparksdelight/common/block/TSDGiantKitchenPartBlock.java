package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class TSDGiantKitchenPartBlock extends Block {
    public enum Kind {
        STOVE,
        COOKING_POT
    }

    private final Supplier<? extends Block> controller;
    private final Kind kind;

    public TSDGiantKitchenPartBlock(Properties properties, Supplier<? extends Block> controller, Kind kind) {
        super(properties.noOcclusion().lightLevel(state ->
                state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 13 : 0));
        this.controller = controller;
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(
                net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);
    }

    @Override
    public net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.INVISIBLE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                           Player player, net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        BlockPos controllerPos = TSDGiantKitchenStructure.findController(level, pos, controller.get());
        if (controllerPos == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockState controllerState = level.getBlockState(controllerPos);
        BlockHitResult redirected = new BlockHitResult(
                hit.getLocation(), hit.getDirection(), controllerPos, hit.isInside());
        if (controller.get() instanceof TSDGiantKitchenController kitchenController) {
            return kitchenController.useOnPart(held, controllerState, level, controllerPos, player, hand, redirected);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        ItemInteractionResult result = useItemOn(ItemStack.EMPTY, state, level, pos, player,
                net.minecraft.world.InteractionHand.MAIN_HAND, hit);
        return result.result();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !TSDGiantKitchenStructure.isRemoving()) {
            TSDGiantKitchenStructure.removeControllerFromPart(level, pos, controller.get(), this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (kind == Kind.STOVE && entity instanceof net.minecraft.world.entity.LivingEntity
                && !entity.isSteppingCarefully() && isControllerLit(level, pos)) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (kind == Kind.COOKING_POT && level instanceof Level actualLevel) {
            return TSDGiantKitchenStructure.scaledCookingPotShape(actualLevel, pos,
                    controller.get(), this);
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        if (kind == Kind.COOKING_POT && level instanceof Level actualLevel) {
            return TSDGiantKitchenStructure.scaledCookingPotShape(actualLevel, pos,
                    controller.get(), this);
        }
        return Shapes.block();
    }

    private boolean isControllerLit(Level level, BlockPos partPos) {
        BlockPos controllerPos = TSDGiantKitchenStructure.findController(level, partPos, controller.get());
        if (controllerPos == null) {
            return false;
        }
        BlockState controllerState = level.getBlockState(controllerPos);
        return controllerState.getOptionalValue(
                net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT).orElse(false);
    }
}
