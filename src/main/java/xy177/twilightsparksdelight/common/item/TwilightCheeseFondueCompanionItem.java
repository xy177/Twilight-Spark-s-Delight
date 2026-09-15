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
        return TSDComponents.COMPANION_USES.getOrDefault(stack, 1);
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
        if (chef != null) TSDComponents.COMPANION_CHEF.set(stack, chef.toString());
    }

    public static UUID getChef(ItemStack stack) {
        String value = TSDComponents.COMPANION_CHEF.getOrDefault(stack, "");
        try {
            return value.isEmpty() ? null : UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void addDiner(ItemStack stack, UUID diner) {
        if (diner == null) return;
        java.util.List<String> diners = new java.util.ArrayList<>(
                TSDComponents.COMPANION_DINERS.getOrDefault(stack, java.util.List.of()));
        String value = diner.toString();
        if (diners.contains(value)) return;
        diners.add(value);
        TSDComponents.COMPANION_DINERS.set(stack, diners);
    }

    public static int getDinerCount(ItemStack stack) {
        return TSDComponents.COMPANION_DINERS.getOrDefault(stack, java.util.List.of()).size();
    }

    public static java.util.List<UUID> getDiners(ItemStack stack) {
        java.util.ArrayList<UUID> result = new java.util.ArrayList<>();
        java.util.List<String> stored = TSDComponents.COMPANION_DINERS.get(stack);
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
    public boolean canApplyAtEnchantingTable(ItemStack stack,
            net.minecraft.world.item.enchantment.Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
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
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level context,
                                java.util.List<Component> tooltip, TooltipFlag flag) {
        ensureIntegrity(stack);
        tooltip.add(Component.translatable(
                "twilight_spark_delight.tooltip.twilight_cheese_fondue_companion.durability",
                getRemainingUses(stack), MAX_USES));
    }

    private static void ensureIntegrity(ItemStack stack) {
        int uses = TSDComponents.COMPANION_USES.getOrDefault(stack, 1);
        setUses(stack, uses);
        stack.removeTagKey("Enchantments");
        stack.removeTagKey("StoredEnchantments");
        stack.removeTagKey("Unbreakable");
    }

    private static void setUses(ItemStack stack, int uses) {
        int clamped = Math.max(1, Math.min(MAX_USES, uses));
        TSDComponents.COMPANION_USES.set(stack, clamped);
        stack.setDamageValue(MAX_USES - clamped);
    }
}
