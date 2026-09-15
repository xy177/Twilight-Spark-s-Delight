package xy177.twilightsparksdelight;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common configuration entry point for the new edition.
 */
public final class TSDConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> RANDOM_CURE_EFFECT_BLACKLIST = BUILDER
            .comment(
                    "随机解除负面效果的食物不能解除的药水效果注册名。",
                    "Effect registry ids that random-debuff-curing foods cannot remove."
            )
            .defineListAllowEmpty("randomCureEffectBlacklist", List.of(),
                    value -> value instanceof String id && ResourceLocation.tryParse(id) != null);

    public static final ModConfigSpec.ConfigValue<String> CONFIG_VERSION = BUILDER
            .comment(
                    "配置文件格式版本。",
                    "Configuration format version."
            )
            .define("configVersion", "1");

    public static final ModConfigSpec.BooleanValue EXTENDED_FOOD_STATS_ENABLED = BUILDER
            .comment(
                    "是否启用额外饥饿值与饱和度上限系统。",
                    "Whether to enable the extended hunger and saturation cap system."
            )
            .define("extendedFoodStatsEnabled", true);

    public static final ModConfigSpec.BooleanValue EXTENDED_FOOD_PROGRESSIVE_CAPS_ENABLED = BUILDER
            .comment(
                    "是否随暮色森林进度逐步解锁额外饥饿值与饱和度上限。",
                    "Whether extra hunger and saturation caps unlock progressively with Twilight Forest advancements."
            )
            .define("extendedFoodProgressiveCapsEnabled", true);

    public static final ModConfigSpec.BooleanValue EXTENDED_FOOD_KEEP_VANILLA_RESPAWN = BUILDER
            .comment(
                    "开启时死亡复活恢复20点饥饿值和原版初始饱和度；关闭时饥饿值恢复至当前上限。本项不改变上限。",
                    "When enabled, respawn restores 20 hunger and vanilla starting saturation; otherwise hunger is restored to the current cap. This does not change the cap."
            )
            .define("extendedFoodKeepVanillaRespawn", true);

    public static final ModConfigSpec.BooleanValue EXTENDED_FOOD_EXTRA_CONSUMPTION_ENABLED = BUILDER
            .comment(
                    "是否启用额外饥饿值区间的消耗与收益调整。",
                    "Whether to enable the extra hunger-range consumption and benefit adjustments."
            )
            .define("extendedFoodExtraConsumptionEnabled", true);

    public static final ModConfigSpec.DoubleValue EXTENDED_FOOD_EXTRA_CONSUMPTION_MULTIPLIER = BUILDER
            .comment(
                    "额外饥饿值区间的额外消耗比例，0.5表示增加50%。",
                    "Additional consumption ratio in the extended hunger range; 0.5 means 50% more."
            )
            .defineInRange("extendedFoodExtraConsumptionMultiplier", 0.5D, 0.0D, 10.0D);

    public static final ModConfigSpec.DoubleValue EXTENDED_FOOD_EXTRA_BENEFIT_MULTIPLIER = BUILDER
            .comment(
                    "额外饥饿值区间的生命恢复、疾跑和挖掘收益比例，0.25表示增加25%。",
                    "Healing, sprinting, and mining benefit ratio in the extended hunger range; 0.25 means 25% more."
            )
            .defineInRange("extendedFoodExtraBenefitMultiplier", 0.25D, 0.0D, 10.0D);

    public static final ModConfigSpec.BooleanValue EXPERIMENT_BINDING_MODE_ENABLED = BUILDER
            .comment(
                    "是否启用实验物品250的物品与生物绑定模式。",
                    "Whether to enable Experiment 250 item-to-entity binding mode."
            )
            .define("experimentBindingModeEnabled", false);

    public enum ExperimentWorkstation {
        CRAFTING_TABLE, CUTTING_BOARD, COOKING_POT
    }

    public static final ModConfigSpec.EnumValue<ExperimentWorkstation> EXPERIMENT_WORKSTATION = BUILDER
            .comment(
                    "试验物品250分化使用的工作站；培育始终使用厨锅。",
                    "Workstation used for Experiment 250 differentiation; cultivation always uses a cooking pot."
            )
            .defineEnum("experimentWorkstation", ExperimentWorkstation.CUTTING_BOARD);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXPERIMENT_ENTITY_MEAT_BINDINGS =
            BUILDER.comment(
                    "实验物品250绑定关系，每行格式为 生物注册名=物品注册名。",
                    "Experiment 250 bindings, one entry per line in the form entity_id=item_id."
            ).defineListAllowEmpty("experimentEntityMeatBindings", List.of(
                    "minecraft:pig=minecraft:porkchop",
                    "minecraft:cow=minecraft:beef",
                    "minecraft:chicken=minecraft:chicken",
                    "minecraft:sheep=minecraft:mutton",
                    "minecraft:rabbit=minecraft:rabbit",
                    "minecraft:spider=minecraft:spider_eye",
                    "minecraft:zombie=minecraft:rotten_flesh",
                    "twilightforest:wild_boar=twilight_spark_delight:raw_wild_boar_meat",
                    "twilightforest:bighorn_sheep=twilight_spark_delight:raw_bighorn_mutton",
                    "twilightforest:helmet_crab=twilight_spark_delight:hermit_crab_leg",
                    "twilightforest:fire_beetle=twilight_spark_delight:fire_beetle_leg",
                    "twilightforest:slime_beetle=twilight_spark_delight:slime_beetle_leg",
                    "twilightforest:pinch_beetle=twilight_spark_delight:pinch_beetle_leg",
                    "twilightforest:minotaur=twilightforest:raw_meef",
                    "twilightforest:minoshroom=twilightforest:raw_meef",
                    "twilightforest:hydra=twilightforest:hydra_chop"
            ), value -> value instanceof String && ((String) value).contains("="));

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXPERIMENT_MEAT_ACTIVITY_COSTS =
            BUILDER.comment(
                    "试验物品250可分化的生肉及每份活性消耗，格式为 物品注册名:活性值；熟肉默认不参与分化。",
                    "Raw materials differentiable by Experiment 250 and activity cost per item, in the form item_id:activity_cost; cooked meats are excluded by default."
            ).defineListAllowEmpty("experimentMeatActivityCosts", List.of(
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
            ), value -> value instanceof String && ((String) value).lastIndexOf(':') > 0);

    private static Map<ResourceLocation, Double> parsedMeatActivityCosts() {
        Map<ResourceLocation, Double> result = new LinkedHashMap<>();
        for (String raw : EXPERIMENT_MEAT_ACTIVITY_COSTS.get()) {
            int separator = raw.lastIndexOf(':');
            if (separator <= 0 || separator == raw.length() - 1) {
                continue;
            }
            try {
                ResourceLocation item = ResourceLocation.parse(raw.substring(0, separator).trim());
                double cost = Double.parseDouble(raw.substring(separator + 1).trim());
                if (Double.isFinite(cost) && cost > 0.0D) {
                    result.put(item, cost);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result;
    }

    public static Map<ResourceLocation, Double> getExperimentMeatActivityCosts() {
        Map<ResourceLocation, Double> result = parsedMeatActivityCosts();
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> binding : getExperimentBindings().entrySet()) {
            double health = getEntityMaxHealth(binding.getKey());
            if (health > 0.0D && Double.isFinite(health)) {
                for (ResourceLocation item : binding.getValue()) {
                    if (!result.containsKey(item)) {
                        result.put(item, getExperimentMeatActivityCost(item));
                    }
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public static double getExperimentMeatActivityCost(ResourceLocation item) {
        if (item == null) {
            return 0.0D;
        }
        double explicit = parsedMeatActivityCosts().getOrDefault(item, 0.0D);
        if (explicit > 0.0D) {
            return explicit;
        }
        double generated = 0.0D;
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> binding : getExperimentBindings().entrySet()) {
            if (!binding.getValue().contains(item)) {
                continue;
            }
            generated = Math.max(generated, getEntityMaxHealth(binding.getKey()));
        }
        return generated;
    }

    @SuppressWarnings("unchecked")
    private static double getEntityMaxHealth(ResourceLocation entityId) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId).orElse(null);
        if (entityType == null) {
            return 0.0D;
        }
        if (!DefaultAttributes.hasSupplier(entityType)) {
            return 0.0D;
        }
        try {
            EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) entityType;
            return DefaultAttributes.getSupplier(livingType).getBaseValue(Attributes.MAX_HEALTH);
        } catch (RuntimeException ignored) {
            return 0.0D;
        }
    }

    public static final ModConfigSpec.DoubleValue EXPERIMENT_FATAL_ACTIVITY_COST = BUILDER
            .comment(
                    "实验物品250触发濒死保护所需的最低活性值。",
                    "Minimum activity required for Experiment 250 fatal protection."
            )
            .defineInRange("experimentFatalActivityCost", 20000.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue EXPERIMENT_PROLIFERATION_CHANCE = BUILDER
            .comment(
                    "实验物品250尝试增殖时的成功概率。",
                    "Chance for Experiment 250 to successfully proliferate."
            )
            .defineInRange("experimentProliferationChance", 0.30D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue EXPERIMENT_ACTIVITY_DISCOUNT_PER_LEVEL = BUILDER
            .comment(
                    "试验物品250每提升一级时生肉分化活性消耗的折扣比例；0.06表示每级减少6%。",
                    "Activity cost discount per Experiment 250 level for raw-meat differentiation; 0.06 means 6% less per level."
            )
            .defineInRange("experimentActivityDiscountPerLevel", 0.06D, 0.0D, 1.0D);

    public static final ModConfigSpec.IntValue EXPERIMENT_FATAL_TRIGGERS_PER_SLEEP = BUILDER
            .comment(
                    "每个睡眠周期内实验物品250最多触发濒死保护的次数。",
                    "Maximum fatal protection triggers per sleep cycle."
            )
            .defineInRange("experimentFatalTriggersPerSleep", 3, 1, 100);

    public static final ModConfigSpec.BooleanValue EXPERIMENT_FATAL_ALLOWS_BOSS_ATTACKS = BUILDER
            .comment(
                    "BOSS造成的致死伤害是否允许触发试验物品250的濒死保护。",
                    "Whether fatal damage caused by bosses may activate Experiment 250 fatal protection."
            )
            .define("experimentFatalAllowsBossAttacks", true);

    public static final ModConfigSpec.BooleanValue EXPERIMENT_FATAL_BOSS_ATTACKS_CONSUME_TRIGGER = BUILDER
            .comment(
                    "BOSS攻击触发濒死保护时是否消耗本次睡眠周期的触发次数。",
                    "Whether fatal protection activated by a boss attack consumes a trigger for the current sleep cycle."
            )
            .define("experimentFatalBossAttacksConsumeTrigger", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXPERIMENT_FATAL_NON_CONSUMING_ATTACKERS =
            BUILDER.comment(
                    "这些生物造成的致死伤害触发濒死保护时不会消耗本次睡眠周期的触发次数，每行填写一个生物注册名。",
                    "Entity registry ids whose fatal attacks activate protection without consuming a trigger for the current sleep cycle, one id per line."
            ).defineListAllowEmpty("experimentFatalNonConsumingAttackers", List.of(),
                    value -> value instanceof String && !((String) value).isBlank());

    public static final ModConfigSpec.EnumValue<FatalProtectionItemLocation> EXPERIMENT_FATAL_ITEM_LOCATION =
            BUILDER.comment(
                    "濒死保护查找试验物品250的位置。",
                    "Where Fatal Protection searches for Experiment 250."
            ).defineEnum("experimentFatalItemLocation", FatalProtectionItemLocation.INVENTORY);

    public static final ModConfigSpec.DoubleValue SHRINK_BASE_REDUCTION = BUILDER
            .comment(
                    "缩小效果的基础缩小比例；0.40表示基础缩小40%。",
                    "Base reduction used by Shrink; 0.40 means a 40% reduction."
            )
            .defineInRange("shrinkBaseReduction", 0.40D, 0.0D, 0.9D);

    public static final ModConfigSpec.DoubleValue SHRINK_REDUCTION_PER_LEVEL = BUILDER
            .comment(
                    "缩小效果每级额外缩小比例；0.10表示每级额外缩小10%。",
                    "Additional reduction per Shrink level; 0.10 means 10% per level."
            )
            .defineInRange("shrinkReductionPerLevel", 0.10D, 0.0D, 0.9D);

    public static final ModConfigSpec.DoubleValue ENLARGE_BASE_SCALE = BUILDER
            .comment(
                    "扩大效果的基础放大比例；1.00表示基础放大100%。",
                    "Base scale increase used by Enlarge; 1.00 means a 100% increase."
            )
            .defineInRange("enlargeBaseScale", 1.00D, 0.0D, 20.0D);

    public static final ModConfigSpec.DoubleValue ENLARGE_SCALE_PER_LEVEL = BUILDER
            .comment(
                    "扩大效果每级额外放大比例；1.00表示每级额外放大100%。",
                    "Additional scale increase per Enlarge level; 1.00 means 100% per level."
            )
            .defineInRange("enlargeScalePerLevel", 1.00D, 0.0D, 20.0D);

    public static final ModConfigSpec.DoubleValue CHARGE_SPEED_BONUS = BUILDER
            .comment("冲锋提供的移动速度加成，0.20表示增加20%。", "Movement speed bonus from Charge; 0.20 means 20%.")
            .defineInRange("chargeSpeedBonus", 0.20D, 0.0D, 10.0D);
    public static final ModConfigSpec.DoubleValue CHARGE_STEP_HEIGHT_BONUS = BUILDER
            .comment("冲锋提供的跨步高度加成。", "Step-height bonus from Charge.")
            .defineInRange("chargeStepHeightBonus", 5.0D, 0.0D, 10.0D);
    public static final ModConfigSpec.DoubleValue CHARGE_MAX_ATTACK_BONUS = BUILDER
            .comment("冲锋按实际移速增幅提供的攻击加成上限。", "Maximum Charge attack bonus based on the actual movement speed increase.")
            .defineInRange("chargeMaxAttackBonus", 0.50D, 0.0D, 10.0D);
    public static final ModConfigSpec.DoubleValue SYMBIOSIS_DAMAGE_REDUCTION = BUILDER
            .comment("共生提供的基础减伤，0.10表示10%。", "Base damage reduction from Symbiosis; 0.10 means 10%.")
            .defineInRange("symbiosisDamageReduction", 0.10D, 0.0D, 0.95D);
    public static final ModConfigSpec.DoubleValue SYMBIOSIS_DAMAGE_REDUCTION_PER_LEVEL = BUILDER
            .comment("共生每级额外减伤，0.10表示每级10%。", "Additional damage reduction per Symbiosis level; 0.10 means 10% per level.")
            .defineInRange("symbiosisDamageReductionPerLevel", 0.10D, 0.0D, 0.95D);
    public static final ModConfigSpec.DoubleValue SYMBIOSIS_HUNGER_DRAIN_PER_TICK = BUILDER
            .comment("玩家共生每刻消耗的饥饿值，受共生等级影响。", "Hunger drain per tick for player Symbiosis, affected by its level.")
            .defineInRange("symbiosisHungerDrainPerTick", 0.0025D, 0.0D, 10.0D);
    public static final ModConfigSpec.IntValue SYMBIOSIS_HEAL_INTERVAL = BUILDER
            .comment("非玩家生物共生的治疗间隔（刻）。", "Healing interval in ticks for non-player Symbiosis.")
            .defineInRange("symbiosisHealInterval", 100, 1, 72000);
    public static final ModConfigSpec.DoubleValue SYMBIOSIS_ENTITY_HEAL_AMOUNT = BUILDER
            .comment("共生每次为非玩家生物恢复的生命值。", "Health restored to a non-player entity per Symbiosis healing interval.")
            .defineInRange("symbiosisEntityHealAmount", 1.0D, 0.0D, 1000.0D);
    public static final ModConfigSpec.DoubleValue GRIEF_DAMAGE_BONUS_PER_LEVEL = BUILDER
            .comment("悲恸每级造成的额外伤害，0.10表示每级10%。", "Additional damage per Grief level; 0.10 means 10% per level.")
            .defineInRange("griefDamageBonusPerLevel", 0.10D, 0.0D, 10.0D);

    public static final ModConfigSpec.DoubleValue DOUBLE_CROWN_ICE_CREAM_NO_FROSTED_CHANCE = BUILDER
            .comment(
                    "食用双冠冰淇淋后不获得冻结效果的概率。",
                    "Chance for Double-Crown Ice Cream to skip the Frosted effect after being eaten."
            )
            .defineInRange("doubleCrownIceCreamNoFrostedChance", 0.10D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue DOUBLE_CROWN_ICE_CREAM_LONG_FROSTED_CHANCE = BUILDER
            .comment(
                    "食用双冠冰淇淋后获得十分钟冻结效果的概率。",
                    "Chance for Double-Crown Ice Cream to grant ten minutes of Frosted after being eaten."
            )
            .defineInRange("doubleCrownIceCreamLongFrostedChance", 0.10D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue HELMET_CRAB_CUTTING_EXTRA_LEGS_CHANCE = BUILDER
            .comment(
                    "在砧板上用铁砧处理寄居蟹时，额外获得三个生寄居蟹腿的概率。",
                    "Chance to obtain three extra raw Helmet Crab Legs when processing a Helmet Crab with an anvil on a cutting board."
            )
            .defineInRange("helmetCrabCuttingExtraLegsChance", 0.75D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue HELMET_CRAB_CUTTING_ARMOR_CHANCE = BUILDER
            .comment(
                    "在砧板上用铁砧处理寄居蟹时，获得五个装甲碎片堆的概率。",
                    "Chance to obtain five Armor Shard Clusters when processing a Helmet Crab with an anvil on a cutting board."
            )
            .defineInRange("helmetCrabCuttingArmorChance", 0.25D, 0.0D, 1.0D);

    public static final ModConfigSpec.IntValue GLORY_CRUCIBLE_CAPACITY_MB = BUILDER
            .comment(
                    "荣耀坩埚的最大液体容量（毫桶），非整桶数值向下取整到1000的倍数。",
                    "Maximum Glory Crucible capacity in millibuckets; rounded down to a multiple of 1000."
            )
            .defineInRange("gloryCrucibleCapacityMb", 2000, 1000, 64000);

    public static final ModConfigSpec.IntValue GLORY_CRUCIBLE_BREWING_TICKS = BUILDER
            .comment(
                    "荣耀坩埚每次整锅酿造需要的游戏刻数，200刻等于10秒。",
                    "Ticks required per Glory Crucible batch conversion; 200 ticks equals 10 seconds."
            )
            .defineInRange("gloryCrucibleBrewingTimeTicks", 200, 1, 72000);

    public static final ModConfigSpec.DoubleValue RABBIT_POCKET_WATCH_KILLER_RABBIT_CHANCE = BUILDER
            .comment(
                    "使用转化粉转化兔子时，将其转化为特殊杀手兔的概率。",
                    "Chance for Transformation Powder to turn a rabbit into a special killer rabbit."
            )
            .defineInRange("rabbitPocketWatchKillerRabbitChance", 0.10D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_BASE_RIPENING_CHANCE = BUILDER
            .comment(
                    "未腌制完成的蕨菜罐每次随机刻增加熟成进度的基础概率。",
                    "Base chance for an unripe Pickled Bracken Jar to gain ripening progress per random tick."
            )
            .defineInRange("pickledBrackenJarBaseRipeningChance", 0.50D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_SHADE_BONUS = BUILDER
            .comment("上方存在遮蔽物时增加的熟成概率。", "Ripening chance bonus when a shade block is above the jar.")
            .defineInRange("pickledBrackenJarShadeBonus", 0.20D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_LEAF_SHADE_BONUS = BUILDER
            .comment("上方存在树叶遮蔽物时增加的熟成概率。", "Ripening chance bonus when leaves provide the shade above the jar.")
            .defineInRange("pickledBrackenJarLeafShadeBonus", 0.30D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_CLEAN_AREA_BONUS = BUILDER
            .comment("遮蔽物周围没有其他杂物时增加的熟成概率。", "Ripening chance bonus when the area around the shade is free of clutter.")
            .defineInRange("pickledBrackenJarCleanAreaBonus", 0.15D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_MAGIC_LOG_BONUS = BUILDER
            .comment("附近存在魔法原木核心时增加的熟成概率。", "Ripening chance bonus when a Magic Log Core is nearby.")
            .defineInRange("pickledBrackenJarMagicLogBonus", 0.50D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_MUSHROOM_COLONY_BONUS = BUILDER
            .comment("附近存在迷宫菌落时增加的熟成概率。", "Ripening chance bonus when a Labyrinth Mushroom Colony is nearby.")
            .defineInRange("pickledBrackenJarMushroomColonyBonus", 0.10D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_PEACOCK_FAN_BONUS = BUILDER
            .comment("附近使用过孔雀羽扇时增加的熟成概率。", "Ripening chance bonus after a Peacock Feather Fan was used nearby.")
            .defineInRange("pickledBrackenJarPeacockFanBonus", 0.30D, 0.0D, 100.0D);
    public static final ModConfigSpec.DoubleValue PICKLED_BRACKEN_JAR_STRUCTURE_BONUS = BUILDER
            .comment("位于中空矿山、蘑菇城堡、迷宫、骑士要塞或谜题羊树丛范围内时增加的熟成概率。",
                    "Ripening chance bonus within Hollow Hills, Mushroom Towers, Labyrinths, Knight Strongholds, or Quest Groves.")
            .defineInRange("pickledBrackenJarStructureBonus", 0.20D, 0.0D, 100.0D);

    public static final ModConfigSpec.IntValue GIANT_COOKING_POT_MAX_BATCHES = BUILDER
            .comment(
                    "巨型厨锅一次最多同时处理的配方份数。",
                    "Maximum number of recipe batches a Giant Cooking Pot can process at once."
            )
            .defineInRange("giantCookingPotMaxBatches", 8, 1, 64);

    public static final ModConfigSpec.IntValue GIANTS_COOKING_POT_MAX_BATCHES = BUILDER
            .comment(
                    "巨人厨锅一次最多同时处理的配方份数。",
                    "Maximum number of recipe batches a Giant's Cooking Pot can process at once."
            )
            .defineInRange("giantsCookingPotMaxBatches", 16, 1, 64);

    public static final ModConfigSpec.IntValue GIANTS_STOVE_MAX_PORTIONS = BUILDER
            .comment(
                    "巨人炉灶一次最多同时处理的食材份数；实际数量仍受可用槽位和原料数量限制。",
                    "Maximum portions the Giant's Stove can process at once; actual processing is still limited by available slots and ingredients."
            )
            .defineInRange("giantsStoveMaxPortions", 16, 1, 16);

    public static final ModConfigSpec.DoubleValue ARMORED_GIANT_SKILLET_VARIANT_CHANCE = BUILDER
            .comment(
                    "武装巨人生成时成为平底锅变种的概率。",
                    "Chance for an Armored Giant to spawn as the Skillet variant."
            )
            .defineInRange("armoredGiantSkilletVariantChance", 0.20D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue ARMORED_GIANT_KITCHEN_SET_DROP_CHANCE = BUILDER
            .comment(
                    "平底锅变种武装巨人死亡时掉落巨人厨具套装的概率。",
                    "Chance for the Skillet variant Armored Giant to drop the Giant kitchen set on death."
            )
            .defineInRange("armoredGiantKitchenSetDropChance", 0.25D, 0.0D, 1.0D);

    public static final ModConfigSpec.IntValue TWILIGHT_CHEESE_FONDUE_DINER_COUNT = BUILDER
            .comment(
                    "芝士火锅分食成就所需的不同食客人数。",
                    "Number of distinct diners required for Twilight Cheese Fondue sharing advancements."
            )
            .defineInRange("twilightCheeseFondueDinerCount", 3, 1, 64);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public enum FatalProtectionItemLocation {
        INVENTORY,
        OFFHAND,
        BAUBLES_OR_OFFHAND
    }

    public static Map<ResourceLocation, List<ResourceLocation>> getExperimentBindings() {
        Map<ResourceLocation, List<ResourceLocation>> result = new LinkedHashMap<>();
        for (String raw : EXPERIMENT_ENTITY_MEAT_BINDINGS.get()) {
            int separator = raw.indexOf('=');
            if (separator <= 0 || separator >= raw.length() - 1) {
                continue;
            }
            try {
                ResourceLocation entity = ResourceLocation.parse(raw.substring(0, separator).trim());
                ResourceLocation meat = ResourceLocation.parse(raw.substring(separator + 1).trim());
                result.computeIfAbsent(entity, ignored -> new java.util.ArrayList<>()).add(meat);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result;
    }

    public static boolean isExperimentFatalNonConsumingAttacker(ResourceLocation entityId) {
        if (entityId == null) {
            return false;
        }
        return EXPERIMENT_FATAL_NON_CONSUMING_ATTACKERS.get().stream()
                .map(String::trim)
                .anyMatch(entityId.toString()::equals);
    }

    private TSDConfig() {
    }
}
