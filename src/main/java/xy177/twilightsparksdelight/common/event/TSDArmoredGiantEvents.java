package xy177.twilightsparksdelight.common.event;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import twilightforest.entity.EntityTFArmoredGiant;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDArmoredGiantEvents
{
    private static final String TAG_VARIANT_ROLLED = "TsdSkilletVariantRolled";
    public static final String TAG_SKILLET_VARIANT = "TsdSkilletVariant";
    private static final ResourceLocation SKILLET = new ResourceLocation("farmersdelight", "skillet");
    private static final ResourceLocation IRON_KNIFE = new ResourceLocation("farmersdelight", "iron_knife");

    private TSDArmoredGiantEvents()
    {
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityTFArmoredGiant)) {
            return;
        }

        EntityTFArmoredGiant giant = (EntityTFArmoredGiant) event.getEntity();
        if (!giant.getEntityData().getBoolean(TAG_VARIANT_ROLLED)) {
            giant.getEntityData().setBoolean(TAG_VARIANT_ROLLED, true);
            giant.getEntityData().setBoolean(
                TAG_SKILLET_VARIANT,
                giant.getRNG().nextFloat() < TSDConfig.armoredGiantSkilletVariantChance
            );
        }

        if (giant.getEntityData().getBoolean(TAG_SKILLET_VARIANT)) {
            equipVariant(giant);
        }
    }

    private static void equipVariant(EntityTFArmoredGiant giant)
    {
        Item skillet = ForgeRegistries.ITEMS.getValue(SKILLET);
        Item ironKnife = ForgeRegistries.ITEMS.getValue(IRON_KNIFE);
        if (skillet == null || ironKnife == null) {
            return;
        }

        giant.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(skillet));
        giant.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ironKnife));
        giant.setDropChance(EntityEquipmentSlot.HEAD, 0.0F);
        giant.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0F);
    }
}
