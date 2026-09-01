package xy177.twilightsparksdelight.common.event;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDItems;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDLootTableEvents
{
    private TSDLootTableEvents()
    {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        String name = event.getName().toString();
        if ("twilightforest:structures/darktower_boss/darktower_boss".equals(name)) {
            addExperiment234Pool(event);
            return;
        }
        if (!"twilightforest:structures/hill_1/ultrarare".equals(name)
            && !"twilightforest:structures/hill_2/ultrarare".equals(name)
            && !"twilightforest:structures/hill_3/ultrarare".equals(name)) {
            return;
        }

        LootPool pool = event.getTable().getPool("main");
        if (pool == null || pool.getEntry("twilight_spark_delight:rabbit_pocket_watch") != null) {
            return;
        }

        int weight = "twilightforest:structures/hill_3/ultrarare".equals(name) ? 5 : 1;
        pool.addEntry(new LootEntryItem(
            TSDItems.RABBIT_POCKET_WATCH,
            weight,
            0,
            new LootFunction[0],
            new LootCondition[0],
            "twilight_spark_delight:rabbit_pocket_watch"
        ));
    }

    private static void addExperiment234Pool(LootTableLoadEvent event)
    {
        String poolName = "twilight_spark_delight:experiment_234";
        if (event.getTable().getPool(poolName) != null) {
            return;
        }
        LootFunction setCount = new SetCount(new LootCondition[0], new RandomValueRange(5.0F, 8.0F));
        LootEntry entry = new LootEntryItem(
            TSDItems.EXPERIMENT_234,
            1,
            0,
            new LootFunction[] {setCount},
            new LootCondition[0],
            poolName
        );
        event.getTable().addPool(new LootPool(
            new LootEntry[] {entry},
            new LootCondition[0],
            new RandomValueRange(1.0F),
            new RandomValueRange(0.0F),
            poolName
        ));
    }
}
