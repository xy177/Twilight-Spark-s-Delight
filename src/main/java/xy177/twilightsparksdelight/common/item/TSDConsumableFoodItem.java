package xy177.twilightsparksdelight.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import vectorwing.farmersdelight.common.item.ConsumableItem;

/**
 * Preserves legacy use animation and duration while retaining Farmer's Delight
 * food-effect tooltip and consumption behavior.
 */
public class TSDConsumableFoodItem extends ConsumableItem {
    private final UseAnim useAnimation;
    private final int useDuration;
    private final List<Supplier<MobEffectInstance>> displayEffects = new ArrayList<>();

    public TSDConsumableFoodItem(Properties properties, UseAnim useAnimation, int useDuration) {
        super(properties, true);
        this.useAnimation = useAnimation;
        this.useDuration = Math.max(1, useDuration);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return useAnimation;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return useDuration;
    }

    public TSDConsumableFoodItem addDisplayEffect(Supplier<MobEffectInstance> effect) {
        displayEffects.add(effect);
        return this;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (xy177.twilightsparksdelight.integration.CopperCupCompat.isCupFood(stack)) {
            return xy177.twilightsparksdelight.integration.CopperCupCompat.container();
        }
        Item remainder = getCraftingRemainingItem();
        return remainder == null ? ItemStack.EMPTY : new ItemStack(remainder);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return xy177.twilightsparksdelight.integration.CopperCupCompat.isCupFood(stack)
                || getCraftingRemainingItem() != null;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level context,
                                List<Component> tooltip, TooltipFlag flag) {
        xy177.twilightsparksdelight.common.food.NagaRiceVariant.addLabel(stack, tooltip);
        if (stack.is(xy177.twilightsparksdelight.registry.TSDItems.MILLION_POUND_MEAL.get())) {
            tooltip.add(Component.translatable("twilight_spark_delight.tooltip.million_pound_meal.1")
                    .withStyle(net.minecraft.ChatFormatting.GOLD));
            tooltip.add(Component.translatable("twilight_spark_delight.tooltip.million_pound_meal.2")
                    .withStyle(net.minecraft.ChatFormatting.GOLD));
        }
        if (!vectorwing.farmersdelight.common.Configuration.ENABLE_FOOD_EFFECT_TOOLTIP.get()) return;
        var effects = new ArrayList<MobEffectInstance>();
        for (var supplier : displayEffects) effects.add(supplier.get());
        var food = stack.getFoodProperties(null);
        if (food != null) {
            for (var possible : food.getEffects()) effects.add(possible.getFirst());
        }
        effects.addAll(xy177.twilightsparksdelight.common.food.NagaRiceVariant.effects(stack));
        var visibleEffects = xy177.twilightsparksdelight.integration.TwilightDelightCompat.foodEffects(stack, effects);
        if (!visibleEffects.isEmpty()) {
            PotionUtils.addPotionTooltip(visibleEffects, tooltip, 1.0F);
        }
    }
}
