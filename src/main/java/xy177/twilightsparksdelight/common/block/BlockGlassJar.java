package xy177.twilightsparksdelight.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.event.ForgeEventFactory;
import twilightforest.TFSounds;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.client.particle.TFParticleType;
import xy177.twilightsparksdelight.common.item.GlassJarItemBlock;
import xy177.twilightsparksdelight.common.tile.TileEntityGlassJar;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class BlockGlassJar extends Block implements ITileEntityProvider
{
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D,
        0.8125D, 1.0D, 0.8125D);

    public BlockGlassJar()
    {
        super(Material.GLASS);
        setHardness(0.3F);
        setResistance(0.3F);
        setSoundType(SoundType.GLASS);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityGlassJar();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
        EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityGlassJar)) {
            return false;
        }
        TileEntityGlassJar jar = (TileEntityGlassJar) tile;
        ItemStack held = player.getHeldItem(hand);

        if (!held.isEmpty() && hitY >= 0.875F && isValidLid(held) && !sameLid(held, jar.getLid())) {
            if (!world.isRemote) {
                jar.setLid(held);
                jar.wobble(TileEntityGlassJar.WobbleStyle.POSITIVE);
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        if (held.isEmpty()) {
            if (!world.isRemote) {
                ItemStack stored = jar.getStoredItem();
                if (player.isSneaking() && isCritter(stored)) {
                    releaseCritter(world, pos, jar, stored);
                } else if (player.isSneaking() && !stored.isEmpty()) {
                    ITextComponent name = stored.hasDisplayName()
                        ? new TextComponentString(stored.getDisplayName())
                        : new TextComponentTranslation(stored.getUnlocalizedName() + ".name");
                    name.appendText(" x" + stored.getCount());
                    player.sendStatusMessage(name, true);
                    wiggle(world, pos, jar);
                } else if (stored.isEmpty()) {
                    wiggle(world, pos, jar);
                } else {
                    ItemStack extracted = jar.extractAll();
                    player.setHeldItem(hand, extracted);
                    jar.wobble(TileEntityGlassJar.WobbleStyle.NEGATIVE);
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
            return true;
        }

        if (!world.isRemote) {
            int oldRotation = jar.getItemRotation();
            jar.setItemRotation(rotationFor(player));
            ItemStack offered = held.copy();
            ItemStack remainder = jar.insert(offered);
            int inserted = offered.getCount() - remainder.getCount();
            if (inserted <= 0) {
                jar.setItemRotation(oldRotation);
                wiggle(world, pos, jar);
            } else {
                if (!player.capabilities.isCreativeMode) {
                    player.setHeldItem(hand, remainder);
                }
                jar.wobble(TileEntityGlassJar.WobbleStyle.POSITIVE);
                float ratio = (float) inserted / (float) Math.max(1, offered.getMaxStackSize());
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS,
                    1.0F, 0.7F + 0.5F * ratio);
            }
        }
        return true;
    }

    private static void releaseCritter(World world, BlockPos pos, TileEntityGlassJar jar, ItemStack stored)
    {
        ItemStack emptyJar = TileEntityGlassJar.createJarStack(ItemStack.EMPTY, jar.getLid(), jar.getItemRotation());
        if (jar.hasCustomName()) {
            emptyJar.setStackDisplayName(jar.getCustomName());
        }
        world.setBlockToAir(pos);
        spawnAsEntity(world, pos, stored);
        spawnAsEntity(world, pos, emptyJar);
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private static void wiggle(World world, BlockPos pos, TileEntityGlassJar jar)
    {
        jar.wobble(TileEntityGlassJar.WobbleStyle.NEGATIVE);
        world.playSound(null, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private static boolean sameLid(ItemStack first, ItemStack second)
    {
        return !first.isEmpty() && !second.isEmpty()
            && first.getItem() == second.getItem()
            && first.getMetadata() == second.getMetadata();
    }

    private static boolean isValidLid(ItemStack stack)
    {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block instanceof BlockLog) {
            return true;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            if ("logWood".equals(OreDictionary.getOreName(id))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCritter(ItemStack stack)
    {
        return GlassJarItemBlock.isCritter(stack, TFBlocks.firefly)
            || GlassJarItemBlock.isCritter(stack, TFBlocks.cicada);
    }

    private static int rotationFor(EntityPlayer player)
    {
        return net.minecraft.util.math.MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5F) & 15;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityGlassJar) {
            drops.add(((TileEntityGlassJar) tile).createJarStack());
        } else {
            drops.add(GlassJarItemBlock.createEmptyJar());
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state,
        @Nullable TileEntity tile, ItemStack tool)
    {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        ItemStack drop = tile instanceof TileEntityGlassJar
            ? ((TileEntityGlassJar) tile).createJarStack()
            : GlassJarItemBlock.createEmptyJar();
        List<ItemStack> drops = new ArrayList<>();
        drops.add(drop);
        float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, 0, 1.0F, false, player);
        for (ItemStack stack : drops) {
            if (world.rand.nextFloat() <= chance) {
                spawnAsEntity(world, pos, stack);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityGlassJar
            ? ((TileEntityGlassJar) tile).createJarStack()
            : GlassJarItemBlock.createEmptyJar();
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityGlassJar ? ((TileEntityGlassJar) tile).getComparatorLevel() : 0;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityGlassJar ? ((TileEntityGlassJar) tile).getContentLight() : 0;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, param);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityGlassJar)) {
            return;
        }
        ItemStack stored = ((TileEntityGlassJar) tile).getStoredItem();
        if (GlassJarItemBlock.isCritter(stored, TFBlocks.firefly)) {
            for (int i = 0; i < 2; i++) {
                double x = pos.getX() + (random.nextFloat() - random.nextFloat()) * 0.2F + 0.5F;
                double y = pos.getY() + 0.4F + (random.nextFloat() - random.nextFloat()) * 0.3F;
                double z = pos.getZ() + (random.nextFloat() - random.nextFloat()) * 0.2F + 0.5F;
                TwilightForestMod.proxy.spawnParticle(TFParticleType.FIREFLY, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        } else if (GlassJarItemBlock.isCritter(stored, TFBlocks.cicada)) {
            double x = pos.getX() + (random.nextFloat() - random.nextFloat()) * 0.2F + 0.5F;
            double y = pos.getY() + 0.4F + (random.nextFloat() - random.nextFloat()) * 0.2F;
            double z = pos.getZ() + (random.nextFloat() - random.nextFloat()) * 0.2F + 0.5F;
            world.spawnParticle(EnumParticleTypes.NOTE, x, y, z, 0.0D, 0.0D, 0.0D);
            if (random.nextInt(75) == 0) {
                world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    TFSounds.CICADA, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
