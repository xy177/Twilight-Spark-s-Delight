package xy177.twilightsparksdelight.common.event;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockCuttingBoard;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCuttingBoard;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityUnripePickledBrackenJar;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDInteractionEvents
{
    private TSDInteractionEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCopperCupServing(PlayerInteractEvent.RightClickBlock event)
    {
        if (!Loader.isModLoaded("miners_delight_bridge")) {
            return;
        }
        Item copperCup = ForgeRegistries.ITEMS.getValue(new ResourceLocation("miners_delight", "copper_cup"));
        ItemStack held = event.getEntityPlayer().getHeldItem(event.getHand());
        if (copperCup == null || held.isEmpty() || held.getItem() != copperCup) {
            return;
        }
        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (!(tile instanceof TileEntityCookingPot)) {
            return;
        }
        TileEntityCookingPot pot = (TileEntityCookingPot) tile;
        Item cupFood = getCupServing(pot.getStoredMealStack());
        if (cupFood == null) {
            return;
        }

        if (held.getCount() >= 2 && !event.getWorld().isRemote) {
            pot.decrStackSize(6, 1);
            pot.awardExperience(event.getEntityPlayer(), 1);
            if (!event.getEntityPlayer().capabilities.isCreativeMode) {
                held.shrink(2);
            }
            giveOrReplace(event.getEntityPlayer(), event.getHand(), new ItemStack(cupFood, 2));
            pot.markDirty();
            event.getWorld().notifyBlockUpdate(
                event.getPos(),
                event.getWorld().getBlockState(event.getPos()),
                event.getWorld().getBlockState(event.getPos()),
                3
            );
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static Item getCupServing(ItemStack meal)
    {
        if (meal.isEmpty()) {
            return null;
        }
        if (meal.getItem() == TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP) {
            return TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP;
        }
        if (meal.getItem() == TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP) {
            return TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP;
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onExperiment250CuttingBoard(PlayerInteractEvent.RightClickBlock event)
    {
        if (!"cutting_board".equals(TSDConfig.experiment250WorkstationMode)) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        ItemStack experiment250 = player.getHeldItem(event.getHand());
        if (experiment250.isEmpty() || experiment250.getItem() != TSDItems.EXPERIMENT_250) {
            return;
        }

        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (!(tile instanceof TileEntityCuttingBoard)
            || !(event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockCuttingBoard)) {
            return;
        }
        TileEntityCuttingBoard board = (TileEntityCuttingBoard) tile;
        ItemStack input = board.getStoredItem();
        if (input.isEmpty()) {
            return;
        }

        ResourceLocation outputId = Experiment250Logic.resolveReplicationTarget(experiment250, input);
        if (outputId == null) {
            return;
        }
        Item outputItem = outputId == null ? null : ForgeRegistries.ITEMS.getValue(outputId);
        Experiment250Logic.ReplicationResult result = Experiment250Logic.calculateReplication(experiment250, outputId);
        if (outputItem == null || !result.isValid()) {
            if (!event.getWorld().isRemote) {
                player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                    "twilight_spark_delight.message.experiment_250.not_enough_activity"
                ), true);
            }
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            return;
        }

        if (!event.getWorld().isRemote) {
            ItemStack removed = board.removeStoredItem();
            if (!removed.isEmpty() && Experiment250Logic.consumeReplicationActivity(experiment250, result)) {
                ItemStack output = new ItemStack(outputItem, result.getOutputCount());
                InventoryHelper.spawnItemStack(
                    event.getWorld(),
                    event.getPos().getX() + 0.5D,
                    event.getPos().getY() + 0.2D,
                    event.getPos().getZ() + 0.5D,
                    output
                );
                BlockCuttingBoard.spawnCuttingParticles(event.getWorld(), event.getPos(), removed, 5);
                event.getWorld().playSound(null, event.getPos(), ModSounds.CUTTING_BOARD_KNIFE, SoundCategory.BLOCKS, 0.9F, 1.0F);
                player.inventoryContainer.detectAndSendChanges();
            } else if (!removed.isEmpty()) {
                board.setStoredItem(removed);
            }
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBindExperiment250(PlayerInteractEvent.EntityInteract event)
    {
        if (!TSDConfig.experiment250BindingModeEnabled || !event.getEntityPlayer().isSneaking()) {
            return;
        }
        ItemStack experiment250 = event.getEntityPlayer().getHeldItem(event.getHand());
        if (experiment250.isEmpty() || experiment250.getItem() != TSDItems.EXPERIMENT_250) {
            return;
        }
        ResourceLocation entityId = EntityList.getKey(event.getTarget());
        ResourceLocation meatId = TSDConfig.getNextExperiment250MeatForEntity(
            entityId,
            Experiment250Item.getBoundMeat(experiment250)
        );
        if (meatId == null) {
            return;
        }
        if (!event.getWorld().isRemote) {
            Experiment250Item.setBoundMeat(experiment250, meatId);
            Item meatItem = ForgeRegistries.ITEMS.getValue(meatId);
            String meatName = meatItem == null ? meatId.toString() : new ItemStack(meatItem).getDisplayName();
            event.getEntityPlayer().sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                "twilight_spark_delight.message.experiment_250.bound",
                meatName
            ), true);
            event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItem(event.getHand());
        if (held.isEmpty() || held.getItem() != TSDItems.LABYRINTH_MUSHROOM || event.getFace() != EnumFacing.UP) {
            return;
        }

        BlockPos soilPos = event.getPos();
        ResourceLocation richSoilId = new ResourceLocation("farmersdelight", "rich_soil");
        if (event.getWorld().getBlockState(soilPos).getBlock() != ForgeRegistries.BLOCKS.getValue(richSoilId)) {
            return;
        }

        BlockPos colonyPos = soilPos.up();
        if (!event.getWorld().mayPlace(TSDBlocks.LABYRINTH_MUSHROOM_COLONY, colonyPos, false, EnumFacing.UP, player)) {
            return;
        }

        if (!event.getWorld().isRemote) {
            event.getWorld().setBlockState(colonyPos, TSDBlocks.LABYRINTH_MUSHROOM_COLONY.getDefaultState(), 3);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            event.getWorld().playSound(null, colonyPos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onUsePeacockFan(PlayerInteractEvent.RightClickItem event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItem(event.getHand());
        if (held.isEmpty() || held.getItem().getRegistryName() == null
            || !"twilightforest:peacock_fan".equals(held.getItem().getRegistryName().toString())) {
            return;
        }
        if (event.getWorld().isRemote) {
            return;
        }
        AxisAlignedBB range = player.getEntityBoundingBox().grow(4.0D);
        BlockPos min = new BlockPos(range.minX, range.minY, range.minZ);
        BlockPos max = new BlockPos(range.maxX, range.maxY, range.maxZ);
        for (BlockPos pos : BlockPos.getAllInBoxMutable(min, max)) {
            TileEntity tile = event.getWorld().getTileEntity(pos);
            if (tile instanceof TileEntityUnripePickledBrackenJar) {
                ((TileEntityUnripePickledBrackenJar) tile).markPeacockFanUsed();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (!(event.getTarget() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase target = (EntityLivingBase) event.getTarget();
        ResourceLocation entityId = EntityList.getKey(target);
        if (entityId == null || !"twilightforest".equals(entityId.getResourceDomain()) || !"quest_ram".equals(entityId.getResourcePath())) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItem(event.getHand());
        if (held.isEmpty() || held.getItem() != Items.GLASS_BOTTLE) {
            return;
        }

        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }
        give(player, new ItemStack(TSDItems.QUEST_RAM_MILK));
        player.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_COW_MILK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (player instanceof EntityPlayerMP) {
            TSDAdvancements.MILK_QUEST_RAM.trigger((EntityPlayerMP) player);
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    private static void giveOrReplace(EntityPlayer player, EnumHand hand, ItemStack stack)
    {
        if (player.getHeldItem(hand).isEmpty()) {
            player.setHeldItem(hand, stack);
        } else {
            give(player, stack);
        }
    }

}
