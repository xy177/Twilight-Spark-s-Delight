package xy177.twilightsparksdelight.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import twilightforest.init.TFBlocks;
import xy177.twilightsparksdelight.common.event.TSDLootEvents;
import xy177.twilightsparksdelight.registry.TSDItems;

/** Adds knife harvesting to the existing plant loot without replacing its table. */
public final class BrackenLootModifier extends LootModifier {
    public static final Codec<BrackenLootModifier> CODEC =
            RecordCodecBuilder.create(instance -> codecStart(instance).apply(instance, BrackenLootModifier::new));

    public BrackenLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        var state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        var tool = context.getParamOrNull(LootContextParams.TOOL);
        if (state != null && state.is(TFBlocks.FIDDLEHEAD.get()) && tool != null && TSDLootEvents.isKnife(tool)
                && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            int count = 1 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, tool)
                    + 2 * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
            loot.add(new ItemStack(TSDItems.BRACKEN.get(), count));
        }
        return loot;
    }

    @Override
    public Codec<BrackenLootModifier> codec() {
        return CODEC;
    }
}
