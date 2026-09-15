package xy177.twilightsparksdelight.common.event;

import java.util.Locale;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import twilightforest.init.TFItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDTriggers;

/**
 * Adds the migrated entity drops and preserves the upstream tables unless a
 * legacy whole-meat entry must be redirected to the corresponding local item.
 */
public final class TSDLootEvents {
    private static final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> COMMON_KNIFE_TAG =
            net.minecraft.tags.TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("c", "tools/knife"));
    private static final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> FARMERS_KNIFE_TAG =
            net.minecraft.tags.TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("farmersdelight", "tools/knives"));
    private TSDLootEvents() {
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!event.getState().is(BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath("twilightforest", "fiddlehead")))) {
            return;
        }

        ItemStack tool = event.getTool();
        if (!isKnife(tool)
                || EnchantmentHelper.getItemEnchantmentLevel(
                        event.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                                .getOrThrow(Enchantments.SILK_TOUCH), tool) > 0) {
            return;
        }

        HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> enchantments =
                event.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        int looting = EnchantmentHelper.getItemEnchantmentLevel(
                enchantments.getOrThrow(Enchantments.LOOTING), tool);
        int fortune = EnchantmentHelper.getItemEnchantmentLevel(
                enchantments.getOrThrow(Enchantments.FORTUNE), tool);
        int amount = 1 + Math.max(0, looting) + Math.max(0, fortune) * 2;
        if (amount <= 0) {
            return;
        }

        event.getDrops().add(new ItemEntity(event.getLevel(),
                event.getPos().getX() + 0.5D,
                event.getPos().getY() + 0.5D,
                event.getPos().getZ() + 0.5D,
                new ItemStack(TSDItems.BRACKEN.get(), amount)));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation darkTowerBoss = ResourceLocation.fromNamespaceAndPath(
                "twilightforest", "darktower_boss");
        if (darkTowerBoss.equals(event.getName())) {
            addExperiment234Pool(event);
            return;
        }

        ResourceLocation hill1 = ResourceLocation.fromNamespaceAndPath("twilightforest", "hill_1");
        ResourceLocation hill2 = ResourceLocation.fromNamespaceAndPath("twilightforest", "hill_2");
        ResourceLocation hill3 = ResourceLocation.fromNamespaceAndPath("twilightforest", "hill_3");
        if (event.getName().equals(hill1) || event.getName().equals(hill2)
                || event.getName().equals(hill3)) {
            addPocketWatchPool(event, event.getName().equals(hill3) ? 5 : 1);
        }
    }

    private static void addExperiment234Pool(LootTableLoadEvent event) {
        if (event.getTable().getPool("twilight_spark_delight:experiment_234") != null) {
            return;
        }
        event.getTable().addPool(LootPool.lootPool()
                .name("twilight_spark_delight:experiment_234")
                .setRolls(net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(TSDItems.EXPERIMENT_234.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 8.0F))))
                .build());
    }

    private static void addPocketWatchPool(LootTableLoadEvent event, int weight) {
        var ops = event.getRegistries().createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE);
        var encoded = LootTable.DIRECT_CODEC.encodeStart(ops, event.getTable());
        encoded.result().ifPresent(json -> {
            var pools = json.getAsJsonObject().getAsJsonArray("pools");
            if (pools == null || pools.isEmpty() || containsPocketWatch(json)) return;
            var entry = new com.google.gson.JsonObject();
            entry.addProperty("type", "minecraft:item");
            entry.addProperty("name", "twilightforest:pocket_watch");
            entry.addProperty("weight", weight);
            // Join the existing rare roll; a separate pool would guarantee a watch.
            pools.get(pools.size() - 1).getAsJsonObject().getAsJsonArray("entries").add(entry);
            LootTable.DIRECT_CODEC.parse(ops, json).resultOrPartial(
                    xy177.twilightsparksdelight.TwilightSparksDelight.LOGGER::error)
                    .ifPresent(event::setTable);
        });
    }

    private static boolean containsPocketWatch(com.google.gson.JsonElement json) {
        if (json.isJsonArray()) {
            for (var child : json.getAsJsonArray()) if (containsPocketWatch(child)) return true;
        } else if (json.isJsonObject()) {
            var object = json.getAsJsonObject();
            if (object.has("name") && object.get("name").isJsonPrimitive()
                    && "twilightforest:pocket_watch".equals(object.get("name").getAsString())) return true;
            for (var child : object.entrySet()) if (containsPocketWatch(child.getValue())) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id == null) {
            return;
        }

        if (TSDRabbitPocketWatchEvents.canDropPocketWatch(
                entity instanceof LivingEntity living ? living : null,
                event.getSource().getEntity() instanceof Player player ? player : null)) {
            Player player = (Player) event.getSource().getEntity();
            addDrop(event, new ItemStack(TFItems.POCKET_WATCH.get()));
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                TSDTriggers.LATE_FOR_DATE.get().trigger(serverPlayer);
            }
        }

        if (!"twilightforest".equals(id.getNamespace())) {
            return;
        }

        if (TSDArmoredGiantEvents.isSkilletVariant(entity)
                && entity.getRandom().nextDouble()
                < xy177.twilightsparksdelight.TSDConfig.ARMORED_GIANT_KITCHEN_SET_DROP_CHANCE.get()) {
            addDrop(event, new ItemStack(TSDBlocks.GIANTS_STOVE.get()));
            addDrop(event, new ItemStack(TSDBlocks.GIANTS_COOKING_POT.get()));
        }

        if ("wild_boar".equals(id.getPath())) {
            replaceDrop(event, net.minecraft.world.item.Items.PORKCHOP,
                    entity.isOnFire() ? TSDItems.COOKED_WILD_BOAR_MEAT.get()
                            : TSDItems.RAW_WILD_BOAR_MEAT.get());
            replaceDrop(event, net.minecraft.world.item.Items.COOKED_PORKCHOP,
                    TSDItems.COOKED_WILD_BOAR_MEAT.get());
        } else if ("bighorn_sheep".equals(id.getPath())) {
            replaceDrop(event, net.minecraft.world.item.Items.MUTTON,
                    entity.isOnFire() ? TSDItems.COOKED_BIGHORN_MUTTON.get()
                            : TSDItems.RAW_BIGHORN_MUTTON.get());
            replaceDrop(event, net.minecraft.world.item.Items.COOKED_MUTTON,
                    TSDItems.COOKED_BIGHORN_MUTTON.get());
        }

        ItemStack leg = switch (id.getPath()) {
            case "helmet_crab" -> new ItemStack(entity.isOnFire()
                    ? TSDItems.COOKED_HERMIT_CRAB_LEG.get() : TSDItems.HERMIT_CRAB_LEG.get());
            case "pinch_beetle" -> new ItemStack(entity.isOnFire()
                    ? TSDItems.COOKED_PINCH_BEETLE_LEG.get() : TSDItems.PINCH_BEETLE_LEG.get());
            case "slime_beetle" -> new ItemStack(entity.isOnFire()
                    ? TSDItems.COOKED_SLIME_BEETLE_LEG.get() : TSDItems.SLIME_BEETLE_LEG.get());
            case "fire_beetle" -> new ItemStack(entity.isOnFire()
                    ? TSDItems.COOKED_FIRE_BEETLE_LEG.get() : TSDItems.FIRE_BEETLE_LEG.get());
            default -> ItemStack.EMPTY;
        };
        if (!leg.isEmpty()) {
            if ("helmet_crab".equals(id.getPath())) {
                event.getDrops().removeIf(drop -> drop.getItem().is(net.minecraft.world.item.Items.COD)
                        || drop.getItem().is(net.minecraft.world.item.Items.SALMON)
                        || drop.getItem().is(net.minecraft.world.item.Items.TROPICAL_FISH)
                        || drop.getItem().is(net.minecraft.world.item.Items.PUFFERFISH));
            }
            leg.setCount(2 + entity.getRandom().nextInt(5) + getLootingLevel(event));
            addDrop(event, leg);
            if (isKnifeKill(event)) addDrop(event, leg.copyWithCount(2));
        }

        if (isKnifeKill(event)) {
            switch (id.getPath()) {
                case "fire_beetle" -> addDrop(event, new ItemStack(TSDItems.FIRE_BEETLE_FLAME_SAC.get()));
                case "slime_beetle" -> addDrop(event, new ItemStack(TSDItems.SLIME_BEETLE_HONEY_GLAND.get()));
                case "helmet_crab" -> {
                    event.getDrops().clear();
                    addDrop(event, new ItemStack(TSDItems.HERMIT_CRAB.get()));
                }
                case "redcap", "redcap_sapper" -> addDrop(event, new ItemStack(TSDItems.REDCAP_SPICE.get()));
                case "death_tome" -> addDrop(event, new ItemStack(TFItems.TRANSFORMATION_POWDER.get()));
                default -> {
                }
            }
        }

        switch (id.getPath()) {
            case "snow_queen" -> addDrop(event, new ItemStack(TSDItems.GELID_CRYSTAL.get(),
                    6 + entity.getRandom().nextInt(5) + getLootingLevel(event)));
            case "minoshroom" -> addDrop(event, new ItemStack(TSDItems.LABYRINTH_MUSHROOM.get(),
                    6 + entity.getRandom().nextInt(5) + getLootingLevel(event)));
            case "knight_phantom" -> addDrop(event, new ItemStack(TSDItems.EXPERIMENT_PROTOTYPE.get(),
                    1 + entity.getRandom().nextInt(2) + getLootingLevel(event)));
            case "quest_ram" -> {
                if (entity.isOnFire()) {
                    addDrop(event, new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON.get(), 32));
                    addDrop(event, new ItemStack(TSDItems.QUEST_RAM_CHEESE.get(), 10));
                    awardNearby((ServerLevel) entity.level(), entity, "burning_ram_lord");
                    if (event.getSource().getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                        TSDTriggers.BURNING_RAM_LORD.get().trigger(player);
                    }
                }
            }
            default -> {
            }
        }
    }

    private static boolean isKnifeKill(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return false;
        }
        return isKnife(player.getMainHandItem());
    }

    public static boolean isKnife(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(COMMON_KNIFE_TAG) || stack.is(FARMERS_KNIFE_TAG)) {
            return true;
        }
        return stack.getItem() instanceof vectorwing.farmersdelight.common.item.KnifeItem;
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        Level level = event.getEntity().level();
        event.getDrops().add(new ItemEntity(level, event.getEntity().getX(), event.getEntity().getY(),
                event.getEntity().getZ(), stack));
    }

    private static void replaceDrop(LivingDropsEvent event,
                                    net.minecraft.world.item.Item original,
                                    net.minecraft.world.item.Item replacement) {
        for (ItemEntity drop : event.getDrops()) {
            if (drop.getItem().is(original)) {
                drop.setItem(new ItemStack(replacement, drop.getItem().getCount()));
            }
        }
    }

    private static int getLootingLevel(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return 0;
        }

        HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> enchantments =
                player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<net.minecraft.world.item.enchantment.Enchantment> looting =
                enchantments.getOrThrow(Enchantments.LOOTING);
        return EnchantmentHelper.getItemEnchantmentLevel(looting, player.getMainHandItem());
    }

    private static void awardNearby(ServerLevel level, Entity entity, String advancementId) {
        AdvancementHolder advancement = level.getServer().getAdvancements().get(
                ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", advancementId));
        if (advancement == null) {
            return;
        }
        for (Player player : level.players()) {
            if (player.getBoundingBox().intersects(entity.getBoundingBox().inflate(16.0D))
                    && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.getAdvancements().award(advancement, advancementId);
            }
        }
    }
}
