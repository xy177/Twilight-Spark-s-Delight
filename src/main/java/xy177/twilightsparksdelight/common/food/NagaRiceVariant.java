package xy177.twilightsparksdelight.common.food;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.registry.TSDComponents;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class NagaRiceVariant {
    private NagaRiceVariant() {}

    public static void addLabel(ItemStack stack, List<Component> tooltip) {
        String variant = TSDComponents.NAGA_INGREDIENT.get(stack);
        if (!"hydra".equals(variant) && !"experiment".equals(variant)) return;
        tooltip.add(Component.literal("[").append(Component.translatable(
                "twilight_spark_delight.tooltip.naga_mixed_rice." + variant)).append("]")
                .withStyle("hydra".equals(variant) ? ChatFormatting.GOLD : ChatFormatting.AQUA));
    }

    public static List<MobEffectInstance> effects(ItemStack stack) {
        if (!stack.is(TSDItems.BOWL_OF_NAGA_MIXED_RICE.get())
                && !stack.is(TSDItems.NAGA_MIXED_RICE_CUP.get())) return List.of();
        int duration = stack.is(TSDItems.NAGA_MIXED_RICE_CUP.get()) ? 3600 : 6000;
        String variant = TSDComponents.NAGA_INGREDIENT.get(stack);
        if ("experiment".equals(variant)) {
            return List.of(new MobEffectInstance(TSDEffects.SORROW.get(), duration, 0, false, false, true));
        }
        if ("hydra".equals(variant)) {
            var effect = BuiltInRegistries.MOB_EFFECT.getOptional(
                    new ResourceLocation("twilightdelight", "fire_range"))
                    .orElse(MobEffects.FIRE_RESISTANCE);
            return List.of(new MobEffectInstance(effect, duration, 0, false, false, true));
        }
        return List.of();
    }
}
