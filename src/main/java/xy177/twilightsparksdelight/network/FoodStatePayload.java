package xy177.twilightsparksdelight.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xy177.twilightsparksdelight.common.event.TSDExtendedFoodEvents;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;

public record FoodStatePayload(int food, float saturation, float exhaustion, int cap) {
    public static void encode(FoodStatePayload payload, FriendlyByteBuf buffer) {
        buffer.writeVarInt(payload.food());
        buffer.writeFloat(payload.saturation());
        buffer.writeFloat(payload.exhaustion());
        buffer.writeVarInt(payload.cap());
    }

    public static FoodStatePayload decode(FriendlyByteBuf buffer) {
        return new FoodStatePayload(buffer.readVarInt(), buffer.readFloat(), buffer.readFloat(), buffer.readVarInt());
    }

    public static void handle(FoodStatePayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientHandler.apply(payload));
        context.setPacketHandled(true);
    }

    private static final class ClientHandler {
        private static void apply(FoodStatePayload payload) {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player == null) return;
            player.getPersistentData().putInt(ExtendedFoodProgression.CLIENT_CAP, payload.cap());
            TSDExtendedFoodEvents.install(player);
            var data = player.getFoodData();
            data.setFoodLevel(payload.food());
            data.setSaturation(payload.saturation());
            data.setExhaustion(payload.exhaustion());
        }
    }
}
