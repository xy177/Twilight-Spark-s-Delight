package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFParticleType;
import twilightforest.init.TFSounds;
import xy177.twilightsparksdelight.common.tile.TSDMasonJarBlockEntity;

public final class TSDMasonJarBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty LIGHT = BlockStateProperties.LEVEL;
    private static final VoxelShape SHAPE = box(3, 0, 3, 13, 16, 13);

    public TSDMasonJarBlock(Properties properties) {
        super(properties.noOcclusion().lightLevel(state -> state.getValue(LIGHT)));
        registerDefaultState(stateDefinition.any().setValue(LIGHT, 0)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED,
                context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState other,
                                  LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, other, level, pos, otherPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDMasonJarBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof TSDMasonJarBlockEntity jar) {
            jar.setRotation(net.minecraft.util.Mth.floor((placer.getYRot() + 180) * 16 / 360 + 0.5F));
            if (stack.hasCustomHoverName()) jar.setCustomName(stack.getHoverName());
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof TSDMasonJarBlockEntity jar)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack held = player.getItemInHand(hand);
        boolean positive = false;
        if (held.is(ItemTags.LOGS) && hit.getLocation().y - pos.getY() >= 0.875
                && !ItemStack.isSameItemSameTags(held, jar.lid())) {
            jar.setLid(held);
            positive = true;
        } else if (held.isEmpty()) {
            if (player.isShiftKeyDown() && isCritter(jar.content())) {
                popResource(level, pos, jar.extract());
                popResource(level, pos, jar.asItem());
                level.removeBlock(pos, false);
                return InteractionResult.CONSUME;
            }
            if (player.isShiftKeyDown() && !jar.content().isEmpty()) {
                player.displayClientMessage(Component.translatable("twilight_spark_delight.tooltip.glass_jar.contents",
                        jar.content().getHoverName(), jar.content().getCount()), true);
            } else if (!jar.content().isEmpty()) {
                player.setItemInHand(hand, jar.extract());
            }
        } else {
            ItemStack remainder = jar.insert(held.copy());
            if (remainder.getCount() < held.getCount()) {
                if (!player.getAbilities().instabuild) player.setItemInHand(hand, remainder);
                jar.setRotation(net.minecraft.util.Mth.floor((player.getYRot() + 180) * 16 / 360 + 0.5F));
                positive = true;
            }
        }
        jar.wobble(positive);
        level.playSound(null, pos, positive ? SoundEvents.ITEM_PICKUP : SoundEvents.GLASS_HIT,
                SoundSource.BLOCKS, 1, 1);
        return InteractionResult.CONSUME;
    }

    public static boolean isCritter(ItemStack stack) {
        return stack.is(TFBlocks.FIREFLY.get().asItem()) || stack.is(TFBlocks.CICADA.get().asItem());
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        var entity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return java.util.List.of(entity instanceof TSDMasonJarBlockEntity jar
                ? jar.asItem() : new ItemStack(this));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level,
                                      BlockPos pos, Player player) {
        return level.getBlockEntity(pos) instanceof TSDMasonJarBlockEntity jar
                ? jar.asItem() : new ItemStack(this);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof TSDMasonJarBlockEntity jar) || jar.content().isEmpty()) return 0;
        return 1 + 14 * jar.content().getCount() / jar.content().getMaxStackSize();
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int value) {
        var entity = level.getBlockEntity(pos);
        return entity != null && entity.triggerEvent(id, value);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof TSDMasonJarBlockEntity jar)) return;
        if (jar.content().is(TFBlocks.FIREFLY.get().asItem())) {
            level.addParticle(TFParticleType.FIREFLY.get(), pos.getX() + 0.3 + random.nextFloat() * 0.4,
                    pos.getY() + 0.3 + random.nextFloat() * 0.4, pos.getZ() + 0.3 + random.nextFloat() * 0.4, 0, 0, 0);
        } else if (jar.content().is(TFBlocks.CICADA.get().asItem())) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.NOTE,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, random.nextFloat(), 0, 0);
            if (random.nextInt(75) == 0) level.playLocalSound(pos, TFSounds.CICADA.get(),
                    SoundSource.BLOCKS, 1, 1, false);
        }
    }
}
