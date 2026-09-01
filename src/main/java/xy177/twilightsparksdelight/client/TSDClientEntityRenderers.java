package xy177.twilightsparksdelight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.entity.EntityThrownPickledBracken;
import xy177.twilightsparksdelight.common.registry.TSDItems;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID, value = Side.CLIENT)
public final class TSDClientEntityRenderers
{
    private TSDClientEntityRenderers()
    {
    }

    @SubscribeEvent
    public static void register(ModelRegistryEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(
            EntityThrownPickledBracken.class,
            manager -> new RenderSnowball<>(manager, TSDItems.PICKLED_BRACKEN, Minecraft.getMinecraft().getRenderItem())
        );
    }
}
