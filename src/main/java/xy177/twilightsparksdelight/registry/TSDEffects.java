package xy177.twilightsparksdelight.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.effect.TSDMobEffect;

public final class TSDEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MOB_EFFECT, TwilightSparksDelight.MOD_ID);

    public static final RegistryObject<MobEffect> CHARGE =
            register("charge", MobEffectCategory.BENEFICIAL, 0xC88A2A);
    public static final RegistryObject<MobEffect> SYMBIOSIS =
            register("symbiosis", MobEffectCategory.BENEFICIAL, 0x945D76, TSDMobEffect.Behavior.SYMBIOSIS);
    public static final RegistryObject<MobEffect> SHRINK =
            register("shrink", MobEffectCategory.BENEFICIAL, 0x7E5FB7);
    public static final RegistryObject<MobEffect> ENLARGE =
            register("enlarge", MobEffectCategory.BENEFICIAL, 0xB78D3A);
    public static final RegistryObject<MobEffect> ABYSS_CALL =
            register("abyss_call", MobEffectCategory.BENEFICIAL, 0x6D2B83);
    public static final RegistryObject<MobEffect> SORROW =
            register("sorrow", MobEffectCategory.BENEFICIAL, 0x4D7FB8);
    public static final RegistryObject<MobEffect> GRIEF =
            register("grief", MobEffectCategory.BENEFICIAL, 0x8B3A5A);

    private TSDEffects() {
    }

    private static RegistryObject<MobEffect> register(String id, MobEffectCategory category, int color) {
        return register(id, category, color, TSDMobEffect.Behavior.NONE);
    }

    private static RegistryObject<MobEffect> register(String id, MobEffectCategory category, int color,
                                                                  TSDMobEffect.Behavior behavior) {
        return EFFECTS.register(id, () -> new TSDMobEffect(category, color, behavior));
    }
}
