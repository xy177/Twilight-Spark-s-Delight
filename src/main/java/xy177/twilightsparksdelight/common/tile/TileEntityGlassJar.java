package xy177.twilightsparksdelight.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import twilightforest.block.TFBlocks;
import xy177.twilightsparksdelight.common.item.GlassJarItemBlock;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

import javax.annotation.Nullable;

public class TileEntityGlassJar extends TileEntity
{
    public static final String JAR_DATA_TAG = "TsdGlassJar";
    private static final String CONTENT_TAG = "Content";
    private static final String LID_TAG = "Lid";
    private static final String ROTATION_TAG = "Rotation";
    private static final String CUSTOM_NAME_TAG = "CustomName";
    private static final int WOBBLE_EVENT = 1;

    public enum WobbleStyle
    {
        POSITIVE(7),
        NEGATIVE(10);

        private final int duration;

        WobbleStyle(int duration)
        {
            this.duration = duration;
        }

        public int getDuration()
        {
            return duration;
        }
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1)
    {
        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            Block block = Block.getBlockFromItem(stack.getItem());
            return !stack.isEmpty()
                && !(stack.getItem() instanceof GlassJarItemBlock)
                && !(block instanceof BlockShulkerBox);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            markAndNotify();
        }
    };

    private ItemStack lid = createDefaultLid();
    private int itemRotation;
    private String customName;
    private long wobbleStartedAt;
    private WobbleStyle wobbleStyle;
    private boolean loading;

    public ItemStack getStoredItem()
    {
        return itemHandler.getStackInSlot(0).copy();
    }

    public void setStoredItem(ItemStack stack)
    {
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        if (!stored.isEmpty()) {
            stored.setCount(Math.min(stored.getCount(), stored.getMaxStackSize()));
        }
        itemHandler.setStackInSlot(0, stored);
    }

    public ItemStack insert(ItemStack stack)
    {
        return itemHandler.insertItem(0, stack, false);
    }

    public ItemStack extractAll()
    {
        ItemStack stored = itemHandler.getStackInSlot(0);
        return stored.isEmpty() ? ItemStack.EMPTY : itemHandler.extractItem(0, stored.getCount(), false);
    }

    public boolean isEmpty()
    {
        return itemHandler.getStackInSlot(0).isEmpty();
    }

    public ItemStack getLid()
    {
        return lid.copy();
    }

    public void setLid(ItemStack stack)
    {
        lid = stack.isEmpty() ? createDefaultLid() : copySingle(stack);
        markAndNotify();
    }

    public int getItemRotation()
    {
        return itemRotation;
    }

    public void setItemRotation(int itemRotation)
    {
        this.itemRotation = itemRotation & 15;
        markAndNotify();
    }

    public boolean hasCustomName()
    {
        return customName != null && !customName.isEmpty();
    }

    public String getCustomName()
    {
        return customName;
    }

    public void setCustomName(@Nullable String customName)
    {
        this.customName = customName == null || customName.isEmpty() ? null : customName;
        markAndNotify();
    }

    public int getComparatorLevel()
    {
        ItemStack stored = itemHandler.getStackInSlot(0);
        if (stored.isEmpty()) {
            return 0;
        }
        return MathHelper.floor((float) stored.getCount() / (float) stored.getMaxStackSize() * 14.0F) + 1;
    }

    public int getContentLight()
    {
        ItemStack stored = itemHandler.getStackInSlot(0);
        if (stored.isEmpty() || !(stored.getItem() instanceof ItemBlock)) {
            return 0;
        }
        Block block = ((ItemBlock) stored.getItem()).getBlock();
        if (block == Blocks.AIR || block == TSDBlocks.GLASS_JAR) {
            return 0;
        }
        try {
            IBlockState state = block.getStateFromMeta(stored.getMetadata());
            return MathHelper.clamp(block.getLightValue(state), 0, 15);
        } catch (RuntimeException ignored) {
            return MathHelper.clamp(block.getLightValue(block.getDefaultState()), 0, 15);
        }
    }

    public void wobble(WobbleStyle style)
    {
        if (world != null && !world.isRemote) {
            world.addBlockEvent(pos, getBlockType(), WOBBLE_EVENT, style.ordinal());
        }
    }

    public WobbleStyle getWobbleStyle()
    {
        return wobbleStyle;
    }

    public long getWobbleStartedAt()
    {
        return wobbleStartedAt;
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == WOBBLE_EVENT && type >= 0 && type < WobbleStyle.values().length) {
            wobbleStyle = WobbleStyle.values()[type];
            wobbleStartedAt = world == null ? 0L : world.getTotalWorldTime();
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    public ItemStack createJarStack()
    {
        ItemStack stored = getStoredItem();
        ItemStack jar = new ItemStack(TSDBlocks.GLASS_JAR, 1, stored.isEmpty() ? GlassJarItemBlock.EMPTY_META : GlassJarItemBlock.FILLED_META);
        writeJarData(jar, stored, lid);
        if (hasCustomName()) {
            jar.setStackDisplayName(customName);
        }
        return jar;
    }

    public void readFromJarStack(ItemStack jar)
    {
        loading = true;
        try {
            NBTTagCompound data = getJarData(jar);
            setStoredItem(readStack(data, CONTENT_TAG));
            ItemStack savedLid = readStack(data, LID_TAG);
            lid = savedLid.isEmpty() ? createDefaultLid() : copySingle(savedLid);
            itemRotation = data == null ? 0 : data.getInteger(ROTATION_TAG) & 15;
            customName = jar.hasDisplayName() ? jar.getDisplayName() : null;
        } finally {
            loading = false;
        }
        markAndNotify();
    }

    public static ItemStack createJarStack(ItemStack content, ItemStack lid, int rotation)
    {
        ItemStack jar = new ItemStack(TSDBlocks.GLASS_JAR, 1, content.isEmpty() ? GlassJarItemBlock.EMPTY_META : GlassJarItemBlock.FILLED_META);
        writeJarData(jar, content, lid);
        return jar;
    }

    public static ItemStack getStoredItem(ItemStack jar)
    {
        return readStack(getJarData(jar), CONTENT_TAG);
    }

    public static ItemStack getLid(ItemStack jar)
    {
        ItemStack lid = readStack(getJarData(jar), LID_TAG);
        return lid.isEmpty() ? createDefaultLid() : copySingle(lid);
    }

    public static int getItemRotation(ItemStack jar)
    {
        return 0;
    }

    public static ItemStack createDefaultLid()
    {
        return new ItemStack(TFBlocks.twilight_log, 1, 0);
    }

    public static ItemStack createCicadaLid()
    {
        return new ItemStack(TFBlocks.twilight_log, 1, 1);
    }

    private static void writeJarData(ItemStack jar, ItemStack content, ItemStack lid)
    {
        NBTTagCompound root = jar.hasTagCompound() ? jar.getTagCompound() : new NBTTagCompound();
        NBTTagCompound data = new NBTTagCompound();
        if (!content.isEmpty()) {
            data.setTag(CONTENT_TAG, content.writeToNBT(new NBTTagCompound()));
        }
        ItemStack actualLid = lid.isEmpty() ? createDefaultLid() : copySingle(lid);
        data.setTag(LID_TAG, actualLid.writeToNBT(new NBTTagCompound()));
        root.setTag(JAR_DATA_TAG, data);
        jar.setTagCompound(root);
    }

    @Nullable
    private static NBTTagCompound getJarData(ItemStack jar)
    {
        return jar.hasTagCompound() && jar.getTagCompound().hasKey(JAR_DATA_TAG, 10)
            ? jar.getTagCompound().getCompoundTag(JAR_DATA_TAG)
            : null;
    }

    private static ItemStack readStack(@Nullable NBTTagCompound data, String key)
    {
        if (data == null || !data.hasKey(key, 10)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(data.getCompoundTag(key));
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    private static ItemStack copySingle(ItemStack stack)
    {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    private void markAndNotify()
    {
        if (loading) {
            return;
        }
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.updateComparatorOutputLevel(pos, getBlockType());
            world.checkLight(pos);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag(CONTENT_TAG, itemHandler.serializeNBT());
        compound.setTag(LID_TAG, lid.writeToNBT(new NBTTagCompound()));
        compound.setInteger(ROTATION_TAG, itemRotation);
        if (hasCustomName()) {
            compound.setString(CUSTOM_NAME_TAG, customName);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        loading = true;
        try {
            if (compound.hasKey(CONTENT_TAG, 10)) {
                itemHandler.deserializeNBT(compound.getCompoundTag(CONTENT_TAG));
            } else {
                itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            }
            ItemStack savedLid = compound.hasKey(LID_TAG, 10) ? new ItemStack(compound.getCompoundTag(LID_TAG)) : ItemStack.EMPTY;
            lid = savedLid.isEmpty() ? createDefaultLid() : copySingle(savedLid);
            itemRotation = compound.getInteger(ROTATION_TAG) & 15;
            customName = compound.hasKey(CUSTOM_NAME_TAG, 8) ? compound.getString(CUSTOM_NAME_TAG) : null;
        } finally {
            loading = false;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.UP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.UP) {
            return (T) itemHandler;
        }
        return super.getCapability(capability, facing);
    }
}
