package xy177.twilightsparksdelight.common.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import twilightforest.entity.passive.QuestRam;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDTriggers;

public final class TSDInteractionEvents {
    private TSDInteractionEvents() {
    }

    @SubscribeEvent
    public static void onPlantColony(PlayerInteractEvent.RightClickBlock event) {
        if (event.getFace() != net.minecraft.core.Direction.UP
                || !event.getLevel().getBlockState(event.getPos()).is(
                        vectorwing.farmersdelight.common.registry.ModBlocks.RICH_SOIL.get())) return;
        var held = event.getItemStack();
        net.minecraft.world.level.block.Block colony;
        if (held.is(TSDItems.LABYRINTH_MUSHROOM.get())) {
            colony = xy177.twilightsparksdelight.registry.TSDBlocks.LABYRINTH_MUSHROOM_COLONY.get();
        } else if (held.is(TSDItems.BRACKEN.get())
                || held.is(twilightforest.init.TFBlocks.FIDDLEHEAD.get().asItem())) {
            colony = xy177.twilightsparksdelight.registry.TSDBlocks.TWILIGHT_BRACKEN_COLONY.get();
        } else {
            return;
        }
        var level = event.getLevel();
        var pos = event.getPos().above();
        var player = event.getEntity();
        if (!level.isEmptyBlock(pos) || !player.mayUseItemAt(pos, event.getFace(), held)
                || !level.mayInteract(player, pos)) return;
        if (!level.isClientSide) {
            level.setBlock(pos, colony.defaultBlockState(), 3);
            if (!player.getAbilities().instabuild) held.shrink(1);
            level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onQuestRamInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (!(target instanceof QuestRam)
                || !held.is(Items.GLASS_BOTTLE)) {
            return;
        }

        if (!event.getLevel().isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            ItemStack milk = new ItemStack(TSDItems.QUEST_RAM_MILK.get());
            if (!player.getInventory().add(milk)) {
                player.drop(milk, false);
            }
            event.getLevel().playSound(null, target.blockPosition(),
                    SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                TSDTriggers.MILK_QUEST_RAM.get().trigger(serverPlayer);
            }
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }
}
