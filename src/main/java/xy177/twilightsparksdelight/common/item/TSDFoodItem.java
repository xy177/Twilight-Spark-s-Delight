package xy177.twilightsparksdelight.common.item;

import com.wdcftgg.farmersdelightlegacy.api.food.AddonFoodItem;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.ArrayList;
import java.util.List;

public class TSDFoodItem extends AddonFoodItem
{
    private final List<EffectEntry> effects = new ArrayList<>();
    private final List<EffectEntry> displayEffects = new ArrayList<>();
    private final List<TooltipEntry> customTooltips = new ArrayList<>();
    private final Item containerItem;
    private ResourceLocation containerItemId;
    private final EnumAction action;
    private int useDuration = 32;

    public TSDFoodItem(int amount, float saturation, boolean wolfFood)
    {
        this(amount, saturation, wolfFood, null, EnumAction.EAT);
    }

    public TSDFoodItem(int amount, float saturation, boolean wolfFood, Item containerItem, EnumAction action)
    {
        super(amount, saturation, wolfFood);
        this.containerItem = containerItem;
        this.action = action;
    }

    public TSDFoodItem setContainerItem(ResourceLocation containerItemId)
    {
        this.containerItemId = containerItemId;
        return this;
    }

    public TSDFoodItem addEffect(ResourceLocation id, int duration, int amplifier, float chance)
    {
        if (id != null) {
            effects.add(new EffectEntry(id, duration, amplifier, chance));
        }
        return this;
    }

    public TSDFoodItem addDisplayEffect(ResourceLocation id, int duration, int amplifier, float chance)
    {
        if (id != null) {
            displayEffects.add(new EffectEntry(id, duration, amplifier, chance));
        }
        return this;
    }

    public TSDFoodItem setUseDuration(int useDuration)
    {
        this.useDuration = Math.max(1, useDuration);
        return this;
    }

    public TSDFoodItem addTooltip(String translationKey, TextFormatting color)
    {
        if (translationKey != null && color != null) {
            customTooltips.add(new TooltipEntry(translationKey, color));
        }
        return this;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return useDuration;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return action;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return resolveContainerItem() != null;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        Item container = resolveContainerItem();
        return container == null ? ItemStack.EMPTY : new ItemStack(container);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity)
    {
        ItemStack result = super.onItemUseFinish(stack, world, entity);
        Item containerItem = resolveContainerItem();
        if (containerItem == null) {
            return result;
        }
        ItemStack container = new ItemStack(containerItem);
        if (result.isEmpty()) {
            return container;
        }
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(container)) {
                player.dropItem(container, false);
            }
        }
        return result;
    }

    private Item resolveContainerItem()
    {
        return containerItem != null || containerItemId == null
            ? containerItem
            : ForgeRegistries.ITEMS.getValue(containerItemId);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote) {
            for (EffectEntry effect : effects) {
                if (world.rand.nextFloat() <= effect.chance) {
                    Potion potion = ForgeRegistries.POTIONS.getValue(effect.id);
                    if (potion != null) {
                        player.addPotionEffect(new PotionEffect(potion, effect.duration, effect.amplifier, false, false));
                    }
                }
            }
            afterEaten(stack, world, player);
        }
    }

    protected void afterEaten(ItemStack stack, World world, EntityPlayer player)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        addNagaMixedRiceVariantTooltip(stack, tooltip);
        if (Configuration.foodEffectTooltip) {
            addEffectTooltips(tooltip);
            addNagaMixedRiceEffectTooltip(stack, tooltip);
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("TsdJeiHint")) {
            tooltip.add(I18n.translateToLocal(stack.getTagCompound().getString("TsdJeiHint")));
        }
        for (TooltipEntry entry : customTooltips) {
            tooltip.add(entry.color + I18n.translateToLocal(entry.translationKey));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void addNagaMixedRiceVariantTooltip(ItemStack stack, List<String> tooltip)
    {
        String ingredient = getNagaMixedRiceIngredient(stack);
        if (ingredient == null) {
            return;
        }
        if (TSDItems.NAGA_MIXED_RICE_HYDRA.equals(ingredient)) {
            tooltip.add(TextFormatting.GOLD + "["
                + I18n.translateToLocal("twilight_spark_delight.tooltip.naga_mixed_rice.hydra") + "]");
        } else if (TSDItems.NAGA_MIXED_RICE_EXPERIMENT.equals(ingredient)) {
            tooltip.add(TextFormatting.AQUA + "["
                + I18n.translateToLocal("twilight_spark_delight.tooltip.naga_mixed_rice.experiment") + "]");
        }
    }

    @SideOnly(Side.CLIENT)
    private static String getNagaMixedRiceIngredient(ItemStack stack)
    {
        Item nagaMixedRiceBlockItem = Item.getItemFromBlock(xy177.twilightsparksdelight.common.registry.TSDBlocks.NAGA_MIXED_RICE);
        if ((stack.getItem() != TSDItems.BOWL_OF_NAGA_MIXED_RICE
            && stack.getItem() != TSDItems.NAGA_MIXED_RICE_CUP
            && stack.getItem() != nagaMixedRiceBlockItem)
            || !stack.hasTagCompound()
            || !stack.getTagCompound().hasKey(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, 8)) {
            return null;
        }
        return stack.getTagCompound().getString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG);
    }

    @SideOnly(Side.CLIENT)
    private void addEffectTooltips(List<String> tooltip)
    {
        for (EffectEntry entry : effects) {
            addEffectTooltip(tooltip, entry);
        }
        for (EffectEntry entry : displayEffects) {
            addEffectTooltip(tooltip, entry);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void addNagaMixedRiceEffectTooltip(ItemStack stack, List<String> tooltip)
    {
        String ingredient = getNagaMixedRiceIngredient(stack);
        if (ingredient == null) {
            return;
        }
        int duration = stack.getItem() == TSDItems.NAGA_MIXED_RICE_CUP ? 3600 : 6000;
        if (TSDItems.NAGA_MIXED_RICE_HYDRA.equals(ingredient)) {
            ResourceLocation effectId = new ResourceLocation("minecraft", "fire_resistance");
            ResourceLocation fireRangeId = new ResourceLocation("twilightdelight", "fire_range");
            if (Loader.isModLoaded("twilightdelight") && ForgeRegistries.POTIONS.getValue(fireRangeId) != null) {
                effectId = fireRangeId;
            }
            addEffectTooltip(tooltip, new EffectEntry(effectId, duration, 0, 1.0F));
        } else if (TSDItems.NAGA_MIXED_RICE_EXPERIMENT.equals(ingredient)) {
            addEffectTooltip(
                tooltip,
                new EffectEntry(
                    new ResourceLocation(TwilightSparksDelight.MODID, "sorrow"),
                    duration,
                    0,
                    1.0F
                )
            );
        }
    }

    @SideOnly(Side.CLIENT)
    private static void addEffectTooltip(List<String> tooltip, EffectEntry entry)
    {
        Potion potion = ForgeRegistries.POTIONS.getValue(entry.id);
        if (potion == null) {
            return;
        }
        PotionEffect effect = new PotionEffect(potion, entry.duration, entry.amplifier);
        String duration = Potion.getPotionDurationString(effect, 1.0F);
        String effectName = I18n.translateToLocal(effect.getEffectName());
        if (entry.amplifier > 0) {
            effectName = effectName + " " + I18n.translateToLocal("potion.potency." + entry.amplifier);
        }
        TextComponentTranslation effectLine = new TextComponentTranslation("farmersdelight.tooltip.food.effect", effectName, duration);
        effectLine.getStyle().setColor(TextFormatting.BLUE);
        tooltip.add(effectLine.getFormattedText());

        if (entry.chance < 0.999F) {
            TextComponentTranslation chanceLine = new TextComponentTranslation("farmersdelight.tooltip.food.effect_chance", Math.round(entry.chance * 100.0F));
            chanceLine.getStyle().setColor(TextFormatting.BLUE);
            tooltip.add(chanceLine.getFormattedText());
        }
    }

    private static class EffectEntry
    {
        private final ResourceLocation id;
        private final int duration;
        private final int amplifier;
        private final float chance;

        private EffectEntry(ResourceLocation id, int duration, int amplifier, float chance)
        {
            this.id = id;
            this.duration = Math.max(0, duration);
            this.amplifier = Math.max(0, amplifier);
            this.chance = Math.max(0.0F, Math.min(1.0F, chance));
        }
    }

    private static class TooltipEntry
    {
        private final String translationKey;
        private final TextFormatting color;

        private TooltipEntry(String translationKey, TextFormatting color)
        {
            this.translationKey = translationKey;
            this.color = color;
        }
    }
}
