package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import xy177.twilightsparksdelight.common.tile.TSDGloryCrucibleBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDFluids;

public final class TSDGloryCrucibleBlock extends Block implements EntityBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 6);
    private static final VoxelShape LEGS = Block.box(0, 0, 0, 16, 5, 16);
    private static final VoxelShape WALL_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape WALL_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape WALL_WEST = Block.box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape WALL_EAST = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE = Shapes.or(LEGS, WALL_NORTH, WALL_SOUTH, WALL_WEST, WALL_EAST);

    public TSDGloryCrucibleBlock(Properties properties) {
        super(properties.noOcclusion().sound(net.minecraft.world.level.block.SoundType.METAL)
                .isRedstoneConductor((state, level, pos) -> false));
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDGloryCrucibleBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                    BlockEntityType<T> type) {
        if (type != TSDBlockEntities.GLORY_CRUCIBLE.get()) {
            return null;
        }
        return (BlockEntityTicker<T>) (BlockEntityTicker<TSDGloryCrucibleBlockEntity>)
                TSDGloryCrucibleBlockEntity::tick;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TSDGloryCrucibleBlockEntity crucible)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        if (crucible.canUseAsWaterCauldron() && cleanWithWater(player, hand, stack, crucible)) {
            return ItemInteractionResult.SUCCESS;
        }
        var fruitInput = player.getAbilities().instabuild ? stack.copy() : stack;
        var fruit = xy177.twilightsparksdelight.integration.FruitsDelightCompat.process(crucible, fruitInput, false);
        if (fruit.matched()) {
            giveResult(player, hand, stack, fruit.output());
            if (!player.getAbilities().instabuild) giveResult(player, hand, stack, fruit.remainder());
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.is(twilightforest.init.TFItems.FIERY_BLOOD.get())
                || stack.is(twilightforest.init.TFItems.FIERY_TEARS.get())) {
            FluidStack input = new FluidStack(stack.is(twilightforest.init.TFItems.FIERY_BLOOD.get())
                    ? TSDFluids.FIERY_BLOOD.value() : TSDFluids.FIERY_TEARS.value(),
                    TSDGloryCrucibleBlockEntity.FIERY_ITEM_MB);
            if (crucible.fill(input, IFluidHandler.FluidAction.SIMULATE) != input.getAmount()) {
                return ItemInteractionResult.CONSUME;
            }
            crucible.fill(input, IFluidHandler.FluidAction.EXECUTE);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_LAVA,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof net.minecraft.world.item.PotionItem) {
            if (crucible.pourPotion(stack)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    giveResult(player, hand, stack, new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE));
                }
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
            }
            return ItemInteractionResult.CONSUME;
        }
        if (stack.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
            ItemStack filled = crucible.bottleFieryFluid();
            if (filled.isEmpty()) filled = crucible.bottlePotion();
            if (!filled.isEmpty()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                giveResult(player, hand, stack, filled);
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_FILL,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
            }
            return ItemInteractionResult.CONSUME;
        }
        if (FluidUtil.interactWithFluidHandler(player, hand, crucible)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (crucible.tryProcessIngredient(player.getAbilities().instabuild ? stack.copy() : stack)) {
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static boolean cleanWithWater(Player player, net.minecraft.world.InteractionHand hand,
                                         ItemStack held, TSDGloryCrucibleBlockEntity crucible) {
        if (held.is(net.minecraft.tags.ItemTags.DYEABLE)
                && held.has(net.minecraft.core.component.DataComponents.DYED_COLOR)) {
            held.remove(net.minecraft.core.component.DataComponents.DYED_COLOR);
            crucible.consumeBottle();
            player.awardStat(net.minecraft.stats.Stats.CLEAN_ARMOR);
            return true;
        }
        var patterns = held.getOrDefault(net.minecraft.core.component.DataComponents.BANNER_PATTERNS,
                net.minecraft.world.level.block.entity.BannerPatternLayers.EMPTY);
        if (held.getItem() instanceof net.minecraft.world.item.BannerItem && !patterns.layers().isEmpty()) {
            ItemStack clean = held.copyWithCount(1);
            clean.set(net.minecraft.core.component.DataComponents.BANNER_PATTERNS, patterns.removeLast());
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
                crucible.consumeBottle();
            }
            giveResult(player, hand, held, clean);
            player.awardStat(net.minecraft.stats.Stats.CLEAN_BANNER);
            return true;
        }
        return false;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        if (level.isClientSide || !entity.isOnFire()
                || !(level.getBlockEntity(pos) instanceof TSDGloryCrucibleBlockEntity crucible)) return;
        if (crucible.canUseAsWaterCauldron()
                && entity.getBoundingBox().minY <= pos.getY() + crucible.getSurfaceHeight()) {
            entity.clearFire();
            crucible.consumeBottle();
        }
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos,
                                    net.minecraft.world.level.biome.Biome.Precipitation precipitation) {
        if (!level.isClientSide && precipitation == net.minecraft.world.level.biome.Biome.Precipitation.RAIN
                && level.random.nextInt(20) == 1
                && level.getBlockEntity(pos) instanceof TSDGloryCrucibleBlockEntity crucible) {
            crucible.addFluidBottle(new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TSDGloryCrucibleBlockEntity crucible
                ? crucible.getLiquidLevel() : state.getValue(LEVEL);
    }

    private static void giveResult(Player player, net.minecraft.world.InteractionHand hand,
                                    ItemStack held, ItemStack result) {
        if (result.isEmpty()) return;
        if (held.isEmpty() && player.getItemInHand(hand).isEmpty()) player.setItemInHand(hand, result);
        else player.getInventory().placeItemBackInInventory(result);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.getBlockEntity(pos) instanceof TSDGloryCrucibleBlockEntity crucible
                && crucible.canBubble() && crucible.isHeated(level, pos) && random.nextInt(3) == 0) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE_POP,
                    pos.getX() + 0.2 + random.nextDouble() * 0.6, pos.getY() + crucible.getSurfaceHeight(),
                    pos.getZ() + 0.2 + random.nextDouble() * 0.6, 0, 0, 0);
        }
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }
}
