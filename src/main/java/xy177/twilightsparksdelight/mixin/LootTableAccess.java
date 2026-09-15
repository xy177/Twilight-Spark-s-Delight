package xy177.twilightsparksdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootPool;

@Mixin(LootTable.class)
public interface LootTableAccess {
    @Accessor("pools") java.util.List<LootPool> tsd$pools();
}
