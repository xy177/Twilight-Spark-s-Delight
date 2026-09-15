package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, TwilightSparksDelight.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, TwilightSparksDelight.MOD_ID);

    public static final RegistryObject<FluidType> FIERY_BLOOD_TYPE = FLUID_TYPES.register(
            "fiery_blood", () -> fieryType("fiery_blood"));
    public static final RegistryObject<FluidType> FIERY_TEARS_TYPE = FLUID_TYPES.register(
            "fiery_tears", () -> fieryType("fiery_tears"));

    public static final RegistryObject<Fluid> FIERY_BLOOD = FLUIDS.register("fiery_blood",
            () -> new ForgeFlowingFluid.Source(fieryBloodProperties()));
    public static final RegistryObject<Fluid> FLOWING_FIERY_BLOOD = FLUIDS.register("flowing_fiery_blood",
            () -> new ForgeFlowingFluid.Flowing(fieryBloodProperties()));
    public static final RegistryObject<Fluid> FIERY_TEARS = FLUIDS.register("fiery_tears",
            () -> new ForgeFlowingFluid.Source(fieryTearsProperties()));
    public static final RegistryObject<Fluid> FLOWING_FIERY_TEARS = FLUIDS.register("flowing_fiery_tears",
            () -> new ForgeFlowingFluid.Flowing(fieryTearsProperties()));

    private TSDFluids() {
    }

    private static FluidType fieryType(String name) {
        return new FluidType(FluidType.Properties.create()
                .descriptionId("fluid.twilight_spark_delight." + name)
                .density(1300).viscosity(1500).temperature(1000).lightLevel(8)) {
            @Override
            public void initializeClient(java.util.function.Consumer<
                    net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
                consumer.accept(new net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return TwilightSparksDelight.id("block/fiery_essence_still");
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return TwilightSparksDelight.id("block/fiery_essence_flow");
                    }
                });
            }
        };
    }

    private static ForgeFlowingFluid.Properties fieryBloodProperties() {
        return new ForgeFlowingFluid.Properties(FIERY_BLOOD_TYPE, FIERY_BLOOD::get, FLOWING_FIERY_BLOOD::get);
    }

    private static ForgeFlowingFluid.Properties fieryTearsProperties() {
        return new ForgeFlowingFluid.Properties(FIERY_TEARS_TYPE, FIERY_TEARS::get, FLOWING_FIERY_TEARS::get);
    }

    public static boolean isHeatingFluid(Fluid fluid) {
        return fluid == FIERY_BLOOD.get() || fluid == FIERY_TEARS.get();
    }

    public static ResourceLocation itemId(String path) {
        return new ResourceLocation("twilightforest", path);
    }
}
