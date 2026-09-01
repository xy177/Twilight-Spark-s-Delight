package xy177.twilightsparksdelight.integration.fruitsdelight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.tile.TileEntityGloryCrucible;
import xy177.twilightsparksdelight.common.util.GloryCrucibleCapacity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Optional, reflection-only compatibility for Fruits Delight Legacy's cauldron chain. */
public final class FruitsDelightCauldronCompat
{
    public static final String MODID = "fruits_delight_legacy";
    private static final String FUTURE_DELIGHT_MODID = "farmers_future_delight";
    public static final String MODE_LEMONADE = "lemonade";
    public static final String MODE_FRUIT = "fruit";
    public static final String MODE_JELLY = "jelly";
    public static final String MODE_JELLO = "jello";
    private static final int FRUIT_RECIPE_LEVELS = 12;
    private static final int OUTPUT_PORTIONS_PER_BUCKET = 3;

    private static final ResourceLocation LEMON_SLICE = new ResourceLocation(MODID, "lemon_slice");
    private static final ResourceLocation SUGAR = new ResourceLocation("minecraft", "sugar");
    private static final ResourceLocation SLIME_BALL = new ResourceLocation("minecraft", "slime_ball");
    private static final ResourceLocation GLASS_BOTTLE = new ResourceLocation("minecraft", "glass_bottle");
    private static final ResourceLocation BOWL = new ResourceLocation("minecraft", "bowl");

    private static List<FruitSpec> fruitSpecs = Collections.emptyList();
    private static boolean specsInitialized;
    private static Boolean cauldronRecipesEnabled;

    private FruitsDelightCauldronCompat()
    {
    }

    public static boolean isAvailable()
    {
        return Loader.isModLoaded(MODID) && isCauldronRecipesEnabled() && !getFruitSpecs().isEmpty();
    }

    public static boolean handlePlayerInteraction(
        World world,
        BlockPos pos,
        EntityPlayer player,
        EnumHand hand,
        TileEntityGloryCrucible crucible
    )
    {
        if (!isAvailable()) {
            return false;
        }
        ItemStack held = player.getHeldItem(hand);
        Action action = findAction(crucible, held, isHeated(world, pos));
        if (action == null) {
            return false;
        }
        if (!world.isRemote) {
            crucible.applyFruitsCauldronState(action.nextMode, action.nextFruit, action.nextLevel);
            if (action.output.isEmpty()) {
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            } else if (player.capabilities.isCreativeMode) {
                give(player, action.output.copy());
            } else {
                replaceOneHeldItem(player, hand, action.output.copy());
            }
            if (action.sound != null) {
                world.playSound(null, pos, action.sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
        return true;
    }

    public static Action findAction(TileEntityGloryCrucible crucible, ItemStack held, boolean heated)
    {
        if (!isAvailable() || held.isEmpty()) {
            return null;
        }
        String mode = crucible.getFruitsCauldronMode();
        if (mode.isEmpty()) {
            return isLemonSlice(held) && crucible.hasWholeBucketsOfPlainWaterForFruits()
                ? new Action(MODE_LEMONADE, "", getMaxFruitLevel(), ItemStack.EMPTY, null)
                : null;
        }
        if (MODE_LEMONADE.equals(mode)) {
            FruitSpec fruit = findFruit(held);
            return fruit != null && heated
                ? new Action(MODE_FRUIT, fruit.id, 4 / fruit.jellyCost, ItemStack.EMPTY, null)
                : null;
        }
        String fruitId = crucible.getFruitsCauldronFruit();
        FruitSpec fruit = MODE_LEMONADE.equals(mode) ? findFruit("lemon") : findFruit(fruitId);
        if (fruit == null) {
            return null;
        }
        int level = crucible.getFruitsCauldronLevel();
        if (MODE_FRUIT.equals(mode)) {
            int maxFruitLevel = getMaxFruitLevel();
            FruitSpec rawFruit = findFruit(held);
            if (rawFruit != null && fruit.id.equals(rawFruit.id) && heated && level < maxFruitLevel) {
                return new Action(MODE_FRUIT, fruit.id,
                    Math.min(maxFruitLevel, level + 4 / fruit.jellyCost), ItemStack.EMPTY, null);
            }
            if (isJelly(held, fruit.id) && level < maxFruitLevel) {
                return new Action(MODE_FRUIT, fruit.id,
                    Math.min(maxFruitLevel, level + 4), ItemStack.EMPTY, null);
            }
            return level == maxFruitLevel && isItem(held, SUGAR) && heated
                ? new Action(MODE_JELLY, fruit.id, crucible.getFruitsOutputPortions(), ItemStack.EMPTY, null)
                : null;
        }
        if (MODE_JELLY.equals(mode)) {
            if (isItem(held, SLIME_BALL) && heated) {
                return new Action(MODE_JELLO, fruit.id, Math.min(getMaxOutputLevel(), level), ItemStack.EMPTY, null);
            }
            if (isItem(held, GLASS_BOTTLE) && level > 0) {
                ItemStack jelly = getJelly(fruit.id);
                return jelly.isEmpty() ? null
                    : new Action(level <= 1 ? "" : MODE_JELLY, fruit.id, Math.max(0, level - 1),
                        jelly, SoundEvents.ITEM_BOTTLE_FILL);
            }
            return null;
        }
        if (MODE_JELLO.equals(mode) && isItem(held, BOWL) && level > 0) {
            ItemStack jello = getJello(fruit.id);
            return jello.isEmpty() ? null
                : new Action(level <= 1 ? "" : MODE_JELLO, fruit.id, Math.max(0, level - 1),
                    jello, SoundEvents.ITEM_BOTTLE_FILL);
        }
        return null;
    }

    public static int getMaxLevel(String mode)
    {
        return isOutputMode(mode)
            ? getMaxOutputLevel()
            : getMaxFruitLevel();
    }

    public static boolean isOutputMode(String mode)
    {
        return MODE_JELLY.equals(mode) || MODE_JELLO.equals(mode);
    }

    public static int scaleLegacyLevel(String mode, int level)
    {
        return Math.min(getMaxLevel(mode), Math.max(0, level) * getBucketCount());
    }

    private static int getMaxFruitLevel()
    {
        return FRUIT_RECIPE_LEVELS;
    }

    private static int getMaxOutputLevel()
    {
        return OUTPUT_PORTIONS_PER_BUCKET * getBucketCount();
    }

    private static int getBucketCount()
    {
        return Math.max(1, GloryCrucibleCapacity.getCapacityMb() / GloryCrucibleCapacity.BUCKET_MB);
    }

    public static int getStateColor(String mode, String fruitId)
    {
        FruitSpec fruit = MODE_LEMONADE.equals(mode) ? findFruit("lemon") : findFruit(fruitId);
        return fruit == null ? 0xFFFFFF : fruit.color;
    }

    public static FluidStack getStateFluid(String mode, String fruitId)
    {
        if (MODE_JELLY.equals(mode)) {
            return fluidStack(fruitId + "_jam");
        }
        if (MODE_JELLO.equals(mode)) {
            return fluidStack(fruitId + "_jello");
        }
        return null;
    }

    private static FluidStack fluidStack(String path)
    {
        Fluid fluid = FluidRegistry.getFluid(MODID + "_" + path);
        return fluid == null ? null : new FluidStack(fluid, 1);
    }

    private static ItemStack getJelly(String fruitId)
    {
        return getItem(fruitId + "_jelly");
    }

    private static ItemStack getJello(String fruitId)
    {
        return getItem(fruitId + "_jello");
    }

    private static ItemStack getItem(String path)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, path));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static boolean isLemonSlice(ItemStack stack)
    {
        return isItem(stack, LEMON_SLICE);
    }

    private static boolean isJelly(ItemStack stack, String fruitId)
    {
        return isItem(stack, new ResourceLocation(MODID, fruitId + "_jelly"));
    }

    private static boolean isItem(ItemStack stack, ResourceLocation id)
    {
        return stack.getItem().getRegistryName() != null && id.equals(stack.getItem().getRegistryName());
    }

    private static FruitSpec findFruit(ItemStack stack)
    {
        for (FruitSpec fruit : getFruitSpecs()) {
            if (fruit.accepts(stack.getItem())) {
                return fruit;
            }
        }
        return null;
    }

    private static FruitSpec findFruit(String id)
    {
        for (FruitSpec fruit : getFruitSpecs()) {
            if (fruit.id.equals(id)) {
                return fruit;
            }
        }
        return null;
    }

    private static boolean isHeated(World world, BlockPos pos)
    {
        return com.wdcftgg.farmersdelightlegacy.common.util.HeatSourceHelper.isCookwareHeated(world, pos);
    }

    private static void replaceOneHeldItem(EntityPlayer player, EnumHand hand, ItemStack replacement)
    {
        ItemStack held = player.getHeldItem(hand);
        held.shrink(1);
        if (held.isEmpty()) {
            player.setHeldItem(hand, replacement);
        } else if (!player.inventory.addItemStackToInventory(replacement)) {
            player.dropItem(replacement, false);
        }
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    private static boolean isCauldronRecipesEnabled()
    {
        if (cauldronRecipesEnabled != null) {
            return cauldronRecipesEnabled;
        }
        cauldronRecipesEnabled = true;
        try {
            Class<?> config = Class.forName("xy177.fruitsdelightlegacy.common.config.FDLConfig");
            Field field = config.getField("enableCauldronRecipes");
            cauldronRecipesEnabled = field.getBoolean(null);
        } catch (Throwable ignored) {
            // The optional mod may change its config owner; gameplay compatibility remains enabled if the field is unavailable.
        }
        return cauldronRecipesEnabled;
    }

    private static List<FruitSpec> getFruitSpecs()
    {
        if (specsInitialized) {
            return fruitSpecs;
        }
        specsInitialized = true;
        if (!Loader.isModLoaded(MODID)) {
            return fruitSpecs;
        }
        List<FruitSpec> result = new ArrayList<>();
        try {
            Class<?> fruitTypeClass = Class.forName("xy177.fruitsdelightlegacy.common.food.FruitType");
            Method valuesMethod = fruitTypeClass.getMethod("values");
            Method idMethod = fruitTypeClass.getMethod("getId");
            Method fruitMethod = fruitTypeClass.getMethod("getFruit");
            Field costField = fruitTypeClass.getField("jellyCost");
            Field colorField = fruitTypeClass.getField("color");
            Object[] values = (Object[]) valuesMethod.invoke(null);
            for (Object value : values) {
                String id = (String) idMethod.invoke(value);
                Item item = (Item) fruitMethod.invoke(value);
                int jellyCost = Math.max(1, costField.getInt(value));
                int color = colorField.getInt(value) & 0xFFFFFF;
                result.add(new FruitSpec(id, fruitProviders(id, item), jellyCost, color));
            }
        } catch (Throwable ignored) {
            result.clear();
        }
        fruitSpecs = Collections.unmodifiableList(result);
        return fruitSpecs;
    }

    private static List<Item> fruitProviders(String fruitId, Item primary)
    {
        List<Item> providers = new ArrayList<>();
        addProvider(providers, primary);
        if ("sweetberry".equals(fruitId)) {
            addProvider(providers, FUTURE_DELIGHT_MODID, "sweet_berries");
            addProvider(providers, "futuremc", "sweet_berries");
            addProvider(providers, MODID, "sweetberry");
        } else if ("glowberry".equals(fruitId)) {
            addProvider(providers, FUTURE_DELIGHT_MODID, "glow_berries");
            addProvider(providers, "da", "glow_berry");
            addProvider(providers, "depthsupdate", "glow_berries");
            addProvider(providers, MODID, "glowberry");
        }
        return Collections.unmodifiableList(providers);
    }

    private static void addProvider(List<Item> providers, String modId, String path)
    {
        ResourceLocation id = new ResourceLocation(modId, path);
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item != null && id.equals(item.getRegistryName())) {
            addProvider(providers, item);
        }
    }

    private static void addProvider(List<Item> providers, Item item)
    {
        if (item != null && !providers.contains(item)) {
            providers.add(item);
        }
    }

    public static final class Action
    {
        public final String nextMode;
        public final String nextFruit;
        public final int nextLevel;
        public final ItemStack output;
        public final SoundEvent sound;

        private Action(String nextMode, String nextFruit, int nextLevel, ItemStack output, SoundEvent sound)
        {
            this.nextMode = nextMode;
            this.nextFruit = nextFruit;
            this.nextLevel = nextLevel;
            this.output = output;
            this.sound = sound;
        }
    }

    private static final class FruitSpec
    {
        private final String id;
        private final List<Item> providers;
        private final int jellyCost;
        private final int color;

        private FruitSpec(String id, List<Item> providers, int jellyCost, int color)
        {
            this.id = id;
            this.providers = providers;
            this.jellyCost = jellyCost;
            this.color = color;
        }

        private boolean accepts(Item item)
        {
            return providers.contains(item);
        }
    }
}
