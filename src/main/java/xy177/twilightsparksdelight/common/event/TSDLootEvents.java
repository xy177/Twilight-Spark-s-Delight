package xy177.twilightsparksdelight.common.event;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.config.TSDConfig;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDLootEvents
{
    private static final double BURNING_QUEST_RAM_ADVANCEMENT_RANGE = 16.0D;

    private TSDLootEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        EntityPlayer player = event.getHarvester();
        if (player == null || event.isSilkTouching() || !isFiddlehead(event.getState()) || !isKnife(player.getHeldItemMainhand())) {
            return;
        }
        int looting = Math.max(0, net.minecraft.enchantment.EnchantmentHelper.getLootingModifier(player));
        int fortune = Math.max(0, event.getFortuneLevel());
        int amount = 1 + looting + fortune * 2;
        event.getDrops().add(new ItemStack(TSDItems.BRACKEN, amount));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof twilightforest.entity.EntityTFArmoredGiant
            && entity.getEntityData().getBoolean(TSDArmoredGiantEvents.TAG_SKILLET_VARIANT)
            && entity.getRNG().nextFloat() < TSDConfig.armoredGiantKitchenSetDropChance) {
            drop(event, new ItemStack(TSDBlocks.GIANTS_STOVE));
            drop(event, new ItemStack(TSDBlocks.GIANTS_COOKING_POT));
        }
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (TSDRabbitPocketWatchEvents.canDropPocketWatch(entity, player)) {
                drop(event, new ItemStack(TSDItems.RABBIT_POCKET_WATCH));
                if (player instanceof EntityPlayerMP) {
                    TSDAdvancements.LATE_FOR_DATE.trigger((EntityPlayerMP) player);
                }
            }
        }

        ResourceLocation id = EntityList.getKey(entity);
        if (id == null || !"twilightforest".equals(id.getResourceDomain())) {
            return;
        }

        if ("wild_boar".equals(id.getResourcePath())) {
            replaceDrop(event, Items.PORKCHOP, entity.isBurning() ? TSDItems.COOKED_WILD_BOAR_MEAT : TSDItems.RAW_WILD_BOAR_MEAT);
        } else if ("bighorn_sheep".equals(id.getResourcePath())) {
            replaceDrop(event, Items.MUTTON, entity.isBurning() ? TSDItems.COOKED_BIGHORN_MUTTON : TSDItems.RAW_BIGHORN_MUTTON);
            replaceDrop(event, Items.COOKED_MUTTON, TSDItems.COOKED_BIGHORN_MUTTON);
        } else if ("helmet_crab".equals(id.getResourcePath())) {
            removeDrop(event, Items.FISH);
            if (isKnifeKill(event)) {
                keepOnlyDrop(event, TSDItems.HERMIT_CRAB);
            } else {
                addDrop(event, entity.isBurning() ? TSDItems.COOKED_HERMIT_CRAB_LEG : TSDItems.HERMIT_CRAB_LEG, 2, 6);
            }
        } else if ("fire_beetle".equals(id.getResourcePath())) {
            addDrop(event, entity.isBurning() ? TSDItems.COOKED_FIRE_BEETLE_LEG : TSDItems.FIRE_BEETLE_LEG, 2, 6);
        } else if ("slime_beetle".equals(id.getResourcePath())) {
            addDrop(event, entity.isBurning() ? TSDItems.COOKED_SLIME_BEETLE_LEG : TSDItems.SLIME_BEETLE_LEG, 2, 6);
        } else if ("pinch_beetle".equals(id.getResourcePath())) {
            addDrop(event, entity.isBurning() ? TSDItems.COOKED_PINCH_BEETLE_LEG : TSDItems.PINCH_BEETLE_LEG, 2, 6);
        } else if ("quest_ram".equals(id.getResourcePath()) && entity.isBurning()) {
            drop(event, new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON, 32));
            drop(event, new ItemStack(TSDItems.QUEST_RAM_CHEESE, 10));
            triggerBurningQuestRamAdvancement(event);
        } else if ("snow_queen".equals(id.getResourcePath())) {
            addDrop(event, TSDItems.GELID_CRYSTAL, 6, 10);
        } else if ("minoshroom".equals(id.getResourcePath())) {
            addDrop(event, TSDItems.LABYRINTH_MUSHROOM, 6, 10);
        } else if ("knight_phantom".equals(id.getResourcePath())) {
            addDrop(event, TSDItems.EXPERIMENT_PROTOTYPE, 1, 2);
        }
    }

    private static void triggerBurningQuestRamAdvancement(LivingDropsEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        Set<EntityPlayerMP> players = new HashSet<>();
        AxisAlignedBB range = entity.getEntityBoundingBox().grow(BURNING_QUEST_RAM_ADVANCEMENT_RANGE);
        players.addAll(entity.world.getEntitiesWithinAABB(EntityPlayerMP.class, range));
        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            players.add((EntityPlayerMP) event.getSource().getTrueSource());
        }
        for (EntityPlayerMP player : players) {
            TSDAdvancements.BURNING_RAM_LORD.trigger(player);
        }
    }

    private static void replaceDrop(LivingDropsEvent event, Item oldItem, Item newItem)
    {
        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!stack.isEmpty() && stack.getItem() == oldItem) {
                drop.setItem(new ItemStack(newItem, stack.getCount()));
            }
        }
    }

    private static void removeDrop(LivingDropsEvent event, Item item)
    {
        event.getDrops().removeIf(drop -> {
            ItemStack stack = drop.getItem();
            return !stack.isEmpty() && stack.getItem() == item;
        });
    }

    private static void keepOnlyDrop(LivingDropsEvent event, Item item)
    {
        event.getDrops().removeIf(drop -> {
            ItemStack stack = drop.getItem();
            return stack.isEmpty() || stack.getItem() != item;
        });
    }

    private static void addDrop(LivingDropsEvent event, Item item, int min, int max)
    {
        if (item == null || max < min) {
            return;
        }
        int looting = Math.max(0, event.getLootingLevel());
        int amount = min + event.getEntityLiving().world.rand.nextInt(max - min + 1) + looting;
        if (amount > 0) {
            drop(event, new ItemStack(item, amount));
        }
    }

    private static boolean isKnifeKill(LivingDropsEvent event)
    {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        return isKnife(player.getHeldItemMainhand());
    }

    private static boolean isKnife(ItemStack stack)
    {
        return ItemKnife.isKnife(stack);
    }

    private static boolean isFiddlehead(IBlockState state)
    {
        ResourceLocation id = state.getBlock().getRegistryName();
        return id != null
            && "twilightforest:twilight_plant".equals(id.toString())
            && state.getBlock().getMetaFromState(state) == 3;
    }

    private static void drop(LivingDropsEvent event, ItemStack stack)
    {
        event.getEntityLiving().entityDropItem(stack, 0.0F);
    }
}
