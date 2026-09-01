package xy177.twilightsparksdelight.common.tile;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CampfireCookingRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CampfireCookingRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityStove;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import xy177.twilightsparksdelight.common.block.GiantKitchenStructure;

public class TileEntityGiantStove extends TileEntityStove
{
    private final NonNullList<ItemStack> itemStacks;
    private final int[] cookingTimes;
    private final int[] cookingTimesTotal;
    private boolean structureChecked;

    public TileEntityGiantStove()
    {
        this(6);
    }

    protected TileEntityGiantStove(int slotCount)
    {
        this.itemStacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
        this.cookingTimes = new int[slotCount];
        this.cookingTimesTotal = new int[slotCount];
    }

    @Override
    public void update()
    {
        if (this.world == null) {
            return;
        }
        if (this.world.isRemote) {
            animationTick();
            return;
        }
        if (!this.structureChecked) {
            GiantKitchenStructure.repairLegacyLayout(this.world, this.pos, this.world.getBlockState(this.pos));
            this.structureChecked = true;
        }

        IBlockState state = this.world.getBlockState(this.pos);
        boolean lit = state.getBlock() instanceof BlockStove && state.getValue(BlockStove.LIT);
        if (isStoveBlockedAbove()) {
            if (!isEmpty()) {
                dropAllItems();
            }
            return;
        }
        if (lit) {
            cookAndOutputItems();
            return;
        }
        for (int slot = 0; slot < this.cookingTimes.length; slot++) {
            if (this.cookingTimes[slot] > 0) {
                this.cookingTimes[slot] = Math.max(0, this.cookingTimes[slot] - 2);
            }
        }
    }

    @Override
    public int getNextEmptySlot()
    {
        int limit = Math.min(getSizeInventory(), getProcessingSlotLimit());
        for (int slot = 0; slot < limit; slot++) {
            if (this.itemStacks.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public boolean addItem(ItemStack stack, CampfireCookingRecipe recipe, int slot)
    {
        if (slot < 0 || slot >= Math.min(getSizeInventory(), getProcessingSlotLimit())
            || !this.itemStacks.get(slot).isEmpty()) {
            return false;
        }
        this.cookingTimes[slot] = 0;
        this.cookingTimesTotal[slot] = recipe.getCookingTime();
        this.itemStacks.set(slot, stack.splitStack(1));
        markDirty();
        return true;
    }

    protected int getProcessingSlotLimit()
    {
        return getSizeInventory();
    }

    public int getKitchenStructureSize()
    {
        if (this.world == null) {
            return 2;
        }
        return GiantKitchenStructure.getStructureSize(this.world.getBlockState(this.pos).getBlock());
    }

    public float getRenderOffsetX(int slot)
    {
        int columns = getSizeInventory() > 6 ? 4 : 3;
        float spacing = getSizeInventory() > 6 ? 0.9F : 0.6F;
        return ((columns - 1) * 0.5F - slot % columns) * spacing;
    }

    public float getRenderOffsetZ(int slot)
    {
        int columns = getSizeInventory() > 6 ? 4 : 3;
        int rows = (getSizeInventory() + columns - 1) / columns;
        float spacing = getSizeInventory() > 6 ? 0.9F : 0.8F;
        return ((rows - 1) * 0.5F - slot / columns) * spacing;
    }

    @Override
    public boolean isStoveBlockedAbove()
    {
        if (this.world == null) {
            return false;
        }
        IBlockState state = this.world.getBlockState(this.pos);
        EnumFacing facing = state.getBlock() instanceof BlockStove
            ? state.getValue(BlockStove.FACING)
            : EnumFacing.NORTH;
        int size = getKitchenStructureSize();
        for (BlockPos structurePos : GiantKitchenStructure.getStructurePositions(this.pos, facing, size)) {
            if (structurePos.getY() != this.pos.getY() + size - 1) {
                continue;
            }
            BlockPos above = structurePos.up();
            IBlockState aboveState = this.world.getBlockState(above);
            AxisAlignedBB collision = aboveState.getCollisionBoundingBox(this.world, above);
            if (collision != null && collision != Block.NULL_AABB) {
                return true;
            }
        }
        return false;
    }

    private void cookAndOutputItems()
    {
        boolean changed = false;
        for (int slot = 0; slot < getSizeInventory(); slot++) {
            ItemStack input = this.itemStacks.get(slot);
            if (input.isEmpty()) {
                continue;
            }
            this.cookingTimes[slot]++;
            if (this.cookingTimes[slot] < this.cookingTimesTotal[slot]) {
                continue;
            }
            CampfireCookingRecipe recipe = CampfireCookingRecipeManager.findRecipe(input);
            if (recipe != null && !recipe.getResultStack().isEmpty()) {
                spawnOutput(recipe.getResultStack().copy());
            }
            this.itemStacks.set(slot, ItemStack.EMPTY);
            this.cookingTimes[slot] = 0;
            this.cookingTimesTotal[slot] = 0;
            changed = true;
        }
        if (changed) {
            markDirty();
        }
    }

    private void dropAllItems()
    {
        for (int slot = 0; slot < getSizeInventory(); slot++) {
            ItemStack stack = this.itemStacks.get(slot);
            if (!stack.isEmpty()) {
                spawnOutput(stack.copy());
                this.itemStacks.set(slot, ItemStack.EMPTY);
                this.cookingTimes[slot] = 0;
                this.cookingTimesTotal[slot] = 0;
            }
        }
        markDirty();
    }

    private void spawnOutput(ItemStack stack)
    {
        IBlockState state = this.world.getBlockState(this.pos);
        EnumFacing facing = state.getBlock() instanceof BlockStove
            ? state.getValue(BlockStove.FACING)
            : EnumFacing.NORTH;
        int size = getKitchenStructureSize();
        EntityItem item = new EntityItem(
            this.world,
            GiantKitchenStructure.getCenterX(this.pos, facing, size),
            this.pos.getY() + size + 0.05D,
            GiantKitchenStructure.getCenterZ(this.pos, facing, size),
            stack
        );
        item.motionX = this.world.rand.nextGaussian() * 0.01D;
        item.motionY = 0.1D;
        item.motionZ = this.world.rand.nextGaussian() * 0.01D;
        this.world.spawnEntity(item);
    }

    private void animationTick()
    {
        IBlockState state = this.world.getBlockState(this.pos);
        if (!(state.getBlock() instanceof BlockStove) || !state.getValue(BlockStove.LIT)) {
            return;
        }
        EnumFacing facing = state.getValue(BlockStove.FACING);
        int size = getKitchenStructureSize();
        double centerX = GiantKitchenStructure.getCenterX(this.pos, facing, size);
        double centerZ = GiantKitchenStructure.getCenterZ(this.pos, facing, size);
        for (int slot = 0; slot < getSizeInventory(); slot++) {
            if (getStackInSlot(slot).isEmpty() || this.world.rand.nextFloat() >= 0.2F) {
                continue;
            }
            float xOffset = getRenderOffsetX(slot);
            float zOffset = getRenderOffsetZ(slot);
            if (facing.getAxis() == EnumFacing.Axis.Z) {
                float swap = xOffset;
                xOffset = zOffset;
                zOffset = swap;
            }
            double x = centerX - facing.getFrontOffsetX() * zOffset + facing.rotateY().getFrontOffsetX() * xOffset;
            double z = centerZ - facing.getFrontOffsetZ() * zOffset + facing.rotateY().getFrontOffsetZ() * xOffset;
            for (int particle = 0; particle < 3; particle++) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, this.pos.getY() + size, z, 0.0D, 5.0E-4D, 0.0D);
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        int size = getKitchenStructureSize();
        return new AxisAlignedBB(this.pos.add(-size, 0, -size), this.pos.add(size + 1, size + 1, size + 1));
    }

    @Override
    public int getSizeInventory()
    {
        return this.itemStacks.size();
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack stack : this.itemStacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < getSizeInventory() ? this.itemStacks.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack result = ItemStackHelper.getAndSplit(this.itemStacks, index, count);
        if (!result.isEmpty()) {
            this.cookingTimes[index] = 0;
            this.cookingTimesTotal[index] = 0;
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack result = ItemStackHelper.getAndRemove(this.itemStacks, index);
        if (!result.isEmpty()) {
            this.cookingTimes[index] = 0;
            this.cookingTimesTotal[index] = 0;
            markDirty();
        }
        return result;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.itemStacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.world != null && this.world.getTileEntity(this.pos) == this
            && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index >= 0 && index < getProcessingSlotLimit()
            && CampfireCookingRecipeManager.findRecipe(stack) != null;
    }

    @Override
    public int getField(int id)
    {
        if (id >= 0 && id < getSizeInventory()) {
            return this.cookingTimes[id];
        }
        int totalIndex = id - getSizeInventory();
        return totalIndex >= 0 && totalIndex < getSizeInventory() ? this.cookingTimesTotal[totalIndex] : 0;
    }

    @Override
    public void setField(int id, int value)
    {
        if (id >= 0 && id < getSizeInventory()) {
            this.cookingTimes[id] = value;
            return;
        }
        int totalIndex = id - getSizeInventory();
        if (totalIndex >= 0 && totalIndex < getSizeInventory()) {
            this.cookingTimesTotal[totalIndex] = value;
        }
    }

    @Override
    public int getFieldCount()
    {
        return getSizeInventory() * 2;
    }

    @Override
    public void clear()
    {
        for (int slot = 0; slot < getSizeInventory(); slot++) {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            this.cookingTimes[slot] = 0;
            this.cookingTimesTotal[slot] = 0;
        }
        markDirty();
    }

    @Override
    public String getName()
    {
        return "container.farmersdelight.stove";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(getName());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.itemStacks);
        compound.setIntArray("CookingTimes", this.cookingTimes);
        compound.setIntArray("CookingTotalTimes", this.cookingTimesTotal);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        clearLocal();
        ItemStackHelper.loadAllItems(compound, this.itemStacks);
        copyTimes(compound.getIntArray("CookingTimes"), this.cookingTimes);
        copyTimes(compound.getIntArray("CookingTotalTimes"), this.cookingTimesTotal);
    }

    private void clearLocal()
    {
        for (int slot = 0; slot < getSizeInventory(); slot++) {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            this.cookingTimes[slot] = 0;
            this.cookingTimesTotal[slot] = 0;
        }
    }

    private static void copyTimes(int[] source, int[] target)
    {
        System.arraycopy(source, 0, target, 0, Math.min(source.length, target.length));
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }
}
