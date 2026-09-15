package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.registry.ModSounds;
import xy177.twilightsparksdelight.registry.TSDItems;

/**
 * Four-bite abyss pie. A knife always produces the visible slice item; direct
 * eating applies the same food through the normal consumable item.
 */
public final class TSDAbyssPieBlock extends TSDBitesFeastBlock {
    public TSDAbyssPieBlock(Properties properties) {
        super(properties.randomTicks(), 4, TSDItems.ABYSS_PIE_SLICE, false);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,
            net.minecraft.world.level.BlockGetter level, BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context) {
        return box(0, 0, 0, 16, 8, 16);
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.level.ServerLevel level,
                            BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextFloat() >= 0.5F) return;
        var players = level.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(pos).inflate(8));
        if (players.isEmpty()) return;
        var target = players.get(random.nextInt(players.size()));
        level.playSound(null, target.blockPosition(), SoundEvents.AMBIENT_CAVE.value(),
                SoundSource.AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public InteractionResult use(BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        int bites = state.getValue(BITES);
        if (isKnife(held)) {
            if (!level.isClientSide) {
                give(player, new ItemStack(TSDItems.ABYSS_PIE_SLICE.get()));
                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(hand));
                }
                advance(level, pos, state, bites);
                level.playSound(null, pos, ModSounds.BLOCK_FOOD_SLICE.get(),
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            ItemStack food = new ItemStack(TSDItems.ABYSS_PIE_SLICE.get());
            ItemStack remainder = food.getItem().finishUsingItem(food, level, player);
            if (!remainder.isEmpty()) give(player, remainder);
            advance(level, pos, state, bites);
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void advance(Level level, BlockPos pos, BlockState state, int bites) {
        if (bites >= 3) level.destroyBlock(pos, false);
        else level.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
    }

    private static boolean isKnife(ItemStack stack) {
        return xy177.twilightsparksdelight.common.event.TSDLootEvents.isKnife(stack);
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }
}
