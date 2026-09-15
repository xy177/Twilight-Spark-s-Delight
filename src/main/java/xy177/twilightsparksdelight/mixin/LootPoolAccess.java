package xy177.twilightsparksdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

@Mixin(LootPool.class)
public interface LootPoolAccess {
    @Accessor("entries") LootPoolEntryContainer[] tsd$entries();
    @Mutable @Accessor("entries") void tsd$entries(LootPoolEntryContainer[] entries);
}
