package xy177.twilightsparksdelight.common.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.util.GloryCrucibleCapacity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDConfig
{
    private static final int EXPERIMENT_250_MEAT_DEFAULTS_VERSION = 2;
    private static final Set<ResourceLocation> RANDOM_CURE_BLACKLIST = new HashSet<>();
    private static final Map<ResourceLocation, Double> EXPERIMENT_250_MEAT_ACTIVITY_COSTS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<ResourceLocation>> EXPERIMENT_250_ENTITY_MEATS = new LinkedHashMap<>();
    private static final Set<ResourceLocation> EXPERIMENT_250_AUTO_GENERATED_MEATS = new HashSet<>();
    private static final Set<ResourceLocation> EXPERIMENT_250_NON_RAW_MEATS = new HashSet<>();
    private static final Set<ResourceLocation> EXPERIMENT_250_NON_CONSUMING_ATTACKERS = new HashSet<>();
    private static final String[] DEFAULT_EXPERIMENT_250_MEAT_ACTIVITY_COSTS = {
        "minecraft:porkchop:10",
        "minecraft:beef:10",
        "minecraft:chicken:4",
        "minecraft:mutton:8",
        "minecraft:rabbit:3",
        "minecraft:rabbit_foot:15",
        "farmersdelight:ham:50",
        "twilightforest:raw_venison:10",
        "twilightforest:raw_meef:30",
        "twilight_spark_delight:raw_wild_boar_meat:10",
        "twilight_spark_delight:raw_bighorn_mutton:8",
        "twilight_spark_delight:hermit_crab_leg:13",
        "twilight_spark_delight:fire_beetle_leg:25",
        "twilight_spark_delight:slime_beetle_leg:25",
        "twilight_spark_delight:pinch_beetle_leg:40"
    };
    private static final String[] DEFAULT_EXPERIMENT_250_ENTITY_MEATS = {
        "minecraft:pig=minecraft:porkchop",
        "minecraft:cow=minecraft:beef",
        "minecraft:chicken=minecraft:chicken",
        "minecraft:sheep=minecraft:mutton",
        "minecraft:rabbit=minecraft:rabbit",
        "minecraft:spider=minecraft:spider_eye",
        "minecraft:zombie=minecraft:rotten_flesh",
        "twilightforest:wild_boar=twilight_spark_delight:raw_wild_boar_meat",
        "twilightforest:bighorn_sheep=twilight_spark_delight:raw_bighorn_mutton",
        "twilightforest:deer=twilightforest:raw_venison",
        "twilightforest:helmet_crab=twilight_spark_delight:hermit_crab_leg",
        "twilightforest:fire_beetle=twilight_spark_delight:fire_beetle_leg",
        "twilightforest:slime_beetle=twilight_spark_delight:slime_beetle_leg",
        "twilightforest:pinch_beetle=twilight_spark_delight:pinch_beetle_leg",
        "twilightforest:minotaur=twilightforest:raw_meef",
        "twilightforest:minoshroom=twilightforest:raw_meef",
        "twilightforest:hydra=twilightforest:hydra_chop"
    };
    private static final String[] EXPERIMENT_250_MEAT_BINDINGS_ADDED_IN_VERSION_1 = {
        "minecraft:spider=minecraft:spider_eye",
        "minecraft:zombie=minecraft:rotten_flesh",
        "twilightforest:hydra=twilightforest:hydra_chop"
    };
    private static final String[] EXPERIMENT_250_MEAT_COSTS_ADDED_IN_VERSION_2 = {
        "minecraft:rabbit_foot:15",
        "farmersdelight:ham:50"
    };
    private static boolean experiment250CostsResolvedWithWorld;

    public static float hermitCrabAnvilBonusLegChance = 0.75F;
    public static float hermitCrabAnvilArmorShardClusterChance = 0.25F;
    public static float experimentProliferationChance = 0.30F;
    public static double chargeSpeedBonus = 0.20D;
    public static double chargeStepHeightBonus = 5.0D;
    public static double chargeMaxAttackBonus = 0.50D;
    public static double symbiosisHungerDrainPerTick = 0.0025D;
    public static double symbiosisPlayerDamageReductionBase = 0.10D;
    public static double symbiosisPlayerDamageReductionPerAmplifier = 0.10D;
    public static int symbiosisHealIntervalTicks = 100;
    public static float symbiosisEntityHealAmount = 1.0F;
    public static double shrinkBaseReduction = 0.40D;
    public static double shrinkReductionPerLevel = 0.10D;
    public static double enlargeBaseScale = 1.00D;
    public static double enlargeScalePerLevel = 1.00D;
    public static int twilightCheeseFondueAdvancementDinerCount = 3;
    public static float doubleCrownIceCreamNoFrostedChance = 0.10F;
    public static float doubleCrownIceCreamLongFrostedChance = 0.10F;
    public static boolean extendedFoodStatsEnabled = true;
    public static boolean extendedFoodKeepOnRespawn = true;
    public static boolean extendedFoodProgressiveCapsEnabled = true;
    public static boolean extendedFoodExtraConsumptionAndBenefitsEnabled = true;
    public static double extendedFoodExtraConsumptionMultiplier = 0.50D;
    public static double extendedFoodExtraBenefitMultiplier = 0.25D;
    public static float rabbitPocketWatchKillerRabbitChance = 0.10F;
    public static float armoredGiantSkilletVariantChance = 0.20F;
    public static float armoredGiantKitchenSetDropChance = 0.25F;
    public static float pickledBrackenJarBaseRipeningChance = 0.50F;
    public static double experiment250ActivityDiscountPerLevel = 0.06D;
    public static String experiment250WorkstationMode = "cutting_board";
    public static boolean experiment250BindingModeEnabled = false;
    public static double experiment250FatalProtectionActivityCost = 20000.0D;
    public static int experiment250FatalProtectionTriggersPerSleep = 3;
    public static boolean experiment250FatalProtectionAllowsBossAttacks = true;
    public static boolean experiment250FatalProtectionBossAttacksConsumeTrigger = true;
    public static String experiment250FatalProtectionItemLocation = "inventory";
    public static int gloryCrucibleBrewingTimeTicks = 200;
    public static int gloryCrucibleCapacityMb = 2000;
    public static int giantCookingPotBatchCount = 8;
    public static int giantsStoveBatchCount = 16;
    public static int giantsCookingPotBatchCount = 16;

    static {
        String[] nonRawMeats = {
            "minecraft:cooked_porkchop",
            "minecraft:cooked_beef",
            "minecraft:cooked_chicken",
            "minecraft:cooked_mutton",
            "minecraft:cooked_rabbit",
            "twilightforest:cooked_venison",
            "twilightforest:cooked_meef",
            "twilight_spark_delight:cooked_wild_boar_meat",
            "twilight_spark_delight:cooked_bighorn_mutton",
            "twilight_spark_delight:cooked_hermit_crab_leg",
            "twilight_spark_delight:cooked_fire_beetle_leg",
            "twilight_spark_delight:cooked_slime_beetle_leg",
            "twilight_spark_delight:cooked_pinch_beetle_leg",
            "twilight_spark_delight:mino_patty"
        };
        for (String id : nonRawMeats) {
            EXPERIMENT_250_NON_RAW_MEATS.add(new ResourceLocation(id));
        }
    }

    private TSDConfig()
    {
    }

    public static void load(File file)
    {
        Configuration config = new Configuration(file);
        try {
            hermitCrabAnvilBonusLegChance = config.getFloat(
                "hermitCrabAnvilBonusLegChance",
                "recipes",
                0.75F,
                0.0F,
                1.0F,
                "寄居蟹在砧板上用铁砧处理时，额外获得三个生寄居蟹腿的概率。"
                    + "\nChance for a cutting board anvil process on a Helmet Crab to yield three extra Helmet Crab Legs."
            );
            hermitCrabAnvilArmorShardClusterChance = config.getFloat(
                "hermitCrabAnvilArmorShardClusterChance",
                "recipes",
                0.25F,
                0.0F,
                1.0F,
                "寄居蟹在砧板上用铁砧处理时，获得五个装甲碎片堆的概率。"
                    + "\nChance for a cutting board anvil process on a Helmet Crab to yield five Armor Shard Clusters."
            );
            experimentProliferationChance = config.getFloat(
                "experimentProliferationChance",
                "gameplay",
                0.30F,
                0.0F,
                1.0F,
                "实验原体在玩家进食或受伤时进行增殖的概率。"
                    + "\nChance for the Experiment Prototype to proliferate when the player eats or takes damage."
            );
            experiment250ActivityDiscountPerLevel = config.getFloat(
                "experiment250ActivityDiscountPerLevel",
                "experiment250",
                0.06F,
                0.0F,
                1.0F,
                "试验物品250每提升一级时，增殖生肉所获得的活性消耗减免；0.06表示每级减免6%。"
                    + "\nActivity-cost discount per Experiment 250 level above level 1 when replicating raw meat; 0.06 means 6% per level."
            );
            boolean hadMeatActivityCosts = config.hasKey("experiment250", "meatActivityCosts");
            boolean hadEntityMeatBindings = config.hasKey("experiment250", "entityMeatBindings");
            Property meatDefaultsVersion = config.get(
                "experiment250",
                "meatDefaultsVersion",
                hadMeatActivityCosts || hadEntityMeatBindings ? 0 : EXPERIMENT_250_MEAT_DEFAULTS_VERSION,
                "试验物品250默认增殖材料配置的迁移版本，请勿手动修改。"
                    + "\nMigration version for Experiment 250's default replication materials; do not edit manually."
            );
            Property meatActivityCosts = config.get(
                "experiment250",
                "meatActivityCosts",
                DEFAULT_EXPERIMENT_250_MEAT_ACTIVITY_COSTS,
                "可由试验物品250增殖的材料及单次增殖活性消耗，格式为“物品注册名:活性值”；已知熟肉默认不参与增殖。火腿和兔子脚的默认值分别为生猪肉和生兔肉默认值的五倍，但可以独立修改。"
                    + "\nMaterials replicable by Experiment 250 and their activity cost per replicated item, formatted as 'item_registry_name:activity_cost'; known cooked meats are excluded by default. Ham and Rabbit's Foot default to five times the default Porkchop and raw Rabbit costs respectively, but can be configured independently."
            );
            String[] meatActivityCostValues = meatActivityCosts.getStringList();
            if (meatDefaultsVersion.getInt() < 2) {
                meatActivityCostValues = appendMissingActivityCosts(
                    meatActivityCostValues,
                    EXPERIMENT_250_MEAT_COSTS_ADDED_IN_VERSION_2
                );
                meatActivityCosts.set(meatActivityCostValues);
            }
            loadExperiment250MeatActivityCosts(meatActivityCostValues);
            experiment250WorkstationMode = normalizeExperiment250WorkstationMode(config.getString(
                "workstationMode",
                "experiment250",
                "cutting_board",
                "试验物品250增殖生肉所使用的工作站，可选 cutting_board、cooking_pot 或 crafting_table。"
                    + "\nWorkstation used by Experiment 250 to replicate raw meat: cutting_board, cooking_pot, or crafting_table."
            ));
            experiment250BindingModeEnabled = config.getBoolean(
                "bindingModeEnabled",
                "experiment250",
                false,
                "开启后，潜行右键生物可为试验物品250绑定生肉；增殖时使用面团代替生肉原料。"
                    + "\nWhen enabled, sneak-right-clicking a creature binds its configured raw meat to Experiment 250, and dough replaces raw meat as the replication input."
            );
            String entityBindingComment =
                "绑定模式中生物与生肉的对应关系，格式为“生物注册名=物品注册名”；可重复填写同一生物来设置按配置顺序轮换的多个物品。若目标物品未配置活性值，则按对应生物最大生命值自动生成。"
                    + "\nCreature-to-raw-meat bindings used by binding mode, formatted as 'entity_registry_name=item_registry_name'; repeat an entity to define multiple items that cycle in configuration order. If the target item has no Activity cost, one is generated from the creature's maximum health.";
            Property entityBindings = config.get(
                "experiment250",
                "entityMeatBindings",
                DEFAULT_EXPERIMENT_250_ENTITY_MEATS,
                entityBindingComment
            );
            String[] entityBindingValues = entityBindings.getStringList();
            if (meatDefaultsVersion.getInt() < 1) {
                entityBindingValues = appendMissingEntityBindings(
                    entityBindingValues,
                    EXPERIMENT_250_MEAT_BINDINGS_ADDED_IN_VERSION_1
                );
                entityBindings.set(entityBindingValues);
            }
            meatDefaultsVersion.set(EXPERIMENT_250_MEAT_DEFAULTS_VERSION);
            loadExperiment250EntityMeats(entityBindingValues);
            resolveExperiment250GeneratedActivityCosts(null);
            experiment250FatalProtectionActivityCost = config.getFloat(
                "fatalProtectionActivityCost",
                "experiment250",
                20000.0F,
                0.0F,
                100000000.0F,
                "试验物品250抵消一次致死伤害所需并消耗的活性值；物品活性必须严格大于此值。"
                    + "\nActivity required and consumed when Experiment 250 prevents fatal damage; the item's activity must be strictly greater than this value."
            );
            experiment250FatalProtectionTriggersPerSleep = config.getInt(
                "fatalProtectionTriggersPerSleep",
                "experiment250",
                3,
                0,
                1000,
                "每次睡眠周期内，消耗触发次数的濒死保护最多可生效多少次。"
                    + "\nMaximum fatal-protection activations that consume a trigger during each sleep cycle."
            );
            experiment250FatalProtectionAllowsBossAttacks = config.getBoolean(
                "fatalProtectionAllowsBossAttacks",
                "experiment250",
                true,
                "BOSS造成的致死伤害是否允许触发试验物品250的濒死保护。"
                    + "\nWhether fatal damage caused by bosses may activate Experiment 250 fatal protection."
            );
            experiment250FatalProtectionBossAttacksConsumeTrigger = config.getBoolean(
                "fatalProtectionBossAttacksConsumeTrigger",
                "experiment250",
                true,
                "BOSS攻击触发濒死保护时是否消耗本次睡眠周期的触发次数。"
                    + "\nWhether fatal protection activated by a boss attack consumes a trigger for the current sleep cycle."
            );
            loadResourceLocationSet(
                config.getStringList(
                    "fatalProtectionNonConsumingAttackers",
                    "experiment250",
                    new String[0],
                    "这些生物造成的致死伤害触发濒死保护时不会消耗本次睡眠周期的触发次数，填写生物注册名。"
                        + "\nEntity registry ids whose fatal attacks activate protection without consuming a trigger for the current sleep cycle."
                ),
                EXPERIMENT_250_NON_CONSUMING_ATTACKERS
            );
            experiment250FatalProtectionItemLocation = normalizeExperiment250ItemLocation(config.getString(
                "fatalProtectionItemLocation",
                "experiment250",
                "inventory",
                "濒死保护查找试验物品250的位置，可选 inventory、offhand 或 baubles_or_offhand。"
                    + "\nLocation searched for Experiment 250 fatal protection: inventory, offhand, or baubles_or_offhand."
            ));
            chargeSpeedBonus = config.getFloat(
                "chargeSpeedBonus",
                "effects",
                0.20F,
                0.0F,
                10.0F,
                "冲锋效果提供的移动速度加成。"
                    + "\nMovement speed bonus granted by the Charge effect."
            );
            chargeStepHeightBonus = config.getFloat(
                "chargeStepHeightBonus",
                "effects",
                5.0F,
                0.0F,
                20.0F,
                "冲锋效果提供的额外跨步高度。"
                    + "\nAdditional step height granted by the Charge effect."
            );
            chargeMaxAttackBonus = config.getFloat(
                "chargeMaxAttackBonus",
                "effects",
                0.50F,
                0.0F,
                10.0F,
                "冲锋效果在疾跑或暴击攻击时可提供的最大伤害加成。"
                    + "\nMaximum damage bonus from the Charge effect while sprinting or landing a critical hit."
            );
            symbiosisHungerDrainPerTick = config.getFloat(
                "symbiosisHungerDrainPerTick",
                "effects",
                0.0025F,
                0.0F,
                1.0F,
                "共生效果每刻减少的基础饥饿值。"
                    + "\nBase hunger drain per tick for the Symbiosis effect."
            );
            symbiosisPlayerDamageReductionBase = config.getFloat(
                "symbiosisPlayerDamageReductionBase",
                "effects",
                0.10F,
                0.0F,
                1.0F,
                "共生效果在玩家身上提供的基础伤害减免比例。"
                    + "\nBase damage reduction ratio granted by the Symbiosis effect on players."
            );
            symbiosisPlayerDamageReductionPerAmplifier = config.getFloat(
                "symbiosisPlayerDamageReductionPerAmplifier",
                "effects",
                0.10F,
                0.0F,
                1.0F,
                "共生效果每级额外提供的玩家伤害减免比例。"
                    + "\nAdditional player damage reduction ratio per amplifier level for the Symbiosis effect."
            );
            symbiosisHealIntervalTicks = config.getInt(
                "symbiosisHealIntervalTicks",
                "effects",
                100,
                1,
                1200,
                "共生效果在非玩家生物上回复生命的间隔刻数。"
                    + "\nTick interval between healing pulses for the Symbiosis effect on non-player entities."
            );
            symbiosisEntityHealAmount = config.getFloat(
                "symbiosisEntityHealAmount",
                "effects",
                1.0F,
                0.0F,
                20.0F,
                "共生效果在非玩家生物上每次回复的生命值。"
                    + "\nAmount of health restored each pulse by the Symbiosis effect on non-player entities."
            );
            shrinkBaseReduction = config.getFloat(
                "shrinkBaseReduction",
                "effects",
                0.40F,
                0.0F,
                0.99F,
                "缩小效果的基础缩小比例，0.40表示缩小40%。"
                    + "\nBase reduction ratio for the Shrink effect; 0.40 means 40% smaller."
            );
            shrinkReductionPerLevel = config.getFloat(
                "shrinkReductionPerLevel",
                "effects",
                0.10F,
                0.0F,
                0.99F,
                "缩小效果每个药水等级额外增加的缩小比例，0.10表示每级额外缩小10%。"
                    + "\nAdditional reduction ratio per potion level for the Shrink effect; 0.10 means 10% more reduction per level."
            );
            enlargeBaseScale = config.getFloat(
                "enlargeBaseScale",
                "effects",
                1.00F,
                0.0F,
                20.0F,
                "扩大效果的基础放大比例，1.00表示放大100%。"
                    + "\nBase growth ratio for the Enlarge effect; 1.00 means 100% larger."
            );
            enlargeScalePerLevel = config.getFloat(
                "enlargeScalePerLevel",
                "effects",
                1.00F,
                0.0F,
                20.0F,
                "扩大效果每个药水等级额外增加的放大比例，1.00表示每级额外放大100%。"
                    + "\nAdditional growth ratio per potion level for the Enlarge effect; 1.00 means 100% more growth per level."
            );
            twilightCheeseFondueAdvancementDinerCount = config.getInt(
                "twilightCheeseFondueAdvancementDinerCount",
                "gameplay",
                3,
                1,
                16,
                "暮色芝士火锅分食成就需要的食客人数。\nRequired diner count for Twilight Cheese Fondue sharing advancements."
            );
            doubleCrownIceCreamNoFrostedChance = config.getFloat(
                "doubleCrownIceCreamNoFrostedChance",
                "food",
                0.10F,
                0.0F,
                1.0F,
                "食用双冠冰淇淋后不获得冻结效果的概率。"
                    + "\nChance for Double-Crown Ice Cream to skip the Frosted effect after being eaten."
            );
            doubleCrownIceCreamLongFrostedChance = config.getFloat(
                "doubleCrownIceCreamLongFrostedChance",
                "food",
                0.10F,
                0.0F,
                1.0F,
                "食用双冠冰淇淋后获得十分钟冻结效果的概率。"
                    + "\nChance for Double-Crown Ice Cream to grant ten minutes of Frosted after being eaten."
            );
            extendedFoodStatsEnabled = config.getBoolean(
                "extendedFoodStatsEnabled",
                "gameplay",
                true,
                "是否启用扩展饥饿值与饱和度上限系统；开启后玩家饥饿值和饱和度上限翻倍至40，关闭时完全不替换玩家饥饿数据也不叠加HUD。\n"
                    + "Whether to enable the extended hunger and saturation cap system; when enabled, player hunger and saturation caps double to 40, and when disabled no player food data or HUD overlay is replaced."
            );
            extendedFoodKeepOnRespawn = config.getBoolean(
                "extendedFoodKeepOnRespawn",
                "gameplay",
                true,
                "开启时玩家死亡复活后使用原版复活饥饿值与饱和度；关闭时复活后饥饿值恢复到当前扩展上限，饱和度仍使用原版复活值。\n"
                    + "When enabled, respawn uses vanilla hunger and saturation values; when disabled, respawn restores hunger to the current extended cap while saturation still uses the vanilla respawn value."
            );
            extendedFoodProgressiveCapsEnabled = config.getBoolean(
                "extendedFoodProgressiveCapsEnabled",
                "gameplay",
                true,
                "开启时额外饥饿值与饱和度上限会随暮色森林进度逐步解锁，每个指定进度各增加2点。\n"
                    + "When enabled, extra hunger and saturation caps unlock gradually with Twilight Forest progression, gaining 2 points each per configured progression advancement."
            );
            extendedFoodExtraConsumptionAndBenefitsEnabled = config.getBoolean(
                "extendedFoodExtraConsumptionAndBenefitsEnabled",
                "gameplay",
                true,
                "开启时额外饥饿值与饱和度区间的消耗提高，并提高原版用饥饿值恢复生命、疾跑速度与挖掘速度。\n"
                    + "When enabled, consumption in the extra hunger and saturation range is increased, while vanilla hunger-based healing, sprint speed, and mining speed are improved."
            );
            extendedFoodExtraConsumptionMultiplier = config.getFloat(
                "extendedFoodExtraConsumptionMultiplier",
                "gameplay",
                0.50F,
                0.0F,
                10.0F,
                "额外饥饿值与饱和度区间的额外消耗倍率，0.50表示额外增加50%。\n"
                    + "Extra consumption multiplier in the extended hunger and saturation range; 0.50 means 50% additional consumption."
            );
            extendedFoodExtraBenefitMultiplier = config.getFloat(
                "extendedFoodExtraBenefitMultiplier",
                "gameplay",
                0.25F,
                0.0F,
                10.0F,
                "额外饥饿值与饱和度区间给予的生命恢复、疾跑与挖掘加成倍率，0.25表示提升25%。\n"
                    + "Benefit multiplier granted in the extended hunger and saturation range for healing, sprinting, and mining; 0.25 means 25% stronger."
            );
            rabbitPocketWatchKillerRabbitChance = config.getFloat(
                "rabbitPocketWatchKillerRabbitChance",
                "gameplay",
                0.10F,
                0.0F,
                1.0F,
                "使用转换粉转换小兔子或兔子时，将其转换为特殊杀手兔的概率；在任意空心矿山范围内时固定为100%。\n"
                    + "Chance for transformation powder used on a bunny or rabbit to create a special killer rabbit; this is forced to 100% inside any hollow hill."
            );
            armoredGiantSkilletVariantChance = config.getFloat(
                "armoredGiantSkilletVariantChance",
                "gameplay",
                0.20F,
                0.0F,
                1.0F,
                "武装巨人在生成时变为头戴平底锅、手持铁刀的特殊变体的概率。\n"
                    + "Chance for an Armored Giant to spawn as the special Skillet and Iron Knife variant."
            );
            armoredGiantKitchenSetDropChance = config.getFloat(
                "armoredGiantKitchenSetDropChance",
                "gameplay",
                0.25F,
                0.0F,
                1.0F,
                "头戴平底锅、手持铁刀的特殊武装巨人死亡时成套掉落巨人炉灶与巨人厨锅的概率。\n"
                    + "Chance for the special Skillet and Iron Knife Armored Giant to drop the Giant's Stove and Giant's Cooking Pot together."
            );
            pickledBrackenJarBaseRipeningChance = config.getFloat(
                "pickledBrackenJarBaseRipeningChance",
                "gameplay",
                0.50F,
                0.0F,
                10.0F,
                "未腌制完成的蕨菜罐被随机刻选中时增加熟成计数的基础概率；超过1.0的部分会继续判定额外阶段。\n"
                    + "Base chance for an Unripe Pickled Bracken Jar to gain ripening progress on a random tick; values above 1.0 roll for extra progress."
            );
            gloryCrucibleBrewingTimeTicks = config.getInt(
                "gloryCrucibleBrewingTimeTicks",
                "gameplay",
                200,
                1,
                72000,
                "荣耀坩埚完成一次酿造所需的时间（刻）；200刻为10秒。"
                    + "\nBrewing time in ticks for one Glory Crucible conversion; 200 ticks equals 10 seconds."
            );
            int configuredGloryCrucibleCapacity = config.getInt(
                "gloryCrucibleCapacityMb",
                "gameplay",
                2000,
                GloryCrucibleCapacity.BUCKET_MB,
                64000,
                "荣耀坩埚可容纳的最大液体量（mB）。必须为1000 mB的整数倍；无效值会向下调整到最近的有效值。\n"
                    + "Maximum Glory Crucible liquid capacity in mB. It must be a multiple of 1000 mB; invalid values are rounded down to the nearest valid value."
            );
            gloryCrucibleCapacityMb = GloryCrucibleCapacity.normalizeCapacityMb(configuredGloryCrucibleCapacity);
            if (gloryCrucibleCapacityMb != configuredGloryCrucibleCapacity) {
                config.get("gameplay", "gloryCrucibleCapacityMb", 2000).set(gloryCrucibleCapacityMb);
            }
            giantCookingPotBatchCount = config.getInt(
                "giantCookingPotBatchCount",
                "gameplay",
                8,
                1,
                64,
                "巨型厨锅在一次烹饪周期内最多同时制作的配方批次数。\n"
                    + "Maximum recipe batches the Giant Cooking Pot processes during one cooking cycle."
            );
            giantsStoveBatchCount = config.getInt(
                "giantsStoveBatchCount",
                "gameplay",
                16,
                1,
                16,
                "巨人炉灶可同时处理的食材份数；实际数量仍受可用槽位与原料数量限制。\n"
                    + "Maximum portions the Giant's Stove can process at once; actual processing is still limited by available slots and ingredients."
            );
            giantsCookingPotBatchCount = config.getInt(
                "giantsCookingPotBatchCount",
                "gameplay",
                16,
                1,
                16,
                "巨人厨锅在一次烹饪周期内最多连续制作的配方批次数；实际数量仍受原料与产物堆叠上限限制。\n"
                    + "Maximum recipe batches the Giant's Cooking Pot processes during one cooking cycle; actual processing is still limited by ingredients and output stack limits."
            );
            String[] values = config.getStringList(
                "randomCureEffectBlacklist",
                "effects",
                new String[0],
                "谜题羊奶、谜题羊奶酪以及后续随机解除负面效果食物不可解除的药水注册名。"
                    + "\nPotion registry ids that cannot be removed by Quest Ram Milk / Quest Ram Cheese and later random-cure foods."
            );
            RANDOM_CURE_BLACKLIST.clear();
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    RANDOM_CURE_BLACKLIST.add(new ResourceLocation(value.trim()));
                }
            }
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static boolean isRandomCureBlacklisted(Potion potion)
    {
        ResourceLocation id = ForgeRegistries.POTIONS.getKey(potion);
        return id != null && RANDOM_CURE_BLACKLIST.contains(id);
    }

    public static double getExperiment250MeatActivityCost(ResourceLocation itemId)
    {
        if (itemId == null || EXPERIMENT_250_NON_RAW_MEATS.contains(itemId)) {
            return -1.0D;
        }
        Double cost = EXPERIMENT_250_MEAT_ACTIVITY_COSTS.get(itemId);
        return cost == null ? -1.0D : cost;
    }

    public static boolean isExperiment250RawMeat(ResourceLocation itemId)
    {
        return getExperiment250MeatActivityCost(itemId) > 0.0D;
    }

    public static Map<ResourceLocation, Double> getExperiment250MeatActivityCosts()
    {
        return Collections.unmodifiableMap(EXPERIMENT_250_MEAT_ACTIVITY_COSTS);
    }

    public static Set<ResourceLocation> getExperiment250ReplicationMeatIds()
    {
        Set<ResourceLocation> meatIds = new LinkedHashSet<>(EXPERIMENT_250_MEAT_ACTIVITY_COSTS.keySet());
        for (List<ResourceLocation> candidates : EXPERIMENT_250_ENTITY_MEATS.values()) {
            for (ResourceLocation meatId : candidates) {
                if (!EXPERIMENT_250_NON_RAW_MEATS.contains(meatId)) {
                    meatIds.add(meatId);
                }
            }
        }
        return Collections.unmodifiableSet(meatIds);
    }

    public static ResourceLocation getExperiment250MeatForEntity(ResourceLocation entityId)
    {
        return getNextExperiment250MeatForEntity(entityId, null);
    }

    public static ResourceLocation getNextExperiment250MeatForEntity(
        ResourceLocation entityId,
        ResourceLocation currentMeatId
    )
    {
        List<ResourceLocation> candidates = entityId == null ? null : EXPERIMENT_250_ENTITY_MEATS.get(entityId);
        if (candidates == null) {
            return null;
        }
        ResourceLocation firstValid = null;
        boolean useNextValid = false;
        for (ResourceLocation meatId : candidates) {
            if (!isExperiment250RawMeat(meatId)) {
                continue;
            }
            if (firstValid == null) {
                firstValid = meatId;
            }
            if (useNextValid) {
                return meatId;
            }
            if (meatId.equals(currentMeatId)) {
                useNextValid = true;
            }
        }
        return firstValid;
    }

    public static boolean isExperiment250NonConsumingAttacker(ResourceLocation entityId)
    {
        return entityId != null && EXPERIMENT_250_NON_CONSUMING_ATTACKERS.contains(entityId);
    }

    public static Set<ResourceLocation> getExperiment250NonConsumingAttackers()
    {
        return Collections.unmodifiableSet(EXPERIMENT_250_NON_CONSUMING_ATTACKERS);
    }

    private static void loadExperiment250MeatActivityCosts(String[] values)
    {
        EXPERIMENT_250_MEAT_ACTIVITY_COSTS.clear();
        EXPERIMENT_250_AUTO_GENERATED_MEATS.clear();
        experiment250CostsResolvedWithWorld = false;
        for (String value : values) {
            if (value == null) {
                continue;
            }
            String trimmed = value.trim();
            int separator = trimmed.lastIndexOf(':');
            if (separator <= 0 || separator >= trimmed.length() - 1) {
                continue;
            }
            try {
                ResourceLocation itemId = new ResourceLocation(trimmed.substring(0, separator));
                double cost = Double.parseDouble(trimmed.substring(separator + 1));
                if (cost > 0.0D && !EXPERIMENT_250_NON_RAW_MEATS.contains(itemId)) {
                    EXPERIMENT_250_MEAT_ACTIVITY_COSTS.put(itemId, cost);
                }
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static void loadExperiment250EntityMeats(String[] values)
    {
        EXPERIMENT_250_ENTITY_MEATS.clear();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            int separator = value.indexOf('=');
            if (separator <= 0 || separator >= value.length() - 1) {
                continue;
            }
            try {
                ResourceLocation entityId = new ResourceLocation(value.substring(0, separator).trim());
                ResourceLocation meatId = new ResourceLocation(value.substring(separator + 1).trim());
                if (!EXPERIMENT_250_NON_RAW_MEATS.contains(meatId)) {
                    List<ResourceLocation> candidates = EXPERIMENT_250_ENTITY_MEATS.get(entityId);
                    if (candidates == null) {
                        candidates = new ArrayList<>();
                        EXPERIMENT_250_ENTITY_MEATS.put(entityId, candidates);
                    }
                    if (!candidates.contains(meatId)) {
                        candidates.add(meatId);
                    }
                }
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static void resolveExperiment250GeneratedActivityCosts(World world)
    {
        for (ResourceLocation meatId : EXPERIMENT_250_AUTO_GENERATED_MEATS) {
            EXPERIMENT_250_MEAT_ACTIVITY_COSTS.remove(meatId);
        }
        EXPERIMENT_250_AUTO_GENERATED_MEATS.clear();

        Map<ResourceLocation, Double> generatedCosts = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> binding : EXPERIMENT_250_ENTITY_MEATS.entrySet()) {
            List<ResourceLocation> unresolvedMeats = new ArrayList<>();
            for (ResourceLocation meatId : binding.getValue()) {
                if (!EXPERIMENT_250_MEAT_ACTIVITY_COSTS.containsKey(meatId)
                    && !EXPERIMENT_250_NON_RAW_MEATS.contains(meatId)) {
                    unresolvedMeats.add(meatId);
                }
            }
            if (unresolvedMeats.isEmpty()) {
                continue;
            }
            EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(binding.getKey());
            if (entityEntry == null) {
                continue;
            }
            try {
                Entity entity = entityEntry.newInstance(world);
                if (entity instanceof EntityLivingBase) {
                    double maxHealth = ((EntityLivingBase) entity).getMaxHealth();
                    if (maxHealth > 0.0D) {
                        for (ResourceLocation meatId : unresolvedMeats) {
                            Double previous = generatedCosts.get(meatId);
                            generatedCosts.put(meatId, previous == null ? maxHealth : Math.max(previous, maxHealth));
                        }
                    }
                }
            } catch (RuntimeException exception) {
                if (world != null && TwilightSparksDelight.logger != null) {
                    TwilightSparksDelight.logger.warn(
                        "Could not derive Experiment 250 Activity costs for {} from {}",
                        unresolvedMeats,
                        binding.getKey(),
                        exception
                    );
                }
            }
        }
        for (Map.Entry<ResourceLocation, Double> entry : generatedCosts.entrySet()) {
            putAutoGeneratedExperiment250Cost(entry.getKey(), entry.getValue());
        }
    }

    private static void putAutoGeneratedExperiment250Cost(ResourceLocation itemId, double cost)
    {
        if (cost > 0.0D && !EXPERIMENT_250_NON_RAW_MEATS.contains(itemId)) {
            EXPERIMENT_250_MEAT_ACTIVITY_COSTS.put(itemId, cost);
            EXPERIMENT_250_AUTO_GENERATED_MEATS.add(itemId);
        }
    }

    private static String[] appendMissingEntityBindings(String[] current, String[] additions)
    {
        List<String> bindings = new ArrayList<>();
        Set<String> entityIds = new HashSet<>();
        for (String value : current) {
            if (value == null) {
                continue;
            }
            bindings.add(value);
            int separator = value.indexOf('=');
            if (separator > 0) {
                entityIds.add(value.substring(0, separator).trim());
            }
        }
        for (String value : additions) {
            int separator = value.indexOf('=');
            String entityId = value.substring(0, separator).trim();
            if (entityIds.add(entityId)) {
                bindings.add(value);
            }
        }
        return bindings.toArray(new String[bindings.size()]);
    }

    private static String[] appendMissingActivityCosts(String[] current, String[] additions)
    {
        List<String> values = new ArrayList<>();
        Set<String> itemIds = new HashSet<>();
        for (String value : current) {
            if (value == null) {
                continue;
            }
            values.add(value);
            int separator = value.trim().lastIndexOf(':');
            if (separator > 0) {
                itemIds.add(value.trim().substring(0, separator));
            }
        }
        for (String value : additions) {
            int separator = value.lastIndexOf(':');
            String itemId = value.substring(0, separator);
            if (itemIds.add(itemId)) {
                values.add(value);
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (!experiment250CostsResolvedWithWorld) {
            resolveExperiment250GeneratedActivityCosts(event.getWorld());
            experiment250CostsResolvedWithWorld = true;
        }
    }

    private static String normalizeExperiment250WorkstationMode(String value)
    {
        if ("cooking_pot".equals(value) || "crafting_table".equals(value)) {
            return value;
        }
        return "cutting_board";
    }

    private static String normalizeExperiment250ItemLocation(String value)
    {
        if ("offhand".equals(value) || "baubles_or_offhand".equals(value)) {
            return value;
        }
        return "inventory";
    }

    private static void loadResourceLocationSet(String[] values, Set<ResourceLocation> target)
    {
        target.clear();
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            try {
                target.add(new ResourceLocation(value.trim()));
            } catch (RuntimeException ignored) {
            }
        }
    }
}
