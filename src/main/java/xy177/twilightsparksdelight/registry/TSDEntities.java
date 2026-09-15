package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.entity.ThrownPickledBracken;

public final class TSDEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TwilightSparksDelight.MOD_ID);

    public static final RegistryObject<EntityType<ThrownPickledBracken>> PICKLED_BRACKEN =
            ENTITIES.register("pickled_bracken", () -> EntityType.Builder
                    .<ThrownPickledBracken>of(ThrownPickledBracken::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                    .build(TwilightSparksDelight.MOD_ID + ":pickled_bracken"));

    private TSDEntities() {
    }
}
