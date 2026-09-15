package xy177.twilightsparksdelight.common.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TSDCommands {
    private TSDCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tsd250")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("level", IntegerArgumentType.integer(1, Experiment250Item.MAX_LEVEL))
                        .then(Commands.argument("activity", DoubleArgumentType.doubleArg(0.0D))
                                .executes(context -> give(context.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(context, "level"),
                                        DoubleArgumentType.getDouble(context, "activity")))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> give(EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "level"),
                                                DoubleArgumentType.getDouble(context, "activity")))))));
    }

    private static int give(ServerPlayer player, int level, double activity) {
        double capacity = Experiment250Item.getCapacity(level);
        if (!Double.isFinite(activity) || activity > capacity) {
            player.sendSystemMessage(Component.translatable(
                    "commands.twilight_spark_delight.tsd250.activity_too_high", capacity, level));
            return 0;
        }
        ItemStack stack = Experiment250Item.createStack(TSDItems.EXPERIMENT_250.get(), level, activity);
        player.getInventory().placeItemBackInInventory(stack);
        player.sendSystemMessage(Component.translatable(
                "commands.twilight_spark_delight.tsd250.success",
                player.getName(), level, activity, capacity));
        return 1;
    }
}
