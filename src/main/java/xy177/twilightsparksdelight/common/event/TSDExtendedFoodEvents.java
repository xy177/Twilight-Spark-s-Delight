package xy177.twilightsparksdelight.common.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.food.ExtendedFoodData;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;

public final class TSDExtendedFoodEvents {
    private static final java.util.Map<Player, xy177.twilightsparksdelight.network.FoodStatePayload> LAST_SENT =
            new java.util.WeakHashMap<>();
    private static final ResourceLocation SPRINT_ID =
            ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", "extended_food_sprint");

    private TSDExtendedFoodEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()) {
            install(player);
            updateSprint(player);
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                FoodData food = player.getFoodData();
                var state = new xy177.twilightsparksdelight.network.FoodStatePayload(food.getFoodLevel(),
                        food.getSaturationLevel(), food.getExhaustionLevel(),
                        ExtendedFoodProgression.getMaxFood(player));
                if (!state.equals(LAST_SENT.get(player)) || player.tickCount % 100 == 0) {
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, state);
                    LAST_SENT.put(player, state);
                }
            }
        } else {
            removeSprint(player);
            uninstall(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        if (!TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()) {
            return;
        }
        install(player);
        FoodData original = event.getOriginal().getFoodData();
        if (event.isWasDeath()) {
            ExtendedFoodData.resetForRespawn(player, !TSDConfig.EXTENDED_FOOD_KEEP_VANILLA_RESPAWN.get(),
                    new FoodData().getSaturationLevel());
        } else {
            ExtendedFoodData.copy(original, player.getFoodData());
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(BreakSpeed event) {
        if (!TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()) {
            return;
        }
        double benefit = ExtendedFoodProgression.getExtraBenefit(event.getEntity());
        if (benefit > 0.0D) {
            event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + benefit)));
        }
    }

    public static void install(Player player) {
        ExtendedFoodData.attach(player);
    }

    private static void uninstall(Player player) {
        ExtendedFoodData.attach(player);
    }

    private static void updateSprint(Player player) {
        var attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) {
            return;
        }
        double benefit = ExtendedFoodProgression.getExtraBenefit(player);
        var existing = attribute.getModifier(SPRINT_ID);
        if (existing != null && (!player.isSprinting() || existing.amount() != benefit || benefit <= 0)) {
            attribute.removeModifier(SPRINT_ID);
        }
        if (benefit > 0.0D && player.isSprinting()) {
            if (!attribute.hasModifier(SPRINT_ID)) {
                attribute.addTransientModifier(new AttributeModifier(SPRINT_ID,
                        benefit, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    private static void removeSprint(Player player) {
        var attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.removeModifier(SPRINT_ID);
        }
    }

}
