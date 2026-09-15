package xy177.twilightsparksdelight.integration.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import twilightforest.entity.boss.KnightPhantom;
import twilightforest.init.TFItems;
import vectorwing.farmersdelight.common.registry.ModItems;
import xy177.twilightsparksdelight.registry.TSDItems;

/** The modern dependency has no legacy HuntingDropRecipeApi or hunting page. */
final class TSDHuntingCategory extends TSDJeiCategory<TSDHuntingCategory.Drop> {
    record Drop(ResourceLocation entity, ItemStack output, boolean burning, boolean knifeOnly,
                boolean extraLegSlot, boolean transformedRabbit) {}

    private final IDrawable arrow;
    private final Map<Drop, LivingEntity> previews = new HashMap<>();
    private net.minecraft.client.multiplayer.ClientLevel previewLevel;

    TSDHuntingCategory(IGuiHelper helper) {
        super(helper, TwilightSparksDelightJeiPlugin.HUNTING, "twilight_spark_delight.jei.hunting.title",
                new ItemStack(ModItems.IRON_KNIFE.get()), 162, 76);
        arrow = helper.createDrawable(ResourceLocation.fromNamespaceAndPath("farmersdelight",
                "textures/gui/jei/cutting_board.png"), 47, 20, 24, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, Drop recipe, IFocusGroup focuses) {
        layout.addOutputSlot(112, 23).setStandardSlotBackground().addItemStack(recipe.output())
                .addRichTooltipCallback((slot, tooltip) -> tooltip.add(Component.translatable(
                        recipe.knifeOnly() ? "twilight_spark_delight.jei.hunting_tool_extra"
                                : "twilight_spark_delight.jei.any_kill")));
        if (recipe.extraLegSlot()) {
            layout.addOutputSlot(135, 23).setStandardSlotBackground().addItemStack(recipe.output())
                    .addRichTooltipCallback((slot, tooltip) -> tooltip.add(Component.translatable(
                            "twilight_spark_delight.jei.hunting_tool_extra")));
        }
        if (recipe.knifeOnly() || recipe.extraLegSlot()) {
            var knives = new ArrayList<ItemStack>();
            BuiltInRegistries.ITEM.forEach(item -> {
                var stack = item.getDefaultInstance();
                if (xy177.twilightsparksdelight.common.event.TSDLootEvents.isKnife(stack)) knives.add(stack);
            });
            layout.addInputSlot(74, 43).setSlotName("knife")
                    .setCustomRenderer(VanillaTypes.ITEM_STACK, new TSDSwingingKnifeRenderer())
                    .addItemStacks(knives);
        }
    }

    @Override
    public void draw(Drop recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 77, 24);
        var mc = Minecraft.getInstance();
        if (mc.level != previewLevel) {
            previews.clear();
            previewLevel = mc.level;
        }
        if (previewLevel != null) {
            LivingEntity entity = previews.computeIfAbsent(recipe, this::createPreview);
            if (entity != null) {
                entity.setRemainingFireTicks(recipe.burning() ? 200 : 0);
                int scale = (int) Math.min(36 / Math.max(0.5, entity.getBbHeight()),
                        46 / Math.max(0.5, entity.getBbWidth()));
                renderPreview(graphics, entity, scale);
            }
        }
        if (recipe.burning()) {
            centeredText(graphics, Component.translatable("twilight_spark_delight.jei.hunting.burning"),
                    64, 0x555555);
        }
    }

    private static void renderPreview(GuiGraphics graphics, LivingEntity entity, int scale) {
        // GUI scissor coordinates are absolute, unlike JEI's translated recipe pose.
        var matrix = graphics.pose().last().pose();
        var min = matrix.transformPosition(4, 0, 0, new org.joml.Vector3f());
        var max = matrix.transformPosition(66, 59, 0, new org.joml.Vector3f());
        graphics.enableScissor((int) min.x, (int) min.y, (int) max.x, (int) max.y);
        float oldBody = entity.yBodyRot;
        float oldYaw = entity.getYRot();
        float oldHead = entity.yHeadRot;
        float oldHeadPrevious = entity.yHeadRotO;
        try {
            entity.yBodyRot = 150;
            entity.setYRot(150);
            entity.yHeadRot = 150;
            entity.yHeadRotO = 150;
            var tilt = new org.joml.Quaternionf().rotationX(0.12F);
            var pose = new org.joml.Quaternionf().rotationZ((float) Math.PI).mul(tilt);
            InventoryScreen.renderEntityInInventory(graphics, 35, 30, scale / entity.getScale(),
                    new org.joml.Vector3f(0, entity.getBbHeight() / 2 + 0.06F * entity.getScale(), 0),
                    pose, tilt, entity);
        } finally {
            entity.yBodyRot = oldBody;
            entity.setYRot(oldYaw);
            entity.yHeadRot = oldHead;
            entity.yHeadRotO = oldHeadPrevious;
            graphics.disableScissor();
        }
    }

    @Override
    public void getTooltip(mezz.jei.api.gui.builder.ITooltipBuilder tooltip, Drop recipe,
                           IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (mouseX < 4 || mouseX >= 66 || mouseY < 0 || mouseY >= 59) return;
        LivingEntity entity = previews.get(recipe);
        if (entity == null) return;
        tooltip.add(recipe.transformedRabbit()
                ? Component.translatable("entity.minecraft.killer_bunny") : entity.getName());
        if (recipe.transformedRabbit()) {
            // EMI's JEI bridge ignores FormattedText lines unless they are Components.
            tooltip.addAll(Minecraft.getInstance().font.getSplitter().splitLines(Component.translatable(
                    "twilight_spark_delight.jei.transformation_powder_chance"), 100,
                    net.minecraft.network.chat.Style.EMPTY).stream()
                    .map(line -> Component.literal(line.getString())).toList());
        }
    }

    private LivingEntity createPreview(Drop recipe) {
        var type = BuiltInRegistries.ENTITY_TYPE.getOptional(recipe.entity()).orElse(null);
        if (type == null || !(type.create(previewLevel) instanceof LivingEntity entity)) return null;
        if (entity instanceof KnightPhantom phantom) {
            phantom.setNumber(0);
            phantom.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TFItems.PHANTOM_HELMET.get()));
            phantom.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TFItems.PHANTOM_CHESTPLATE.get()));
            phantom.switchToFormation(KnightPhantom.Formation.ATTACK_PLAYER_START);
        }
        if (entity instanceof Rabbit rabbit && recipe.transformedRabbit()) {
            rabbit.setVariant(Rabbit.Variant.EVIL);
            rabbit.setCustomName(null);
        }
        return entity;
    }

    static List<Drop> recipes() {
        var recipes = new ArrayList<Drop>();
        pair(recipes, "wild_boar", TSDItems.RAW_WILD_BOAR_MEAT.get(), TSDItems.COOKED_WILD_BOAR_MEAT.get(), false);
        pair(recipes, "bighorn_sheep", TSDItems.RAW_BIGHORN_MUTTON.get(), TSDItems.COOKED_BIGHORN_MUTTON.get(), false);
        pair(recipes, "helmet_crab", TSDItems.HERMIT_CRAB_LEG.get(), TSDItems.COOKED_HERMIT_CRAB_LEG.get(), true);
        pair(recipes, "fire_beetle", TSDItems.FIRE_BEETLE_LEG.get(), TSDItems.COOKED_FIRE_BEETLE_LEG.get(), true);
        pair(recipes, "slime_beetle", TSDItems.SLIME_BEETLE_LEG.get(), TSDItems.COOKED_SLIME_BEETLE_LEG.get(), true);
        pair(recipes, "pinch_beetle", TSDItems.PINCH_BEETLE_LEG.get(), TSDItems.COOKED_PINCH_BEETLE_LEG.get(), true);
        add(recipes, "fire_beetle", TSDItems.FIRE_BEETLE_FLAME_SAC.get(), true);
        add(recipes, "slime_beetle", TSDItems.SLIME_BEETLE_HONEY_GLAND.get(), true);
        add(recipes, "helmet_crab", TSDItems.HERMIT_CRAB.get(), true);
        add(recipes, "redcap", TSDItems.REDCAP_SPICE.get(), true);
        add(recipes, "redcap_sapper", TSDItems.REDCAP_SPICE.get(), true);
        add(recipes, "death_tome", TFItems.TRANSFORMATION_POWDER.get(), true);
        add(recipes, "snow_queen", TSDItems.GELID_CRYSTAL.get(), false);
        add(recipes, "minoshroom", TSDItems.LABYRINTH_MUSHROOM.get(), false);
        add(recipes, "knight_phantom", TSDItems.EXPERIMENT_PROTOTYPE.get(), false);
        recipes.add(new Drop(ResourceLocation.withDefaultNamespace("rabbit"),
                new ItemStack(TFItems.POCKET_WATCH.get()), false, true, false, true));
        return List.copyOf(recipes);
    }

    private static void pair(List<Drop> recipes, String entity, Item raw, Item cooked, boolean legs) {
        var id = ResourceLocation.fromNamespaceAndPath("twilightforest", entity);
        recipes.add(new Drop(id, new ItemStack(raw), false, false, legs, false));
        recipes.add(new Drop(id, new ItemStack(cooked), true, false, legs, false));
    }

    private static void add(List<Drop> recipes, String entity, Item output, boolean knife) {
        recipes.add(new Drop(ResourceLocation.fromNamespaceAndPath("twilightforest", entity),
                new ItemStack(output), false, knife, false, false));
    }
}
