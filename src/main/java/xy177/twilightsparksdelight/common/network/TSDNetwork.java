package xy177.twilightsparksdelight.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import xy177.twilightsparksdelight.common.network.message.MessageExperiment250Activation;
import xy177.twilightsparksdelight.common.network.message.MessageExperiment250TriggerCount;

public final class TSDNetwork
{
    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("tsd_main");

    private TSDNetwork()
    {
    }

    public static void init()
    {
        CHANNEL.registerMessage(
            MessageExperiment250Activation.Handler.class,
            MessageExperiment250Activation.class,
            0,
            Side.CLIENT
        );
        CHANNEL.registerMessage(
            MessageExperiment250TriggerCount.Handler.class,
            MessageExperiment250TriggerCount.class,
            1,
            Side.CLIENT
        );
    }

    public static void displayExperiment250Activation(EntityPlayerMP player, ItemStack stack)
    {
        CHANNEL.sendTo(new MessageExperiment250Activation(stack), player);
    }

    public static void syncExperiment250TriggerCount(EntityPlayerMP player, int triggerCount)
    {
        CHANNEL.sendTo(new MessageExperiment250TriggerCount(triggerCount), player);
    }
}
