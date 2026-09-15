package xy177.twilightsparksdelight.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.event.TSDExtendedFoodEvents;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;

public record FoodStatePayload(int food, float saturation, float exhaustion, int cap)
        implements CustomPacketPayload {
    public static final Type<FoodStatePayload> TYPE = new Type<>(TwilightSparksDelight.id("food_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodStatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FoodStatePayload::food,
            ByteBufCodecs.FLOAT, FoodStatePayload::saturation,
            ByteBufCodecs.FLOAT, FoodStatePayload::exhaustion,
            ByteBufCodecs.VAR_INT, FoodStatePayload::cap, FoodStatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FoodStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            player.getPersistentData().putInt(ExtendedFoodProgression.CLIENT_CAP, payload.cap());
            TSDExtendedFoodEvents.install(player);
            var data = player.getFoodData();
            data.setFoodLevel(payload.food());
            data.setSaturation(payload.saturation());
            data.setExhaustion(payload.exhaustion());
        });
    }
}
