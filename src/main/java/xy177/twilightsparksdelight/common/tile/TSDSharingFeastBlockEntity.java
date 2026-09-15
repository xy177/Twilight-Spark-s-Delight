package xy177.twilightsparksdelight.common.tile;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

/**
 * Persists the chef and distinct diners for a staged feast that does not use
 * an item-based serving tool.
 */
public final class TSDSharingFeastBlockEntity extends BlockEntity {
    private static final String CHEF = "Chef";
    private static final String DINERS = "Diners";
    private UUID chef;
    private final Set<UUID> diners = new LinkedHashSet<>();

    public TSDSharingFeastBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.SHARING_FEAST.get(), pos, state);
    }

    public void recordDiner(Player player) {
        if (chef == null) {
            chef = player.getUUID();
        }
        diners.add(player.getUUID());
        setChanged();
    }

    public UUID getChef() {
        return chef;
    }

    public Set<UUID> getDiners() {
        return Set.copyOf(diners);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        chef = tag.hasUUID(CHEF) ? tag.getUUID(CHEF) : null;
        diners.clear();
        ListTag stored = tag.getList(DINERS, 8);
        for (int index = 0; index < stored.size(); index++) {
            try {
                diners.add(UUID.fromString(stored.getString(index)));
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed entries from externally edited save data.
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (chef != null) {
            tag.putUUID(CHEF, chef);
        }
        ListTag stored = new ListTag();
        for (UUID diner : diners) {
            stored.add(StringTag.valueOf(diner.toString()));
        }
        tag.put(DINERS, stored);
    }
}
