package xy177.twilightsparksdelight.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class MessageExperiment250Activation implements IMessage
{
    private ItemStack stack = ItemStack.EMPTY;

    public MessageExperiment250Activation()
    {
    }

    public MessageExperiment250Activation(ItemStack stack)
    {
        this.stack = stack.copy();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static final class Handler implements IMessageHandler<MessageExperiment250Activation, IMessage>
    {
        @Override
        public IMessage onMessage(MessageExperiment250Activation message, MessageContext context)
        {
            ItemStack activationStack = message.stack.copy();
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (!activationStack.isEmpty()) {
                    Minecraft.getMinecraft().entityRenderer.displayItemActivation(activationStack);
                }
            });
            return null;
        }
    }
}
