package xy177.twilightsparksdelight.common.event;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.tile.TSDGloryCrucibleBlockEntity;
import xy177.twilightsparksdelight.integration.CuriosCompat;

public final class TSDCapabilityEvents {
    private TSDCapabilityEvents() {}

    @SubscribeEvent
    public static void attachBlock(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!(event.getObject() instanceof TSDGloryCrucibleBlockEntity crucible)) return;
        LazyOptional<IFluidHandler> fluid = LazyOptional.of(() -> crucible);
        event.addCapability(TwilightSparksDelight.id("fluid_handler"), new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
                return capability == ForgeCapabilities.FLUID_HANDLER ? fluid.cast() : LazyOptional.empty();
            }
        });
        event.addListener(fluid::invalidate);
    }

    @SubscribeEvent
    public static void attachItem(AttachCapabilitiesEvent<ItemStack> event) {
        CuriosCompat.attachCapabilities(event);
    }
}
