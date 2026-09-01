package xy177.twilightsparksdelight.common.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.block.BlockTwilightBrackenColony;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDBrackenColonyEvents
{
    private TSDBrackenColonyEvents()
    {
    }

    @SubscribeEvent
    public static void onRightClickRichSoil(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getFace() != EnumFacing.UP || !isRichSoil(event.getWorld().getBlockState(event.getPos()))) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (!isBracken(held)) {
            return;
        }
        World world = event.getWorld();
        BlockPos plantPos = event.getPos().up();
        if (!world.isAirBlock(plantPos)) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        if (!world.isRemote) {
            world.setBlockState(plantPos, TSDBlocks.TWILIGHT_BRACKEN_COLONY.getDefaultState()
                .withProperty(BlockTwilightBrackenColony.AGE, 0), 3);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            world.playSound(null, plantPos, net.minecraft.init.SoundEvents.BLOCK_GRASS_PLACE,
                SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static boolean isRichSoil(IBlockState state)
    {
        ResourceLocation id = state.getBlock().getRegistryName();
        return id != null && "farmersdelight:rich_soil".equals(id.toString());
    }

    private static boolean isBracken(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == TSDItems.BRACKEN) {
            return true;
        }
        ResourceLocation id = stack.getItem().getRegistryName();
        return id != null && "twilightforest:twilight_plant".equals(id.toString()) && stack.getMetadata() == 3;
    }
}
