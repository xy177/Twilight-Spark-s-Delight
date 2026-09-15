package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.tile.TSDSharingFeastBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import vectorwing.farmersdelight.common.registry.ModSounds;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDTriggers;

/**
 * Salt-roasted helmet crab: the first serving consumes three bowls and gives
 * three plated servings; later servings require a Farmer's Delight knife.
 */
public final class TSDSaltHelmetCrabBlock extends TSDFeastBlock implements EntityBlock {
    public TSDSaltHelmetCrabBlock(Properties properties) {
        super(properties, TSDItems.BOWL_OF_SALTED_CRAB_MEAT, 8, true);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,
            net.minecraft.world.level.BlockGetter level, BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context) {
        return box(1, 0, 1, 15, 12, 15);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDSharingFeastBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                    BlockEntityType<T> type) {
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        int servings = state.getValue(SERVINGS);
        if (servings == 0) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, true);
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.WOOD_BREAK,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (servings == maxServings) {
            if (!held.is(Items.BOWL) || countBowls(player) < 3) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    consumeBowls(player, 3);
                }
                recordDiner(level, pos, player);
                give(player, new ItemStack(TSDItems.BOWL_OF_SALTED_CRAB_MEAT.get(), 3));
                level.setBlock(pos, state.setValue(SERVINGS, servings - 1), Block.UPDATE_ALL);
                level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!isKnife(held)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            give(player, new ItemStack(servings == 1
                    ? TSDItems.SALT_ROASTED_HELMET_CRAB_CLAW.get()
                    : TSDItems.COOKED_HERMIT_CRAB_LEG.get()));
            recordDiner(level, pos, player);
            if (!player.getAbilities().instabuild) {
                held.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(hand));
            }
            int nextServings = servings - 1;
            level.setBlock(pos, state.setValue(SERVINGS, nextServings), Block.UPDATE_ALL);
            if (nextServings == 0) {
                awardSharingAdvancements(level, pos);
            }
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void recordDiner(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof TSDSharingFeastBlockEntity feast) {
            feast.recordDiner(player);
        }
    }

    private static void awardSharingAdvancements(Level level, BlockPos pos) {
        if (level.getServer() == null
                || !(level.getBlockEntity(pos) instanceof TSDSharingFeastBlockEntity feast)
                || feast.getDiners().size() < TSDConfig.TWILIGHT_CHEESE_FONDUE_DINER_COUNT.get()) {
            return;
        }
        for (java.util.UUID diner : feast.getDiners()) {
            net.minecraft.server.level.ServerPlayer player =
                    level.getServer().getPlayerList().getPlayer(diner);
            if (player != null) {
                TSDTriggers.GATHERED_AROUND.trigger(player);
            }
        }
        java.util.UUID chef = feast.getChef();
        if (chef != null) {
            net.minecraft.server.level.ServerPlayer player =
                    level.getServer().getPlayerList().getPlayer(chef);
            if (player != null) {
                TSDTriggers.EXECUTIVE_CHEF.trigger(player);
            }
        }
    }

    private static int countBowls(Player player) {
        int count = 0;
        if (player.getMainHandItem().is(Items.BOWL)) count += player.getMainHandItem().getCount();
        if (player.getOffhandItem().is(Items.BOWL)) count += player.getOffhandItem().getCount();
        return count;
    }

    private static void consumeBowls(Player player, int amount) {
        amount = consume(player.getMainHandItem(), amount);
        if (amount > 0) consume(player.getOffhandItem(), amount);
    }

    private static int consume(ItemStack stack, int amount) {
        int count = Math.min(amount, stack.is(Items.BOWL) ? stack.getCount() : 0);
        stack.shrink(count);
        return amount - count;
    }

    private static boolean isKnife(ItemStack stack) {
        return xy177.twilightsparksdelight.common.event.TSDLootEvents.isKnife(stack);
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state,
            net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        if (state.getValue(SERVINGS) == maxServings) return java.util.List.of(new ItemStack(asItem()));
        var helmet = new ItemStack(twilightforest.init.TFItems.KNIGHTMETAL_HELMET.get());
        helmet.setDamageValue(helmet.getMaxDamage() * 2 / 3);
        helmet.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
        return java.util.List.of(helmet,
                new ItemStack(vectorwing.farmersdelight.common.registry.ModItems.CANVAS.get(), 2));
    }
}
