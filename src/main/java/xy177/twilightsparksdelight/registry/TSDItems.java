package xy177.twilightsparksdelight.registry;

import net.minecraft.world.food.FoodProperties;
import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModEffects;
import twilightforest.init.TFMobEffects;
import xy177.twilightsparksdelight.common.item.MultiRandomCureFoodItem;
import xy177.twilightsparksdelight.common.item.TSDConsumableFoodItem;
import xy177.twilightsparksdelight.common.item.HelmetCrabItem;
import xy177.twilightsparksdelight.common.item.DoubleCrownIceCreamItem;
import xy177.twilightsparksdelight.common.item.PickledBrackenItem;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.ITEMS, TwilightSparksDelight.MOD_ID);

    public static final RegistryObject<Item> RABBIT_POCKET_WATCH = ITEMS.register("rabbit_pocket_watch",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final RegistryObject<Item> MASON_JAR = ITEMS.register("mason_jar",
            () -> new xy177.twilightsparksdelight.common.item.TSDMasonJarItem(TSDBlocks.MASON_JAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIVEROOT_FLOUR = basic("liveroot_cone");
    public static final RegistryObject<Item> LIVEROOT_PIE_CRUST = basic("liveroot_pie_crust");
    public static final RegistryObject<Item> LIVEROOT_DOUGH = foodWithEffects("liveroot_dough", 2, 2.4F, false, null,
            effect(MobEffects.BLINDNESS, 600, 0, 0.3F));
    public static final RegistryObject<Item> LIVEROOT_BREAD = food("liveroot_bread", 6, 5.0F, false, null);

    public static final RegistryObject<Item> RAW_WILD_BOAR_MEAT = food("raw_wild_boar_meat", 3, 1.6F, false, null);
    public static final RegistryObject<Item> COOKED_WILD_BOAR_MEAT = food("cooked_wild_boar_meat", 7, 7.2F, false, null);
    public static final RegistryObject<Item> RAW_WILD_BOAR_MEAT_CUBES = food("raw_wild_boar_meat_cubes", 2, 1.3F, false, null);
    public static final RegistryObject<Item> COOKED_WILD_BOAR_MEAT_CUBES = food("cooked_wild_boar_meat_cubes", 4, 4.1F, false, null);

    public static final RegistryObject<Item> RAW_BIGHORN_MUTTON = food("raw_bighorn_mutton", 2, 1.6F, false, null);
    public static final RegistryObject<Item> COOKED_BIGHORN_MUTTON = food("cooked_bighorn_mutton", 7, 7.2F, false, null);
    public static final RegistryObject<Item> RAW_BIGHORN_MUTTON_CHOP = food("raw_bighorn_mutton_chop", 2, 1.3F, false, null);
    public static final RegistryObject<Item> COOKED_BIGHORN_MUTTON_CHOP = food("cooked_bighorn_mutton_chop", 4, 4.1F, false, null);

    public static final RegistryObject<Item> HERMIT_CRAB_LEG = food("hermit_crab_leg", 4, 1.5F, false, null);
    public static final RegistryObject<Item> COOKED_HERMIT_CRAB_LEG = foodWithEffects("cooked_hermit_crab_leg", 6, 3.5F, false, null,
            effect(MobEffects.NIGHT_VISION, 3600, 0, 1.0F));
    public static final RegistryObject<Item> PINCH_BEETLE_LEG = food("pinch_beetle_leg", 4, 1.5F, false, null);
    public static final RegistryObject<Item> COOKED_PINCH_BEETLE_LEG = foodWithEffects("cooked_pinch_beetle_leg", 6, 3.5F, false, null,
            effect(MobEffects.NIGHT_VISION, 3600, 0, 1.0F));
    public static final RegistryObject<Item> SLIME_BEETLE_LEG = food("slime_beetle_leg", 4, 1.5F, false, null);
    public static final RegistryObject<Item> COOKED_SLIME_BEETLE_LEG = foodWithEffects("cooked_slime_beetle_leg", 6, 3.5F, false, null,
            effect(MobEffects.NIGHT_VISION, 3600, 0, 1.0F));
    public static final RegistryObject<Item> FIRE_BEETLE_LEG = food("fire_beetle_leg", 4, 1.5F, false, null);
    public static final RegistryObject<Item> COOKED_FIRE_BEETLE_LEG = foodWithEffects("cooked_fire_beetle_leg", 6, 3.5F, false, null,
            effect(MobEffects.NIGHT_VISION, 3600, 0, 1.0F));

    public static final RegistryObject<Item> FIRE_BEETLE_FLAME_SAC = foodWithEffects("fire_beetle_flame_sac", 5, 3.0F, false, null,
            effect(MobEffects.FIRE_RESISTANCE, 3600, 0, 1.0F));
    public static final RegistryObject<Item> SLIME_BEETLE_HONEY_GLAND = foodWithEffects("slime_beetle_honey_gland", 5, 3.0F, false, null,
            effect(MobEffects.REGENERATION, 300, 0, 1.0F));
    public static final RegistryObject<Item> HERMIT_CRAB = ITEMS.register("hermit_crab",
            () -> new HelmetCrabItem(new Item.Properties()));
    public static final RegistryObject<Item> QUEST_RAM_MILK = drinkCureFood("quest_ram_milk", 0, 0.0F, Items.GLASS_BOTTLE, 1, true);
    public static final RegistryObject<Item> QUEST_RAM_CHEESE = cureFood("quest_ram_cheese", 6, 9.0F, Items.GLASS_BOTTLE, 1, false);

    public static final RegistryObject<Item> TWILIGHT_SKEWER = food("twilight_skewer", 12, 11.0F, false, null);
    public static final RegistryObject<Item> LABYRINTH_FLAVOR_SKEWER = foodWithEffects("labyrinth_flavor_skewer", 14, 12.0F, false, null,
            effect(TSDEffects.CHARGE, 3600, 0, 1.0F),
            effect(MobEffects.FIRE_RESISTANCE, 5400, 0, 1.0F));
    public static final RegistryObject<Item> LABYRINTH_SASHIMI_MEDLEY = foodWithEffects("labyrinth_sashimi_medley", 16, 10.0F, false, Items.BOWL,
            effect(MobEffects.NIGHT_VISION, 14400, 0, 1.0F),
            effect(ModEffects.NOURISHMENT, 3600, 0, 1.0F));
    public static final RegistryObject<Item> LABYRINTH_A5_DOUBLE_CHEESEBURGER =
            cureFood("labyrinth_a5_mushroom_flavor_double_cheeseburger", 12, 18.6F, null, 3, false,
                    effect(TSDEffects.CHARGE, 5400, 0, 1.0F));
    public static final RegistryObject<Item> TORCHBERRY_SAUCE = food("torchberry_sauce", 3, 2.2F, false, Items.BOWL);
    public static final RegistryObject<Item> LABYRINTH_TACO = foodWithEffects("labyrinth_taco", 12, 14.4F, false, Items.BOWL,
            effect(TSDEffects.CHARGE, 3600, 0, 1.0F));
    public static final RegistryObject<Item> GELID_CRYSTAL = foodWithEffects("gelid_crystal", 1, 0.0F, false, null,
            effect(TFMobEffects.FROSTY, 3600, 0, 1.0F));
    public static final RegistryObject<Item> LABYRINTH_MUSHROOM = foodWithEffects("labyrinth_mushroom", 6, 7.2F, false, null,
            effect(MobEffects.CONFUSION, 60, 0, 1.0F),
            effect(TSDEffects.CHARGE, 1200, 0, 1.0F));

    public static final RegistryObject<Item> EXPERIMENT_PROTOTYPE = basic("experiment_prototype");
    public static final RegistryObject<Item> EXPERIMENT_000 = foodWithEffects("experiment_000", 3, 1.8F, false, null,
            effect(TSDEffects.SYMBIOSIS, 1200, 0, 1.0F));
    public static final RegistryObject<Item> EXPERIMENT_234 = foodWithEffects("experiment_234", 5, 0.0F, false, null,
            effect(MobEffects.HUNGER, 1200, 0, 1.0F),
            effect(MobEffects.CONFUSION, 1200, 0, 1.0F),
            effect(TSDEffects.SORROW, 3600, 0, 1.0F));
    public static final RegistryObject<Item> ABYSS_PIE_SLICE = foodWithEffects("abyss_pie_slice", 8, 10.0F, false, null,
            effect(TSDEffects.SYMBIOSIS, 6000, 2, 1.0F),
            effect(TSDEffects.ABYSS_CALL, 1800, 0, 1.0F),
            effect(MobEffects.NIGHT_VISION, 3600, 0, 1.0F),
            effect(TSDEffects.SORROW, 3600, 0, 1.0F));
    public static final RegistryObject<Experiment250Item> EXPERIMENT_250 =
            ITEMS.register("experiment_250", () -> new Experiment250Item(new Item.Properties()));
    public static final RegistryObject<Item> FIERY_SLAG = basic("fiery_slag");
    public static final RegistryObject<Item> GRIDDLE_TENTACLE = foodWithEffects("griddle_tentacle", 10, 12.5F, false, null,
            effect(TSDEffects.SYMBIOSIS, 2400, 0, 1.0F));
    public static final RegistryObject<Item> SCOURGE_STEAK = foodWithEffects("scourge_steak", 18, 16.0F, false, Items.BOWL,
            effect(ModEffects.COMFORT, 3600, 0, 1.0F));
    public static final RegistryObject<Item> REDCAP_SPICE = basic("redcap_spice");
    public static final RegistryObject<Item> DRINK_ME = drinkWithEffects("drink_me", 4, 2.0F, false, Items.GLASS_BOTTLE,
            displayEffect(TSDEffects.SHRINK, 3600, 0),
            effect(MobEffects.REGENERATION, 600, 0, 1.0F));
    public static final RegistryObject<Item> EAT_ME = foodWithEffects("eat_me", 5, 3.3F, false, null,
            displayEffect(TSDEffects.ENLARGE, 3600, 0));
    public static final RegistryObject<Item> MINO_MINCE = food("mino_mince", 2, 1.2F, false, null);
    public static final RegistryObject<Item> MINO_PATTY = foodWithEffects("mino_patty", 6, 6.2F, false, null,
            effect(TSDEffects.CHARGE, 600, 0, 1.0F));
    public static final RegistryObject<Item> TRAIL_RATIONS = foodWithEffects("trail_rations", 12, 15.0F, false, null, 15,
            effect(TSDEffects.CHARGE, 2400, 0, 1.0F));
    public static final RegistryObject<Item> BRACKEN = food("bracken", 3, 3.6F, false, null);
    public static final RegistryObject<Item> PICKLED_BRACKEN = ITEMS.register("pickled_bracken",
            () -> new PickledBrackenItem(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(7).saturationMod(0.0F).build())));

    public static final RegistryObject<Item> BOWL_OF_TWILIGHT_BORSCHT = foodWithEffects("bowl_of_twilight_borscht", 16, 23.0F, false, Items.BOWL,
            effect(ModEffects.COMFORT, 7800, 0, 1.0F), effect(ModEffects.NOURISHMENT, 4800, 0, 1.0F), effect(TSDEffects.CHARGE, 5400, 0, 1.0F));
    public static final RegistryObject<Item> TWILIGHT_BORSCHT_CUP = foodWithEffects("twilight_borscht_cup", 8, 11.5F, false, null,
            effect(ModEffects.COMFORT, 5100, 0, 1.0F), effect(ModEffects.NOURISHMENT, 3300, 0, 1.0F), effect(TSDEffects.CHARGE, 3600, 0, 1.0F));
    public static final RegistryObject<Item> PLATE_OF_TWILIGHT_BOAR_KNUCKLE = foodWithEffects("plate_of_twilight_boar_knuckle", 18, 23.0F, false, null,
            effect(ModEffects.NOURISHMENT, 6000, 0, 1.0F), effect(TSDEffects.CHARGE, 6000, 1, 1.0F));
    public static final RegistryObject<Item> CREAM_OF_LABYRINTH_MUSHROOM_SOUP =
            cureFood("cream_of_labyrinth_mushroom_soup", 14, 21.0F, Items.BOWL, 3, false,
                    effect(ModEffects.COMFORT, 6000, 0, 1.0F), effect(TSDEffects.CHARGE, 2400, 0, 1.0F));
    public static final RegistryObject<Item> CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP =
            cureFood("cream_of_labyrinth_mushroom_soup_cup", 7, 10.5F, null, 3, false,
                    effect(ModEffects.COMFORT, 3900, 0, 1.0F), effect(TSDEffects.CHARGE, 1500, 0, 1.0F));
    public static final RegistryObject<Item> BOWL_OF_CHICKEN_AND_HYDRA_SOUP =
            foodWithEffects("bowl_of_chicken_and_hydra_soup", 15, 23.0F, false, Items.BOWL,
                    effect(ModEffects.COMFORT, 6000, 0, 1.0F), effect(MobEffects.MOVEMENT_SPEED, 3600, 1, 1.0F), effect(MobEffects.DAMAGE_BOOST, 1800, 1, 1.0F));
    public static final RegistryObject<Item> CHICKEN_AND_HYDRA_SOUP_CUP =
            foodWithEffects("chicken_and_hydra_soup_cup", 8, 11.0F, false, null,
                    effect(ModEffects.COMFORT, 3900, 0, 1.0F), effect(MobEffects.MOVEMENT_SPEED, 2400, 1, 1.0F), effect(MobEffects.DAMAGE_BOOST, 1200, 1, 1.0F));
    public static final RegistryObject<Item> STIR_FRIED_BRACKEN = foodWithEffects("stir_fried_bracken", 17, 19.0F, false, Items.BOWL,
            effect(MobEffects.FIRE_RESISTANCE, 9600, 0, 1.0F));
    public static final RegistryObject<Item> HELMET_CRAB_LEG_SUSHI_ROLL = foodWithEffects("helmet_crab_leg_sushi_roll", 12, 14.4F, false, null,
            effect(MobEffects.DIG_SPEED, 5400, 1, 1.0F));
    public static final RegistryObject<Item> HELMET_CRAB_LEG_SUSHI = foodWithEffects("helmet_crab_leg_sushi", 6, 6.0F, false, null,
            effect(MobEffects.DIG_SPEED, 1800, 2, 1.0F));
    public static final RegistryObject<Item> TENTACLE_CHOW_MEIN = foodWithEffects("tentacle_chow_mein", 16, 15.2F, false, Items.BOWL,
            effect(ModEffects.NOURISHMENT, 6000, 0, 1.0F), effect(TSDEffects.SYMBIOSIS, 6000, 1, 1.0F));
    public static final RegistryObject<Item> MILLION_POUND_MEAL = foodWithEffects("million_pound_meal", 14, 19.0F, false, Items.BOWL,
            effect(ModEffects.NOURISHMENT, 3600, 0, 1.0F), effect(TSDEffects.CHARGE, 3600, 0, 1.0F));
    public static final RegistryObject<Item> TWILIGHT_CHEESE_FONDUE_COMPANION = ITEMS.register(
            "twilight_cheese_fondue_companion",
            () -> new TwilightCheeseFondueCompanionItem(new Item.Properties()));
    public static final RegistryObject<Item> TWILIGHT_CHEESE_FONDUE_WITH_BREAD =
            cureFood("twilight_cheese_fondue_with_bread", 16, 21.0F, null, 3, false,
                    effect(ModEffects.NOURISHMENT, 6000, 0, 1.0F), effect(TSDEffects.CHARGE, 5400, 0, 1.0F), effect(MobEffects.REGENERATION, 600, 0, 1.0F));
    public static final RegistryObject<Item> TWILIGHT_SUPREME_SANDWICH = food("twilight_supreme_sandwich", 15, 16.0F, false, null);
    public static final RegistryObject<Item> DOUBLE_CROWN_ICE_CREAM = ITEMS.register(
            "double_crown_ice_cream", DoubleCrownIceCreamItem::new);
    public static final RegistryObject<Item> TWIN_RADIANCE_ICE_POP = foodWithEffects("twin_radiance_ice_pop", 5, 3.2F, true, null,
            effect(MobEffects.DAMAGE_RESISTANCE, 3600, 0, 1.0F), effect(TFMobEffects.FROSTY, 3600, 0, 1.0F));
    public static final RegistryObject<Item> SALT_ROASTED_HELMET_CRAB_CLAW =
            foodWithEffects("salt_roasted_helmet_crab_claw", 12, 16.6F, false, null,
                    effect(ModEffects.NOURISHMENT, 3600, 0, 1.0F), effect(MobEffects.DIG_SPEED, 5400, 1, 1.0F));
    public static final RegistryObject<Item> BOWL_OF_SALTED_CRAB_MEAT = foodWithEffects("bowl_of_salted_crab_meat", 16, 21.0F, false, Items.BOWL,
            effect(ModEffects.NOURISHMENT, 6000, 0, 1.0F), effect(MobEffects.DAMAGE_BOOST, 5400, 0, 1.0F));
    public static final RegistryObject<Item> BOWL_OF_NAGA_MIXED_RICE = foodWithEffects("bowl_of_naga_mixed_rice", 30, 30.0F, false, Items.BOWL,
            effect(ModEffects.NOURISHMENT, 5400, 0, 1.0F), effect(TSDEffects.CHARGE, 6000, 1, 1.0F),
            effect(ModEffects.COMFORT, 6000, 0, 1.0F), effect(MobEffects.DAMAGE_RESISTANCE, 2400, 1, 1.0F),
            effect(MobEffects.MOVEMENT_SPEED, 2400, 2, 1.0F));
    public static final RegistryObject<Item> NAGA_MIXED_RICE_CUP = foodWithEffects("naga_mixed_rice_cup", 15, 15.0F, false, null,
            effect(ModEffects.NOURISHMENT, 3600, 0, 1.0F), effect(TSDEffects.CHARGE, 3900, 1, 1.0F),
            effect(ModEffects.COMFORT, 3900, 0, 1.0F), effect(MobEffects.DAMAGE_RESISTANCE, 1500, 1, 1.0F),
            effect(MobEffects.MOVEMENT_SPEED, 1500, 2, 1.0F));

    private TSDItems() {
    }

    private static RegistryObject<Item> basic(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> food(String id, int nutrition, float saturationPoints,
                                           boolean alwaysEdible, Item container) {
        return food(id, nutrition, saturationPoints, alwaysEdible, container, 1.6F);
    }

    private static RegistryObject<Item> food(String id, int nutrition, float saturationPoints,
                                           boolean alwaysEdible, Item container, float eatSeconds) {
        return foodWithEffects(id, nutrition, saturationPoints, alwaysEdible, container);
    }

    @SafeVarargs
    private static RegistryObject<Item> foodWithEffects(String id, int nutrition, float saturationPoints,
                                                      boolean alwaysEdible, Item container, EffectSpec... effects) {
        return foodWithEffects(id, nutrition, saturationPoints, alwaysEdible, container, UseAnim.EAT, 32, effects);
    }

    @SafeVarargs
    private static RegistryObject<Item> foodWithEffects(String id, int nutrition, float saturationPoints,
                                                      boolean alwaysEdible, Item container, int useDuration,
                                                      EffectSpec... effects) {
        return foodWithEffects(id, nutrition, saturationPoints, alwaysEdible, container,
                UseAnim.EAT, useDuration, effects);
    }

    @SafeVarargs
    private static RegistryObject<Item> foodWithEffects(String id, int nutrition, float saturationPoints,
                                                      boolean alwaysEdible, Item container, UseAnim useAnimation,
                                                      int useDuration, EffectSpec... effects) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationMod(nutrition == 0 ? 0.0F : saturationPoints / (nutrition * 2.0F));
        if (alwaysEdible) {
            builder.alwaysEat();
        }
        for (EffectSpec effect : effects) {
            if (effect.displayOnly()) {
                continue;
            }
            // Farmer's Delight food tooltips still list these effects, but the
            // effect instances themselves should not create a second particle cloud.
            builder.effect(() -> new MobEffectInstance(
                    effect.effect().get(), effect.duration(), effect.amplifier(), false, false, true), effect.chance());
        }
        return ITEMS.register(id, () -> {
            Item.Properties properties = new Item.Properties().food(builder.build());
            if (container != null) {
                properties.craftRemainder(container);
            }
            TSDConsumableFoodItem item = new TSDConsumableFoodItem(properties, useAnimation, useDuration);
            for (EffectSpec effect : effects) {
                if (effect.displayOnly()) {
                    item.addDisplayEffect(() -> new MobEffectInstance(
                            effect.effect().get(), effect.duration(), effect.amplifier(), false, false, true));
                }
            }
            return item;
        });
    }

    @SafeVarargs
    private static RegistryObject<Item> drinkWithEffects(String id, int nutrition, float saturationPoints,
                                                       boolean alwaysEdible, Item container, EffectSpec... effects) {
        return foodWithEffects(id, nutrition, saturationPoints, alwaysEdible, container,
                UseAnim.DRINK, 32, effects);
    }

    @SafeVarargs
    private static RegistryObject<Item> cureFood(String id, int nutrition, float saturationPoints, Item container,
                                               int maximumCures, boolean alwaysEdible, EffectSpec... effects) {
        return cureFood(id, nutrition, saturationPoints, container, maximumCures, alwaysEdible,
                UseAnim.EAT, 32, effects);
    }

    @SafeVarargs
    private static RegistryObject<Item> drinkCureFood(String id, int nutrition, float saturationPoints, Item container,
                                                    int maximumCures, boolean alwaysEdible, EffectSpec... effects) {
        return cureFood(id, nutrition, saturationPoints, container, maximumCures, alwaysEdible,
                UseAnim.DRINK, 32, effects);
    }

    @SafeVarargs
    private static RegistryObject<Item> cureFood(String id, int nutrition, float saturationPoints, Item container,
                                               int maximumCures, boolean alwaysEdible, UseAnim useAnimation,
                                               int useDuration, EffectSpec... effects) {
        return ITEMS.register(id, () -> {
            FoodProperties.Builder builder = new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationMod(nutrition == 0 ? 0.0F : saturationPoints / (nutrition * 2.0F));
            if (alwaysEdible) {
                builder.alwaysEat();
            }
            for (EffectSpec effect : effects) {
                builder.effect(() -> new MobEffectInstance(
                        effect.effect().get(), effect.duration(), effect.amplifier(), false, false, true), effect.chance());
            }
            Item.Properties properties = new Item.Properties().food(builder.build());
            if (container != null) {
                properties.craftRemainder(container);
            }
            if (id.equals("quest_ram_milk")) {
                properties.stacksTo(16);
            }
            return new MultiRandomCureFoodItem(properties, maximumCures,
                    useAnimation, useDuration);
        });
    }

    private static EffectSpec effect(Supplier<MobEffect> effect, int duration, int amplifier, float chance) {
        return new EffectSpec(effect, duration, amplifier, chance, false);
    }

    private static EffectSpec effect(MobEffect effect, int duration, int amplifier, float chance) {
        return effect(() -> effect, duration, amplifier, chance);
    }

    private static EffectSpec displayEffect(Supplier<MobEffect> effect, int duration, int amplifier) {
        return new EffectSpec(effect, duration, amplifier, 1.0F, true);
    }

    private record EffectSpec(Supplier<MobEffect> effect, int duration, int amplifier, float chance,
                              boolean displayOnly) {
    }
}
