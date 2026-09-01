package xy177.twilightsparksdelight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID, value = Side.CLIENT)
public final class TSDExtendedFoodHudEvents extends Gui
{
    private static final ResourceLocation EXTRA_HUNGER_ICONS =
        new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/extra_hunger_icons.png");
    private static final ResourceLocation EXTRA_SATURATION_ICONS =
        new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/extra_saturation_icons.png");
    private static final int ICON_SIZE = 9;
    private static final int NORMAL_HALF_U = 9;
    private static final int NORMAL_FULL_U = 18;
    private static final int HUNGER_HALF_U = 27;
    private static final int HUNGER_FULL_U = 36;
    private static final int SATURATION_QUARTER_U = 0;
    private static final int SATURATION_HALF_U = 9;
    private static final int SATURATION_THREE_QUARTERS_U = 18;
    private static final int SATURATION_FULL_U = 27;
    private static int foodIconsOffset = 39;
    private static float previewAlpha;
    private static byte previewAlphaDirection = 1;
    private static boolean appleSkinReflectionChecked;
    private static Field appleSkinFoodValuesOverlayField;
    private static Field appleSkinSaturationOverlayField;
    private static Method appleSkinIsFoodMethod;
    private static Method appleSkinModifiedFoodValuesMethod;
    private static Field appleSkinHasAppleCoreField;
    private static Method appleCoreFoodValuesForDisplayMethod;
    private static Field appleSkinFoodValueHungerField;
    private static Method appleSkinSaturationIncrementMethod;

    private TSDExtendedFoodHudEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPreRenderFood(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
            foodIconsOffset = GuiIngameForge.right_height;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderFood(RenderGameOverlayEvent.Post event)
    {
        if (!TSDConfig.extendedFoodStatsEnabled || event.getType() != RenderGameOverlayEvent.ElementType.FOOD) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.getRidingEntity() != null) {
            return;
        }

        FoodStats stats = player.getFoodStats();
        int extraFood = Math.max(0, stats.getFoodLevel() - 20);
        if (extraFood <= 0) {
            return;
        }

        int left = event.getResolution().getScaledWidth() / 2 + 91;
        int top = event.getResolution().getScaledHeight() - foodIconsOffset;

        mc.getTextureManager().bindTexture(EXTRA_HUNGER_ICONS);
        GlStateManager.enableBlend();
        boolean hunger = player.isPotionActive(MobEffects.HUNGER);
        for (int i = 0; i < 10; i++) {
            int threshold = i * 2 + 1;
            if (threshold > extraFood) {
                continue;
            }
            int x = left - i * 8 - 9;
            boolean half = threshold == extraFood;
            if (half) {
                drawIcon(x, top, hunger ? HUNGER_HALF_U : NORMAL_HALF_U);
            } else {
                drawIcon(x, top, hunger ? HUNGER_FULL_U : NORMAL_FULL_U);
            }
        }
        GlStateManager.disableBlend();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderExtraFoodPreview(RenderGameOverlayEvent.Post event)
    {
        if (!TSDConfig.extendedFoodStatsEnabled || event.getType() != RenderGameOverlayEvent.ElementType.FOOD) {
            return;
        }
        if (!isAppleSkinFoodPreviewEnabled()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.getRidingEntity() != null) {
            return;
        }

        FoodPreview preview = getHeldFoodPreview(player);
        if (preview == null || preview.hunger <= 0) {
            resetPreviewAlpha();
            return;
        }

        int currentFood = player.getFoodStats().getFoodLevel();
        int targetFood = Math.min(40, currentFood + preview.hunger);
        if (targetFood <= 20 || targetFood <= currentFood) {
            return;
        }

        int left = event.getResolution().getScaledWidth() / 2 + 91;
        int top = event.getResolution().getScaledHeight() - foodIconsOffset;

        mc.getTextureManager().bindTexture(EXTRA_HUNGER_ICONS);
        enablePreviewAlpha();
        boolean hunger = player.isPotionActive(MobEffects.HUNGER);
        for (int i = 0; i < 10; i++) {
            int slotBase = 20 + i * 2;
            int currentSlotFood = clamp(currentFood - slotBase, 0, 2);
            int targetSlotFood = clamp(targetFood - slotBase, 0, 2);
            if (targetSlotFood <= currentSlotFood) {
                continue;
            }
            int x = left - i * 8 - 9;
            drawIcon(x, top, targetSlotFood == 1
                ? (hunger ? HUNGER_HALF_U : NORMAL_HALF_U)
                : (hunger ? HUNGER_FULL_U : NORMAL_FULL_U));
        }
        disablePreviewAlpha();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderExtraSaturation(RenderGameOverlayEvent.Post event)
    {
        if (!TSDConfig.extendedFoodStatsEnabled || event.getType() != RenderGameOverlayEvent.ElementType.FOOD) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.getRidingEntity() != null) {
            return;
        }

        FoodStats stats = player.getFoodStats();
        int extraFood = Math.max(0, stats.getFoodLevel() - 20);
        float extraSaturation = Math.min(extraFood, Math.max(0.0F, stats.getSaturationLevel() - 20.0F));

        FoodPreview preview = getHeldFoodPreview(player);
        boolean showPreview = preview != null && preview.hunger > 0
            && isAppleSkinFoodPreviewEnabled() && isAppleSkinSaturationOverlayEnabled();
        int targetFood = showPreview ? Math.min(40, stats.getFoodLevel() + preview.hunger) : stats.getFoodLevel();
        float targetSaturation = showPreview
            ? Math.min(targetFood, stats.getSaturationLevel() + preview.saturationIncrement)
            : stats.getSaturationLevel();
        int targetExtraFood = Math.max(0, targetFood - 20);
        float targetExtraSaturation = Math.min(targetExtraFood, Math.max(0.0F, targetSaturation - 20.0F));
        if (extraSaturation <= 0.0F && targetExtraSaturation <= 0.0F) {
            return;
        }

        int left = event.getResolution().getScaledWidth() / 2 + 91;
        int top = event.getResolution().getScaledHeight() - foodIconsOffset;

        mc.getTextureManager().bindTexture(EXTRA_SATURATION_ICONS);
        GlStateManager.enableBlend();
        drawExtraSaturation(left, top, extraFood, extraSaturation);
        if (showPreview && targetExtraSaturation > extraSaturation) {
            enablePreviewAlpha();
            drawExtraSaturationPreview(left, top, extraFood, extraSaturation, targetExtraFood, targetExtraSaturation);
            disablePreviewAlpha();
        }
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        previewAlpha += previewAlphaDirection * 0.125F;
        if (previewAlpha >= 1.5F) {
            previewAlpha = 1.0F;
            previewAlphaDirection = -1;
        } else if (previewAlpha <= -0.5F) {
            resetPreviewAlpha();
        }
    }

    private static void drawExtraSaturation(int left, int top, int extraFood, float extraSaturation)
    {
        for (int i = 0; i < 10; i++) {
            int slotFood = Math.min(2, extraFood - i * 2);
            if (slotFood <= 0) {
                continue;
            }
            float slotSaturation = Math.min(slotFood, Math.max(0.0F, extraSaturation - i * 2));
            if (slotSaturation <= 0.0F) {
                continue;
            }
            int x = left - i * 8 - 9;
            drawIcon(x, top, slotFood == 1 ? halfFoodSaturationU(slotSaturation) : saturationU(slotSaturation / 2.0F));
        }
    }

    private static void drawExtraSaturationPreview(int left, int top, int currentExtraFood, float currentExtraSaturation,
                                                  int targetExtraFood, float targetExtraSaturation)
    {
        for (int i = 0; i < 10; i++) {
            int slotBase = i * 2;
            int currentSlotFood = Math.min(2, Math.max(0, currentExtraFood - slotBase));
            int targetSlotFood = Math.min(2, Math.max(0, targetExtraFood - slotBase));
            if (targetSlotFood <= 0) {
                continue;
            }
            float currentSlotSaturation = Math.min(currentSlotFood, Math.max(0.0F, currentExtraSaturation - slotBase));
            float targetSlotSaturation = Math.min(targetSlotFood, Math.max(0.0F, targetExtraSaturation - slotBase));
            if (targetSlotSaturation <= currentSlotSaturation) {
                continue;
            }
            int x = left - i * 8 - 9;
            drawIcon(x, top, targetSlotFood == 1
                ? halfFoodSaturationU(targetSlotSaturation)
                : saturationU(targetSlotSaturation / 2.0F));
        }
    }

    private static FoodPreview getHeldFoodPreview(EntityPlayer player)
    {
        if (!Loader.isModLoaded("appleskin")) {
            return null;
        }
        ItemStack stack = player.getHeldItemMainhand();
        if (!isFood(stack)) {
            stack = player.getHeldItemOffhand();
        }
        if (!isFood(stack)) {
            return null;
        }
        return getFoodPreview(stack, player);
    }

    private static boolean isFood(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return false;
        }
        prepareAppleSkinReflection();
        if (appleSkinIsFoodMethod != null) {
            try {
                return (Boolean) appleSkinIsFoodMethod.invoke(null, stack);
            } catch (ReflectiveOperationException ignored) {
                return false;
            }
        }
        return stack.getItem() instanceof ItemFood;
    }

    private static FoodPreview getFoodPreview(ItemStack stack, EntityPlayer player)
    {
        prepareAppleSkinReflection();
        if (appleSkinModifiedFoodValuesMethod != null) {
            try {
                Object values = appleSkinModifiedFoodValuesMethod.invoke(null, stack, player);
                if (isAppleCoreLoaded() && appleCoreFoodValuesForDisplayMethod != null) {
                    values = appleCoreFoodValuesForDisplayMethod.invoke(null, values, player);
                }
                return new FoodPreview(
                    appleSkinFoodValueHungerField.getInt(values),
                    ((Number) appleSkinSaturationIncrementMethod.invoke(values)).floatValue()
                );
            } catch (ReflectiveOperationException ignored) {
                return null;
            }
        }
        if (stack.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) stack.getItem();
            int hunger = food.getHealAmount(stack);
            return new FoodPreview(hunger, hunger * food.getSaturationModifier(stack) * 2.0F);
        }
        return null;
    }

    private static boolean isAppleSkinFoodPreviewEnabled()
    {
        prepareAppleSkinReflection();
        return getAppleSkinBoolean(appleSkinFoodValuesOverlayField);
    }

    private static boolean isAppleSkinSaturationOverlayEnabled()
    {
        prepareAppleSkinReflection();
        return getAppleSkinBoolean(appleSkinSaturationOverlayField);
    }

    private static boolean isAppleCoreLoaded()
    {
        if (appleSkinHasAppleCoreField == null) {
            return false;
        }
        try {
            return appleSkinHasAppleCoreField.getBoolean(null);
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    private static boolean getAppleSkinBoolean(Field field)
    {
        if (field == null) {
            return false;
        }
        try {
            return field.getBoolean(null);
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    private static void prepareAppleSkinReflection()
    {
        if (appleSkinReflectionChecked) {
            return;
        }
        appleSkinReflectionChecked = true;
        if (!Loader.isModLoaded("appleskin")) {
            return;
        }
        try {
            Class<?> config = Class.forName("squeek.appleskin.ModConfig");
            appleSkinFoodValuesOverlayField = config.getField("SHOW_FOOD_VALUES_OVERLAY");
            appleSkinSaturationOverlayField = config.getField("SHOW_SATURATION_OVERLAY");

            Class<?> foodHelper = Class.forName("squeek.appleskin.helpers.FoodHelper");
            Class<?> foodValues = Class.forName("squeek.appleskin.helpers.FoodHelper$BasicFoodValues");
            appleSkinIsFoodMethod = foodHelper.getMethod("isFood", ItemStack.class);
            appleSkinModifiedFoodValuesMethod = foodHelper.getMethod("getModifiedFoodValues", ItemStack.class, EntityPlayer.class);
            appleSkinFoodValueHungerField = foodValues.getField("hunger");
            appleSkinSaturationIncrementMethod = foodValues.getMethod("getSaturationIncrement");

            Class<?> appleSkin = Class.forName("squeek.appleskin.AppleSkin");
            appleSkinHasAppleCoreField = appleSkin.getField("hasAppleCore");
            Class<?> appleCoreHelper = Class.forName("squeek.appleskin.helpers.AppleCoreHelper");
            appleCoreFoodValuesForDisplayMethod =
                appleCoreHelper.getMethod("getFoodValuesForDisplay", foodValues, EntityPlayer.class);
        } catch (ReflectiveOperationException ignored) {
            appleSkinFoodValuesOverlayField = null;
            appleSkinSaturationOverlayField = null;
            appleSkinIsFoodMethod = null;
            appleSkinModifiedFoodValuesMethod = null;
            appleSkinHasAppleCoreField = null;
            appleCoreFoodValuesForDisplayMethod = null;
            appleSkinFoodValueHungerField = null;
            appleSkinSaturationIncrementMethod = null;
        }
    }

    private static void enablePreviewAlpha()
    {
        GlStateManager.enableBlend();
        if (previewAlpha != 1.0F) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, previewAlpha);
            GlStateManager.blendFunc(770, 771);
        }
    }

    private static void disablePreviewAlpha()
    {
        if (previewAlpha != 1.0F) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void resetPreviewAlpha()
    {
        previewAlpha = 0.0F;
        previewAlphaDirection = 1;
    }

    private static int clamp(int value, int min, int max)
    {
        return Math.max(min, Math.min(max, value));
    }

    private static int halfFoodSaturationU(float fill)
    {
        if (fill >= 1.0F) {
            return SATURATION_FULL_U;
        }
        if (fill > 0.5F) {
            return SATURATION_THREE_QUARTERS_U;
        }
        if (fill > 0.25F) {
            return SATURATION_HALF_U;
        }
        return SATURATION_QUARTER_U;
    }

    private static int saturationU(float fill)
    {
        if (fill >= 1.0F) {
            return SATURATION_FULL_U;
        }
        if (fill > 0.5F) {
            return SATURATION_THREE_QUARTERS_U;
        }
        if (fill > 0.25F) {
            return SATURATION_HALF_U;
        }
        return SATURATION_QUARTER_U;
    }

    private static void drawIcon(int x, int y, int u)
    {
        drawModalRectWithCustomSizedTexture(x, y, u, 0, ICON_SIZE, ICON_SIZE, 256.0F, 256.0F);
    }

    private static final class FoodPreview
    {
        private final int hunger;
        private final float saturationIncrement;

        private FoodPreview(int hunger, float saturationIncrement)
        {
            this.hunger = hunger;
            this.saturationIncrement = saturationIncrement;
        }
    }
}
