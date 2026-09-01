package xy177.twilightsparksdelight.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xy177.twilightsparksdelight.common.event.Experiment250FatalProtectionEvents;

public final class MessageExperiment250TriggerCount implements IMessage
{
    private int triggerCount;

    public MessageExperiment250TriggerCount()
    {
    }

    public MessageExperiment250TriggerCount(int triggerCount)
    {
        this.triggerCount = triggerCount;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        triggerCount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(triggerCount);
    }

    public static final class Handler implements IMessageHandler<MessageExperiment250TriggerCount, IMessage>
    {
        @Override
        public IMessage onMessage(MessageExperiment250TriggerCount message, MessageContext context)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().player != null) {
                    Experiment250FatalProtectionEvents.setTriggerCount(
                        Minecraft.getMinecraft().player,
                        message.triggerCount
                    );
                }
            });
            return null;
        }
    }
}
