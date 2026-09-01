package xy177.twilightsparksdelight.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public final class TSDFluids
{
    private static final ResourceLocation FIERY_STILL = new ResourceLocation("twilightforest", "blocks/fluid_fiery_still");
    private static final ResourceLocation FIERY_FLOW = new ResourceLocation("twilightforest", "blocks/fluid_fiery_flow");

    public static Fluid FIERY_BLOOD;
    public static Fluid FIERY_TEARS;

    private TSDFluids()
    {
    }

    public static void register()
    {
        FIERY_BLOOD = register("fiery_blood", "twilight_spark_delight.fiery_blood");
        FIERY_TEARS = register("fiery_tears", "twilight_spark_delight.fiery_tears");
    }

    private static Fluid register(String name, String unlocalizedName)
    {
        Fluid fluid = FluidRegistry.getFluid(name);
        if (fluid == null) {
            fluid = new Fluid(name, FIERY_STILL, FIERY_FLOW)
                .setUnlocalizedName(unlocalizedName)
                .setTemperature(1000)
                .setDensity(1300)
                .setViscosity(1500)
                .setLuminosity(8);
            FluidRegistry.registerFluid(fluid);
        }
        FluidRegistry.addBucketForFluid(fluid);
        return fluid;
    }

    public static boolean isGloryCrucibleHeatingFluid(Fluid fluid)
    {
        return fluid != null && (fluid == FIERY_BLOOD || fluid == FIERY_TEARS);
    }
}
