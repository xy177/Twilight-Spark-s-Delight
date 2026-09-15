package xy177.twilightsparksdelight.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import twilightforest.init.TFMobEffects;
import xy177.twilightsparksdelight.TSDConfig;

/**
 * Double-Crown Ice Cream has a normal food effect plus a configurable
 * post-eating Frosted roll.
 */
public final class DoubleCrownIceCreamItem extends TSDConsumableFoodItem {
    private static final int DEFAULT_FROSTED_TICKS = 3600;
    private static final int LONG_FROSTED_TICKS = 12000;

    public DoubleCrownIceCreamItem() {
        super(new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(7)
                .saturationMod(8.4F / (7.0F * 2.0F))
                .effect(() -> new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE, 1800, 1, false, false, true), 1.0F)
                .build()), UseAnim.EAT, 32);
        addDisplayEffect(() -> new MobEffectInstance(TFMobEffects.FROSTY.get(), DEFAULT_FROSTED_TICKS));
    }

    @Override
    public void affectConsumer(ItemStack stack, Level level, LivingEntity consumer) {
        double skipChance = clamp(TSDConfig.DOUBLE_CROWN_ICE_CREAM_NO_FROSTED_CHANCE.get());
        double longChance = Math.min(
                clamp(TSDConfig.DOUBLE_CROWN_ICE_CREAM_LONG_FROSTED_CHANCE.get()),
                1.0D - skipChance);
        double roll = level.random.nextDouble();
        if (roll < skipChance) {
            return;
        }

        int duration = roll < skipChance + longChance
                ? LONG_FROSTED_TICKS : DEFAULT_FROSTED_TICKS;
        consumer.addEffect(new MobEffectInstance(
                TFMobEffects.FROSTY.get(), duration, 0, false, false, true));
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
