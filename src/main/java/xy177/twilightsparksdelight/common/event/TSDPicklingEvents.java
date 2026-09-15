package xy177.twilightsparksdelight.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.common.tile.TSDUnripePickledBrackenJarBlockEntity;

public final class TSDPicklingEvents {
    private TSDPicklingEvents() {
    }

    @SubscribeEvent
    public static void onPeacockFanUsed(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (!held.is(TFItems.PEACOCK_FEATHER_FAN.get())
                || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        var player = event.getEntity();
        BlockPos min = player.blockPosition().offset(-4, -4, -4);
        BlockPos max = player.blockPosition().offset(4, 4, 4);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockEntity(pos) instanceof TSDUnripePickledBrackenJarBlockEntity jar) {
                jar.markPeacockFanUsed();
            }
        }
    }
}
