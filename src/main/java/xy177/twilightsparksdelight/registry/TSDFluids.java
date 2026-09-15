package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TwilightSparksDelight.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, TwilightSparksDelight.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> FIERY_BLOOD_TYPE = FLUID_TYPES.register(
            "fiery_blood", () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.twilight_spark_delight.fiery_blood")
                    .density(1300).viscosity(1500).temperature(1000).lightLevel(8)));
    public static final DeferredHolder<FluidType, FluidType> FIERY_TEARS_TYPE = FLUID_TYPES.register(
            "fiery_tears", () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.twilight_spark_delight.fiery_tears")
                    .density(1300).viscosity(1500).temperature(1000).lightLevel(8)));

    public static final DeferredHolder<Fluid, Fluid> FIERY_BLOOD = FLUIDS.register("fiery_blood",
            () -> new BaseFlowingFluid.Source(fieryBloodProperties()));
    public static final DeferredHolder<Fluid, Fluid> FLOWING_FIERY_BLOOD = FLUIDS.register("flowing_fiery_blood",
            () -> new BaseFlowingFluid.Flowing(fieryBloodProperties()));
    public static final DeferredHolder<Fluid, Fluid> FIERY_TEARS = FLUIDS.register("fiery_tears",
            () -> new BaseFlowingFluid.Source(fieryTearsProperties()));
    public static final DeferredHolder<Fluid, Fluid> FLOWING_FIERY_TEARS = FLUIDS.register("flowing_fiery_tears",
            () -> new BaseFlowingFluid.Flowing(fieryTearsProperties()));

    private TSDFluids() {
    }

    private static BaseFlowingFluid.Properties fieryBloodProperties() {
        return new BaseFlowingFluid.Properties(FIERY_BLOOD_TYPE, FIERY_BLOOD::value, FLOWING_FIERY_BLOOD::value);
    }

    private static BaseFlowingFluid.Properties fieryTearsProperties() {
        return new BaseFlowingFluid.Properties(FIERY_TEARS_TYPE, FIERY_TEARS::value, FLOWING_FIERY_TEARS::value);
    }

    public static boolean isHeatingFluid(Fluid fluid) {
        return fluid == FIERY_BLOOD.value() || fluid == FIERY_TEARS.value();
    }

    public static ResourceLocation itemId(String path) {
        return ResourceLocation.fromNamespaceAndPath("twilightforest", path);
    }
}
