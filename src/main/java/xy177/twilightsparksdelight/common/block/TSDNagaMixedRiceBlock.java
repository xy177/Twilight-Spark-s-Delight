package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.registry.ModSounds;
import xy177.twilightsparksdelight.registry.TSDItems;

/**
 * The naga rice follows the legacy 28-stage serving pattern:
 * Every fourth serving is a scale (or four optional naga chips), other servings are bowls,
 * and the completed platter yields the shield and naga trophy.
 */
public final class TSDNagaMixedRiceBlock extends TSDLargeStageFeastBlock {
    private static final ResourceLocation NAGA_SCALE =
            ResourceLocation.fromNamespaceAndPath("twilightforest", "naga_scale");
    private static final ResourceLocation NAGA_TROPHY =
            ResourceLocation.fromNamespaceAndPath("twilightforest", "naga_trophy");

    public TSDNagaMixedRiceBlock(Properties properties, SupplierArgs args) {
        super(properties, "stage", 28, args.servingItem(), true, 3, 3, args.part());
    }

    public record SupplierArgs(java.util.function.Supplier<Item> servingItem,
                               java.util.function.Supplier<TSDStructurePartBlock> part) {
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            java.util.List<net.minecraft.network.chat.Component> tooltip,
            net.minecraft.world.item.TooltipFlag flag) {
        xy177.twilightsparksdelight.common.food.NagaRiceVariant.addLabel(stack, tooltip);
    }

    @Override
    protected java.util.List<ItemStack> finalDrops() {
        return java.util.List.of(new ItemStack(Items.SHIELD),
                new ItemStack(twilightforest.init.TFItems.NAGA_TROPHY.get()));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           net.minecraft.core.BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        int stage = currentStage(state);
        if (stage >= maxStage) {
            if (!level.isClientSide) {
                for (ItemStack drop : finalDrops()) popResource(level, pos, drop);
                removeStructure(level, pos, false);
                level.removeBlock(pos, false);
                level.playSound(null, pos, twilightforest.init.TFSounds.NAGA_HURT.get(),
                        SoundSource.BLOCKS, 1.0F, 0.8F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        ItemStack result;
        boolean cupServing = xy177.twilightsparksdelight.integration.CopperCupCompat.isCup(held);
        if (stage % 4 == 3) {
            Item chips = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath("twilightdelight", "naga_chip"));
            result = chips == Items.AIR ? new ItemStack(twilightforest.init.TFItems.NAGA_SCALE.get())
                    : new ItemStack(chips, 4);
        } else {
            if (!held.is(Items.BOWL) && !cupServing) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (cupServing && held.getCount() < 2) return ItemInteractionResult.SUCCESS;
            result = new ItemStack(cupServing ? TSDItems.NAGA_MIXED_RICE_CUP.get()
                    : TSDItems.BOWL_OF_NAGA_MIXED_RICE.get(), cupServing ? 2 : 1);
            if (level.getBlockEntity(pos) instanceof xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity feast) {
                feast.writeItem(result, false);
            }
        }

        if (!level.isClientSide) {
            if (stage % 4 != 3
                    && !player.getAbilities().instabuild) {
                held.shrink(cupServing ? 2 : 1);
            }
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            if (stage + 1 >= maxStage) {
                level.setBlock(pos, state.setValue(stageProperty(), maxStage), Block.UPDATE_ALL);
            } else {
                level.setBlock(pos, state.setValue(stageProperty(), stage + 1), Block.UPDATE_ALL);
            }
            level.playSound(null, pos, stage % 4 == 3 ? twilightforest.init.TFSounds.NAGA_HURT.get()
                            : ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
