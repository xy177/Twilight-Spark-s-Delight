package xy177.twilightsparksdelight.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.registries.BuiltInRegistries;
import xy177.twilightsparksdelight.TSDConfig;

/**
 * Removes a limited number of random harmful effects after the food is eaten.
 */
public class MultiRandomCureFoodItem extends TSDConsumableFoodItem {
    private final int maximumCures;

    public MultiRandomCureFoodItem(Properties properties, int maximumCures) {
        this(properties, maximumCures, UseAnim.EAT, 32);
    }

    public MultiRandomCureFoodItem(Properties properties, int maximumCures,
                                   UseAnim useAnimation, int useDuration) {
        super(properties, useAnimation, useDuration);
        this.maximumCures = maximumCures;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack result = super.finishUsingItem(stack, level, consumer);
        if (!level.isClientSide) {
            List<MobEffectInstance> harmful = new ArrayList<>();
            for (MobEffectInstance effect : consumer.getActiveEffects()) {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL
                        && !TSDConfig.RANDOM_CURE_EFFECT_BLACKLIST.get().contains(
                                BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value()).toString())) {
                    harmful.add(effect);
                }
            }
            int cures = Math.min(maximumCures, harmful.size());
            for (int i = 0; i < cures; i++) {
                int index = consumer.getRandom().nextInt(harmful.size());
                MobEffectInstance effect = harmful.remove(index);
                consumer.removeEffect(effect.getEffect());
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable(maximumCures == 1
                ? "twilight_spark_delight.tooltip.random_cure"
                : "twilight_spark_delight.tooltip.random_cure_three").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
