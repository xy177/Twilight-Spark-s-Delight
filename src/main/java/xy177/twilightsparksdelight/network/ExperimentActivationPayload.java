package xy177.twilightsparksdelight.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public record ExperimentActivationPayload(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ExperimentActivationPayload> TYPE =
            new Type<>(TwilightSparksDelight.id("experiment_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExperimentActivationPayload> CODEC =
            ItemStack.STREAM_CODEC.map(ExperimentActivationPayload::new, ExperimentActivationPayload::stack);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExperimentActivationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> xy177.twilightsparksdelight.client.TSDClientEvents
                .showExperimentActivation(payload.stack()));
    }
}
