package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.server.level.ServerPlayer;
import vectorwing.farmersdelight.common.registry.ModSounds;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDTriggers;

/**
 * Unlit fondue is state 7, ready fondue is 6..1, and state 0 is the leftover
 * cookware. The companion is the only serving tool.
 */
public final class TSDTwilightCheeseFondueBlock extends TSDFeastBlock {
    private static final ResourceLocation FIERY_BLOOD =
            new ResourceLocation("twilightforest", "fiery_blood");
    private static final ResourceLocation FIERY_TEARS =
            new ResourceLocation("twilightforest", "fiery_tears");

    public TSDTwilightCheeseFondueBlock(Properties properties) {
        super(properties, TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD, 7, true);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,
            net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context) {
        return box(0, 0, 0, 16, 14, 16);
    }

    @Override
    public InteractionResult use(BlockState state, Level level,
                                           net.minecraft.core.BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        int servings = state.getValue(SERVINGS);
        if (servings == 7) {
            ItemStack fuel = findFuel(player);
            if (fuel.isEmpty()) return InteractionResult.PASS;
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) fuel.shrink(1);
                level.setBlock(pos, state.setValue(SERVINGS, 6), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.FIRECHARGE_USE,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (servings == 0) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, true);
                level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1, 1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!held.is(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION.get())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            TwilightCheeseFondueCompanionItem.addDiner(held, player.getUUID());
            if (TwilightCheeseFondueCompanionItem.getChef(held) == null) {
                TwilightCheeseFondueCompanionItem.setChef(held, player.getUUID());
            }
            if (!player.getInventory().add(new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD.get()))) {
                player.drop(new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD.get()), false);
            }
            int next = servings - 1;
            level.setBlock(pos, state.setValue(SERVINGS, next), Block.UPDATE_ALL);
            if (next == 0) {
                awardSharingAdvancements(level, held);
            }
            TwilightCheeseFondueCompanionItem.damageCompanion(held, player);
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void awardSharingAdvancements(Level level, ItemStack companion) {
        if (!(level.getServer() != null)
                || TwilightCheeseFondueCompanionItem.getDinerCount(companion)
                < TSDConfig.TWILIGHT_CHEESE_FONDUE_DINER_COUNT.get()) {
            return;
        }

        for (java.util.UUID diner : TwilightCheeseFondueCompanionItem.getDiners(companion)) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(diner);
            if (player != null) {
                TSDTriggers.GATHERED_AROUND.trigger(player);
            }
        }

        java.util.UUID chef = TwilightCheeseFondueCompanionItem.getChef(companion);
        if (chef != null) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(chef);
            if (player != null) {
                TSDTriggers.EXECUTIVE_CHEF.trigger(player);
            }
        }
    }

    private static ItemStack findFuel(Player player) {
        ItemStack main = player.getMainHandItem();
        if (isFuel(main)) return main;
        ItemStack off = player.getOffhandItem();
        return isFuel(off) ? off : ItemStack.EMPTY;
    }

    private static boolean isFuel(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return FIERY_BLOOD.equals(id) || FIERY_TEARS.equals(id);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state,
            net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        if (state.getValue(SERVINGS) == 7) return java.util.List.of(new ItemStack(asItem()));
        return java.util.List.of(new ItemStack(twilightforest.init.TFItems.CARMINITE.get(), 5),
                new ItemStack(TSDItems.FIERY_SLAG.get(), 3));
    }
}
