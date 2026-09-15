package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import twilightforest.init.TFItems;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFStructures;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.tile.TSDUnripePickledBrackenJarBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDBlocks;

public final class TSDUnripePickledBrackenJarBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 8);
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 16, 13);

    public TSDUnripePickledBrackenJarBlock(Properties properties) {
        super(properties.noOcclusion().sound(net.minecraft.world.level.block.SoundType.GLASS)
                .randomTicks().pushReaction(PushReaction.DESTROY));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LEVEL, 0));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDUnripePickledBrackenJarBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                   BlockEntityType<T> type) {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LEVEL, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        TSDUnripePickledBrackenJarBlockEntity jar = getJar(level, pos);
        if (jar == null) {
            return;
        }
        if (jar.getProgress() >= TSDUnripePickledBrackenJarBlockEntity.MAX_PROGRESS) {
            ripen(level, pos, state);
            return;
        }

        double chance = getRipeningChance(level, pos, jar);
        int gained = rollProgress(random, chance);
        if (gained <= 0) {
            return;
        }
        jar.addProgress(gained);
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        if (jar.getProgress() >= TSDUnripePickledBrackenJarBlockEntity.MAX_PROGRESS) {
            ripen(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        if (held.is(TFItems.POCKET_WATCH.get())) {
            if (!level.isClientSide) {
                ripen((ServerLevel) level, pos, state);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable(
                            "twilight_spark_delight.block.unripe_pickled_bracken_jar.not_ready"), true);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void ripen(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, TSDBlocks.PICKLED_BRACKEN_JAR.get().defaultBlockState()
                .setValue(TSDPickledBrackenJarBlock.FACING, state.getValue(FACING))
                .setValue(TSDPickledBrackenJarBlock.LEVEL, 0), Block.UPDATE_ALL);
    }

    private static TSDUnripePickledBrackenJarBlockEntity getJar(Level level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof TSDUnripePickledBrackenJarBlockEntity jar ? jar : null;
    }

    private static int rollProgress(RandomSource random, double chance) {
        int progress = 0;
        while (chance > 0.0D) {
            double roll = Math.min(1.0D, chance);
            if (random.nextDouble() > roll) {
                break;
            }
            progress++;
            chance -= 1.0D;
        }
        return progress;
    }

    public static double getRipeningChance(ServerLevel level, BlockPos pos,
                                            TSDUnripePickledBrackenJarBlockEntity jar) {
        double chance = TSDConfig.PICKLED_BRACKEN_JAR_BASE_RIPENING_CHANCE.get();
        BlockPos shade = findShade(level, pos);
        if (shade != null) {
            chance += level.getBlockState(shade).is(net.minecraft.tags.BlockTags.LEAVES)
                    ? TSDConfig.PICKLED_BRACKEN_JAR_LEAF_SHADE_BONUS.get()
                    : TSDConfig.PICKLED_BRACKEN_JAR_SHADE_BONUS.get();
            if (isCleanShadeArea(level, shade)) {
                chance += TSDConfig.PICKLED_BRACKEN_JAR_CLEAN_AREA_BONUS.get();
            }
        }
        if (hasNearby(level, pos, TFBlocks.TIME_LOG_CORE.get())) {
            chance += TSDConfig.PICKLED_BRACKEN_JAR_MAGIC_LOG_BONUS.get();
        }
        if (hasNearby(level, pos, TSDBlocks.LABYRINTH_MUSHROOM_COLONY.get())) {
            chance += TSDConfig.PICKLED_BRACKEN_JAR_MUSHROOM_COLONY_BONUS.get();
        }
        if (jar.wasPeacockFanUsed()) {
            chance += TSDConfig.PICKLED_BRACKEN_JAR_PEACOCK_FAN_BONUS.get();
        }
        if (isInAcceleratingStructure(level, pos)) {
            chance += TSDConfig.PICKLED_BRACKEN_JAR_STRUCTURE_BONUS.get();
        }
        return chance;
    }

    private static boolean isInAcceleratingStructure(ServerLevel level, BlockPos pos) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var structures = java.util.Set.of(TFStructures.HOLLOW_HILL_SMALL, TFStructures.HOLLOW_HILL_MEDIUM,
                TFStructures.HOLLOW_HILL_LARGE, TFStructures.MUSHROOM_TOWER, TFStructures.LABYRINTH,
                TFStructures.KNIGHT_STRONGHOLD, TFStructures.QUEST_GROVE);
        for (var start : level.structureManager().startsForStructure(
                new net.minecraft.world.level.ChunkPos(pos),
                structure -> registry.getResourceKey(structure).map(structures::contains).orElse(false))) {
            var bounds = start.getBoundingBox();
            if (pos.getX() >= bounds.minX() && pos.getX() <= bounds.maxX()
                    && pos.getZ() >= bounds.minZ() && pos.getZ() <= bounds.maxZ()) return true;
        }
        return false;
    }

    private static BlockPos findShade(ServerLevel level, BlockPos pos) {
        for (int y = 1; y <= 5; y++) {
            BlockPos candidate = pos.above(y);
            if (!level.getBlockState(candidate).isAir()) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean isCleanShadeArea(ServerLevel level, BlockPos center) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockState state = level.getBlockState(center.offset(dx, 0, dz));
                if (!state.isAir() && !isCatalystBlock(state)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isCatalystBlock(BlockState state) {
        return state.is(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get())
                || state.is(TSDBlocks.PICKLED_BRACKEN_JAR.get())
                || state.is(TSDBlocks.LABYRINTH_MUSHROOM_COLONY.get())
                || state.is(TFBlocks.TIME_LOG_CORE.get())
                || state.is(TFBlocks.TIME_LOG.get())
                || state.is(TFBlocks.TIME_LEAVES.get())
                || state.is(TFBlocks.TIME_SAPLING.get());
    }

    private static boolean hasNearby(ServerLevel level, BlockPos pos, Block block) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (level.getBlockState(pos.offset(dx, 0, dz)).is(block)) {
                    return true;
                }
            }
        }
        return false;
    }
}
