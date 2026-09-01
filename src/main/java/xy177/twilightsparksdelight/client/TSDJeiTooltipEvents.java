package xy177.twilightsparksdelight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.twilightsparksdelight.TwilightSparksDelight;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import xy177.twilightsparksdelight.common.registry.TSDItems;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID, value = Side.CLIENT)
public final class TSDJeiTooltipEvents
{
    private static final String RECIPES_GUI = "mezz.jei.gui.recipes.RecipesGui";
    private static final String HUNTING_WRAPPER = "com.wdcftgg.farmersdelightlegacy.client.jei.HuntingDropJeiRecipe";
    private static final String RABBIT_WATCH_RECIPE = TwilightSparksDelight.MODID + ":rabbit_pocket_watch_hunting";
    private static final Set<String> POCKET_WATCH_KEEP_RECIPES = new HashSet<>(Arrays.asList(
        TwilightSparksDelight.MODID + ":eat_me_bulk",
        TwilightSparksDelight.MODID + ":trail_rations_watch",
        TwilightSparksDelight.MODID + ":pickled_bracken_jar_watch"
    ));
    private static Field recipeLayoutsField;
    private static Field recipeWrapperField;
    private static Method getPosXMethod;
    private static Method getPosYMethod;
    private static Method getRecipeIdMethod;

    private TSDJeiTooltipEvents()
    {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null || !RECIPES_GUI.equals(gui.getClass().getName())) {
            return;
        }
        String hintKey = getJeiHintKey(stack);
        if (hintKey != null) {
            addTooltipOnce(event.getToolTip(), I18n.format(hintKey));
            return;
        }
        if (stack.getItem() != TSDItems.RABBIT_POCKET_WATCH) {
            return;
        }
        if (isCurrentRecipeOneOf(gui, POCKET_WATCH_KEEP_RECIPES)) {
            addTooltipOnce(event.getToolTip(), I18n.format("twilight_spark_delight.jei.not_consumed"));
        }
    }

    private static void addTooltipOnce(List<String> tooltip, String line)
    {
        if (!tooltip.contains(line)) {
            tooltip.add(line);
        }
    }

    private static String getJeiHintKey(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("TsdJeiHint", 8)) {
            return null;
        }
        return tag.getString("TsdJeiHint");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        GuiScreen gui = event.getGui();
        if (gui == null || !RECIPES_GUI.equals(gui.getClass().getName())) {
            return;
        }

        if (!isMouseOverRabbitWatchEntity(gui, event.getMouseX(), event.getMouseY())) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.fontRenderer == null) {
            return;
        }

        GuiUtils.drawHoveringText(
            Arrays.asList(
                I18n.format("entity.KillerBunny.name"),
                I18n.format("twilight_spark_delight.jei.transformation_powder_chance"),
                TextFormatting.BLUE.toString() + TextFormatting.ITALIC + "Minecraft"
            ),
            event.getMouseX(),
            event.getMouseY(),
            gui.width,
            gui.height,
            -1,
            mc.fontRenderer
        );
    }

    private static boolean isMouseOverRabbitWatchEntity(GuiScreen gui, int mouseX, int mouseY)
    {
        try {
            ensureReflection(gui);
            @SuppressWarnings("unchecked")
            List<Object> layouts = (List<Object>) recipeLayoutsField.get(gui);
            for (Object layout : layouts) {
                Object wrapper = recipeWrapperField.get(layout);
                if (wrapper == null || !HUNTING_WRAPPER.equals(wrapper.getClass().getName())) {
                    continue;
                }
                String recipeId = (String) getRecipeIdMethod.invoke(wrapper);
                if (!RABBIT_WATCH_RECIPE.equals(recipeId)) {
                    continue;
                }
                int posX = (Integer) getPosXMethod.invoke(layout);
                int posY = (Integer) getPosYMethod.invoke(layout);
                if (mouseX >= posX + 19 && mouseX < posX + 52 && mouseY >= posY + 8 && mouseY < posY + 57) {
                    return true;
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return false;
    }

    private static boolean isCurrentRecipeOneOf(GuiScreen gui, Set<String> recipeIds)
    {
        try {
            ensureReflection(gui);
            @SuppressWarnings("unchecked")
            List<Object> layouts = (List<Object>) recipeLayoutsField.get(gui);
            for (Object layout : layouts) {
                Object wrapper = recipeWrapperField.get(layout);
                String recipeId = getRecipeId(wrapper);
                if (recipeId != null && recipeIds.contains(recipeId)) {
                    return true;
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return false;
    }

    private static String getRecipeId(Object wrapper) throws ReflectiveOperationException
    {
        if (wrapper == null) {
            return null;
        }
        String recipeId = invokeStringGetter(wrapper, "getRecipeId");
        if (recipeId != null) {
            return recipeId;
        }
        ResourceLocation registryName = getRegistryName(wrapper);
        return registryName == null ? null : registryName.toString();
    }

    private static String invokeStringGetter(Object target, String methodName) throws ReflectiveOperationException
    {
        Method method;
        try {
            method = target.getClass().getMethod(methodName);
        } catch (NoSuchMethodException ignored) {
            try {
                method = target.getClass().getDeclaredMethod(methodName);
                method.setAccessible(true);
            } catch (NoSuchMethodException ignoredAgain) {
                return null;
            }
        }
        Object value = method.invoke(target);
        return value instanceof String ? (String) value : null;
    }

    private static ResourceLocation getRegistryName(Object wrapper) throws ReflectiveOperationException
    {
        Method method;
        try {
            method = wrapper.getClass().getMethod("getRegistryName");
        } catch (NoSuchMethodException ignored) {
            try {
                method = wrapper.getClass().getDeclaredMethod("getRegistryName");
                method.setAccessible(true);
            } catch (NoSuchMethodException ignoredAgain) {
                return null;
            }
        }
        Object value = method.invoke(wrapper);
        return value instanceof ResourceLocation ? (ResourceLocation) value : null;
    }

    private static void ensureReflection(GuiScreen gui) throws ReflectiveOperationException
    {
        if (recipeLayoutsField == null) {
            recipeLayoutsField = gui.getClass().getDeclaredField("recipeLayouts");
            recipeLayoutsField.setAccessible(true);
        }
        if (recipeWrapperField == null || getPosXMethod == null || getPosYMethod == null) {
            Class<?> recipeLayoutClass = Class.forName("mezz.jei.gui.recipes.RecipeLayout");
            recipeWrapperField = recipeLayoutClass.getDeclaredField("recipeWrapper");
            recipeWrapperField.setAccessible(true);
            getPosXMethod = recipeLayoutClass.getMethod("getPosX");
            getPosYMethod = recipeLayoutClass.getMethod("getPosY");
        }
        if (getRecipeIdMethod == null) {
            getRecipeIdMethod = Class.forName(HUNTING_WRAPPER).getMethod("getRecipeId");
        }
    }
}
