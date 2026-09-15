package xy177.twilightsparksdelight.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.registry.TSDComponents;
import xy177.twilightsparksdelight.registry.TSDDamageTypes;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDTriggers;

public final class HelmetCrabItem extends Item {
    private static final int MAX_BITES = 8;
    private static final int COOLDOWN_TICKS = 300;

    public HelmetCrabItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            player.displayClientMessage(
                    Component.translatable("twilight_spark_delight.item.hermit_crab.hint"), true);
            player.hurt(new net.minecraft.world.damagesource.DamageSource(
                    level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(TSDDamageTypes.CRAB_BITE), player), 2.0F);

            if (player.isDeadOrDying() && player instanceof ServerPlayer serverPlayer
                    && !serverPlayer.getPersistentData()
                    .getBoolean("twilight_spark_delight.false_teeth_unlocked")) {
                serverPlayer.getPersistentData().putBoolean(
                        "twilight_spark_delight.false_teeth_unlocked", true);
                TSDTriggers.MY_FALSE_TEETH.trigger(serverPlayer);
            }

            int bites = Math.max(0, TSDComponents.HERMIT_CRAB_BITES.getOrDefault(stack, 0)) + 1;
            if (bites >= MAX_BITES) {
                stack.shrink(1);
                if (!stack.isEmpty()) {
                    TSDComponents.HERMIT_CRAB_BITES.set(stack, 0);
                }
                give(player, new ItemStack(TSDItems.HERMIT_CRAB_LEG.get(), 6));
                give(player, new ItemStack(TFItems.ARMOR_SHARD.get(), 45));
                if (player instanceof ServerPlayer serverPlayer) {
                    TSDTriggers.RUTHLESS_IRON_MOUTH.trigger(serverPlayer);
                }
            } else {
                TSDComponents.HERMIT_CRAB_BITES.set(stack, bites);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
