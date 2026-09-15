package xy177.twilightsparksdelight.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record ExperimentActivationPayload(ItemStack stack) {
    public static void encode(ExperimentActivationPayload payload, FriendlyByteBuf buffer) {
        buffer.writeItemStack(payload.stack(), false);
    }

    public static ExperimentActivationPayload decode(FriendlyByteBuf buffer) {
        return new ExperimentActivationPayload(buffer.readItem());
    }

    public static void handle(ExperimentActivationPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientHandler.apply(payload.stack()));
        context.setPacketHandled(true);
    }

    private static final class ClientHandler {
        private static void apply(ItemStack stack) {
            xy177.twilightsparksdelight.client.TSDClientEvents.showExperimentActivation(stack);
        }
    }
}
