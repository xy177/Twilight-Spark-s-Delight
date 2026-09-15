package xy177.twilightsparksdelight.common.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.common.item.Experiment250Item;

/** Shared eligibility for the narrow Lifedrain Scepter call-site hooks. */
public final class TSDExperiment250ScepterEvents {
    public static ItemStack findRedirectExperiment(LivingEntity user) {
        if (!(user instanceof Player player) || player.level().isClientSide
                || !player.isUsingItem() || !player.getUseItem().is(TFItems.LIFEDRAIN_SCEPTER.get())) {
            return ItemStack.EMPTY;
        }
        InteractionHand other = player.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack experiment = player.getItemInHand(other);
        return experiment.getItem() instanceof Experiment250Item
                && Experiment250Item.getActivity(experiment) < Experiment250Item.getCapacity(experiment)
                ? experiment : ItemStack.EMPTY;
    }

    private TSDExperiment250ScepterEvents() {}
}
