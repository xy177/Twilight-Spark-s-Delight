package xy177.twilightsparksdelight.common.item;

import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDComponents;

/**
 * The fondue companion has six servings. Its durability is deliberately stored
 * in a separate tag as an integrity check against external repair systems.
 */
public final class TwilightCheeseFondueCompanionItem extends Item {
    private static final int MAX_USES = 6;

    public TwilightCheeseFondueCompanionItem(Properties properties) {
        super(properties.stacksTo(1).durability(MAX_USES - 1).setNoRepair());
    }

    public static ItemStack withFullDurability(ItemStack stack) {
        setUses(stack, MAX_USES);
        return stack;
    }

    public static int getRemainingUses(ItemStack stack) {
        ensureIntegrity(stack);
        return stack.getOrDefault(TSDComponents.COMPANION_USES, 1);
    }

    public static void damageCompanion(ItemStack stack, Player player) {
        ensureIntegrity(stack);
        if (player.getAbilities().instabuild) {
            return;
        }
        int uses = getRemainingUses(stack);
        if (uses <= 1) {
            stack.shrink(1);
        } else {
            setUses(stack, uses - 1);
        }
    }

    public static void setChef(ItemStack stack, UUID chef) {
        if (chef != null) stack.set(TSDComponents.COMPANION_CHEF, chef.toString());
    }

    public static UUID getChef(ItemStack stack) {
        String value = stack.getOrDefault(TSDComponents.COMPANION_CHEF, "");
        try {
            return value.isEmpty() ? null : UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void addDiner(ItemStack stack, UUID diner) {
        if (diner == null) return;
        java.util.List<String> diners = new java.util.ArrayList<>(
                stack.getOrDefault(TSDComponents.COMPANION_DINERS, java.util.List.of()));
        String value = diner.toString();
        if (diners.contains(value)) return;
        diners.add(value);
        stack.set(TSDComponents.COMPANION_DINERS, diners);
    }

    public static int getDinerCount(ItemStack stack) {
        return stack.getOrDefault(TSDComponents.COMPANION_DINERS, java.util.List.of()).size();
    }

    public static java.util.List<UUID> getDiners(ItemStack stack) {
        java.util.ArrayList<UUID> result = new java.util.ArrayList<>();
        java.util.List<String> stored = stack.get(TSDComponents.COMPANION_DINERS);
        if (stored == null) {
            stored = java.util.List.of();
        }
        for (String value : stored) {
            try {
                result.add(UUID.fromString(value));
            } catch (IllegalArgumentException ignored) {
                // Ignore stale or malformed entries from externally edited data.
            }
        }
        return java.util.List.copyOf(result);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide) {
            ensureIntegrity(stack);
            if (entity instanceof Player player && getChef(stack) == null) {
                setChef(stack, player.getUUID());
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack,
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).is(TSDBlocks.TWILIGHT_CHEESE_FONDUE.get())) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide && context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("item.twilight_spark_delight.twilight_cheese_fondue_companion.need_fondue"),
                    true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                  InteractionHand usedHand) {
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                java.util.List<Component> tooltip, TooltipFlag flag) {
        ensureIntegrity(stack);
        tooltip.add(Component.translatable(
                "twilight_spark_delight.tooltip.twilight_cheese_fondue_companion.durability",
                getRemainingUses(stack), MAX_USES));
    }

    private static void ensureIntegrity(ItemStack stack) {
        int uses = stack.getOrDefault(TSDComponents.COMPANION_USES, 1);
        setUses(stack, uses);
        if (stack.isEnchanted()) {
            stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS,
                    net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        }
        stack.remove(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS);
        stack.remove(net.minecraft.core.component.DataComponents.UNBREAKABLE);
    }

    private static void setUses(ItemStack stack, int uses) {
        int clamped = Math.max(1, Math.min(MAX_USES, uses));
        stack.set(TSDComponents.COMPANION_USES, clamped);
        stack.setDamageValue(MAX_USES - clamped);
    }
}
