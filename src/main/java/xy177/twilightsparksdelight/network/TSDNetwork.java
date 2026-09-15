package xy177.twilightsparksdelight.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDNetwork {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            TwilightSparksDelight.id("main"), () -> VERSION, VERSION::equals, VERSION::equals);

    private TSDNetwork() {}

    public static void register() {
        CHANNEL.messageBuilder(FoodStatePayload.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(FoodStatePayload::encode).decoder(FoodStatePayload::decode)
                .consumerNetworkThread(FoodStatePayload::handle).add();
        CHANNEL.messageBuilder(ExperimentActivationPayload.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ExperimentActivationPayload::encode).decoder(ExperimentActivationPayload::decode)
                .consumerNetworkThread(ExperimentActivationPayload::handle).add();
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
