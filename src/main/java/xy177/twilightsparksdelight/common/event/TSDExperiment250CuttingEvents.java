package xy177.twilightsparksdelight.common.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.item.Experiment250Item;

public final class TSDExperiment250CuttingEvents {
    private TSDExperiment250CuttingEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onUseBoard(PlayerInteractEvent.RightClickBlock event) {
        if (TSDConfig.EXPERIMENT_WORKSTATION.get() != TSDConfig.ExperimentWorkstation.CUTTING_BOARD
                || !(event.getItemStack().getItem() instanceof Experiment250Item)
                || !(event.getLevel().getBlockEntity(event.getPos()) instanceof CuttingBoardBlockEntity board)) {
            return;
        }
        ItemStack experiment = event.getItemStack();
        ItemStack source = board.getStoredItem();
        var target = Experiment250Logic.resolveTarget(experiment, source);
        var result = Experiment250Logic.calculate(experiment, target);
        if (!result.valid()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!Experiment250Logic.consume(experiment, result)) {
            return;
        }
        // A board holds a single ingredient. Keep the real board's extraction,
        // synchronization, particles and sound behavior.
        board.removeItem();
        board.spawnCuttingParticles(level, event.getPos(), source);
        board.playProcessingSound(SoundEvents.SLIME_SQUISH.getLocation().toString(), source, experiment);
        var pos = event.getPos();
        ItemEntity output = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.4D,
                pos.getZ() + 0.5D, new ItemStack(BuiltInRegistries.ITEM.get(target), result.outputCount()));
        output.setDefaultPickUpDelay();
        level.addFreshEntity(output);
    }
}
