package xy177.twilightsparksdelight.common.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import twilightforest.entity.monster.ArmoredGiant;
import vectorwing.farmersdelight.common.registry.ModItems;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDBlocks;

/**
 * Adds the optional Skillet equipment variant to Twilight Forest's Armored Giant.
 *
 * The variant is stored on the entity rather than inferred from its current
 * equipment, so chunk reloads keep the original roll and never reroll it.
 */
public final class TSDArmoredGiantEvents {
    public static final String SKILLET_VARIANT_TAG =
            "TwilightSparkDelightSkilletArmoredGiant";
    private static final String ROLLED_TAG =
            "TwilightSparkDelightSkilletArmoredGiantRolled";

    private TSDArmoredGiantEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ArmoredGiant giant)) {
            return;
        }

        var data = giant.getPersistentData();
        if (!data.getBoolean(ROLLED_TAG)) {
            data.putBoolean(ROLLED_TAG, true);
            data.putBoolean(SKILLET_VARIANT_TAG,
                    giant.getRandom().nextDouble()
                            < TSDConfig.ARMORED_GIANT_SKILLET_VARIANT_CHANCE.get());
        }

        if (data.getBoolean(SKILLET_VARIANT_TAG)) {
            applyVariantEquipment(giant);
        }
    }

    public static boolean isSkilletVariant(Entity entity) {
        return entity instanceof ArmoredGiant
                && entity.getPersistentData().getBoolean(SKILLET_VARIANT_TAG);
    }

    private static void applyVariantEquipment(ArmoredGiant giant) {
        giant.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.SKILLET.get()));
        giant.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_KNIFE.get()));
        giant.setDropChance(EquipmentSlot.HEAD, 0.0F);
        giant.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
}
