package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDDamageTypes {
    public static final ResourceKey<DamageType> CRAB_BITE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            TwilightSparksDelight.id("crab_bite"));
    public static final ResourceKey<DamageType> LIFEDRAIN_PEDESTAL = ResourceKey.create(
            Registries.DAMAGE_TYPE, TwilightSparksDelight.id("lifedrain_pedestal"));

    private TSDDamageTypes() {
    }
}
