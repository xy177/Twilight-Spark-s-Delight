package xy177.twilightsparksdelight.common.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.TwilightSparksDelight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDMossSpreadEvents
{
    private static final String TWILIGHT_PLANT = "twilightforest:twilight_plant";
    private static final String RICH_SOIL = "farmersdelight:rich_soil";
    private static final String RICH_SOUL_SOIL = "nethers_delight_legacy:rich_soul_soil";
    private static final int MOSS_META = 0;
    private static final int RANDOM_TICK_RADIUS = 16;
    private static final int RANDOM_TICK_HEIGHT = 8;
    private static final double ADVANCEMENT_RANGE_SQ = 16.0D * 16.0D;

    private TSDMossSpreadEvents()
    {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;
        if (event.phase != TickEvent.Phase.END || world.isRemote) {
            return;
        }

        int attempts = Math.max(0, world.getGameRules().getInt("randomTickSpeed"));
        if (attempts == 0 || world.playerEntities.isEmpty()) {
            return;
        }

        for (EntityPlayer player : world.playerEntities) {
            BlockPos origin = player.getPosition();
            for (int i = 0; i < attempts; i++) {
                BlockPos picked = origin.add(
                    world.rand.nextInt(RANDOM_TICK_RADIUS * 2 + 1) - RANDOM_TICK_RADIUS,
                    world.rand.nextInt(RANDOM_TICK_HEIGHT * 2 + 1) - RANDOM_TICK_HEIGHT,
                    world.rand.nextInt(RANDOM_TICK_RADIUS * 2 + 1) - RANDOM_TICK_RADIUS
                );
                trySpreadFromRandomTick(world, picked);
            }
        }
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event)
    {
        World world = event.getWorld();
        if (world.isRemote || !isMossPatch(event.getBlock())) {
            return;
        }

        if (trySpreadFromMoss(world, event.getPos(), true)) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    private static void trySpreadFromRandomTick(World world, BlockPos picked)
    {
        IBlockState state = world.getBlockState(picked);
        if (isMossPatch(state)) {
            trySpreadFromMoss(world, picked, false);
            return;
        }

        if (isGrowableSoil(state) && isMossPatch(world.getBlockState(picked.up()))) {
            trySpreadFromMoss(world, picked.up(), false);
        }
    }

    private static boolean trySpreadFromMoss(World world, BlockPos mossPos, boolean showBonemealParticles)
    {
        if (!isMossPatch(world.getBlockState(mossPos))) {
            return false;
        }

        BlockPos sourceSoil = mossPos.down();
        if (!isGrowableSoil(world.getBlockState(sourceSoil))) {
            return false;
        }

        List<BlockPos> candidates = collectCandidates(world, sourceSoil);
        if (candidates.isEmpty()) {
            return false;
        }

        Collections.shuffle(candidates, world.rand);
        int count = Math.min(2, candidates.size());
        IBlockState moss = mossState();
        for (int i = 0; i < count; i++) {
            BlockPos target = candidates.get(i).up();
            world.setBlockState(target, moss, 3);
            triggerNearbyPlayers(world, target);
            if (showBonemealParticles) {
                world.playEvent(2005, target, 0);
            }
        }
        if (showBonemealParticles) {
            world.playEvent(2005, mossPos, 0);
        }
        return true;
    }

    private static List<BlockPos> collectCandidates(World world, BlockPos sourceSoil)
    {
        List<BlockPos> candidates = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockPos soilPos = sourceSoil.add(dx, 0, dz);
                if (isGrowableSoil(world.getBlockState(soilPos)) && canPlaceMossAt(world, soilPos.up())) {
                    candidates.add(soilPos);
                }
            }
        }
        return candidates;
    }

    private static boolean canPlaceMossAt(World world, BlockPos pos)
    {
        return world.isAirBlock(pos);
    }

    private static boolean isMossPatch(IBlockState state)
    {
        return isBlock(state, TWILIGHT_PLANT) && state.getBlock().getMetaFromState(state) == MOSS_META;
    }

    private static IBlockState mossState()
    {
        Block block = ForgeRegistries.BLOCKS.getValue(new net.minecraft.util.ResourceLocation(TWILIGHT_PLANT));
        return block == null ? net.minecraft.init.Blocks.AIR.getDefaultState() : block.getStateFromMeta(MOSS_META);
    }

    private static boolean isGrowableSoil(IBlockState state)
    {
        return isBlock(state, RICH_SOIL) || isBlock(state, RICH_SOUL_SOIL);
    }

    private static boolean isBlock(IBlockState state, String id)
    {
        return state.getBlock().getRegistryName() != null && id.equals(state.getBlock().getRegistryName().toString());
    }

    private static void triggerNearbyPlayers(World world, BlockPos pos)
    {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP && player.getDistanceSq(x, y, z) <= ADVANCEMENT_RANGE_SQ) {
                TSDAdvancements.MOSS_SPREAD.trigger((EntityPlayerMP) player);
            }
        }
    }
}
