package xy177.twilightsparksdelight.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twilightforest.init.TFBlocks;
import xy177.twilightsparksdelight.common.block.TSDMasonJarBlock;

public final class TSDMasonJarItem extends BlockItem {
    public TSDMasonJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack content(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        return tag == null ? ItemStack.EMPTY : ItemStack.of(tag.getCompound("Item"));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return content(stack).isEmpty() ? 64 : 1;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStack stored = content(stack);
        if (stored.is(TFBlocks.FIREFLY.get().asItem())) return Component.translatable("block.twilight_spark_delight.mason_jar.firefly");
        if (stored.is(TFBlocks.CICADA.get().asItem())) return Component.translatable("block.twilight_spark_delight.mason_jar.cicada");
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, java.util.List<Component> tooltip, TooltipFlag flag) {
        ItemStack stored = content(stack);
        if (!stored.isEmpty()) tooltip.add(Component.translatable("twilight_spark_delight.tooltip.glass_jar.contents",
                stored.getHoverName(), stored.getCount()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        ItemStack held = context.getItemInHand();
        ItemStack critter = new ItemStack(level.getBlockState(pos).getBlock());
        if (player != null && content(held).isEmpty() && TSDMasonJarBlock.isCritter(critter)) {
            if (!level.isClientSide) {
                ItemStack filled = held.copyWithCount(1);
                filled.getOrCreateTagElement("BlockEntityTag").put("Item", critter.save(new CompoundTag()));
                level.removeBlock(pos, false);
                if (!player.getAbilities().instabuild) held.shrink(1);
                if (held.isEmpty()) player.setItemInHand(context.getHand(), filled);
                else if (!player.addItem(filled)) player.drop(filled, false);
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_FILL,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer;
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new xy177.twilightsparksdelight.client.TSDMasonJarRenderer.ItemRenderer();
                return renderer;
            }
        });
    }
}
