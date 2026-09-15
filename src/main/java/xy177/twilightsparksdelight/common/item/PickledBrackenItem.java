package xy177.twilightsparksdelight.common.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import xy177.twilightsparksdelight.common.entity.ThrownPickledBracken;

public final class PickledBrackenItem extends ConsumableItem {
    public PickledBrackenItem(Properties properties) {
        super(properties, true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return super.use(level, player, hand);
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            ThrownPickledBracken projectile = new ThrownPickledBracken(level, player);
            projectile.setItem(stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 1.0F);
            if (!level.addFreshEntity(projectile)) {
                return InteractionResultHolder.fail(stack);
            }
            stack.consume(1, player);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("twilight_spark_delight.tooltip.pickled_bracken_throw")
                .withStyle(ChatFormatting.GOLD));
    }
}
