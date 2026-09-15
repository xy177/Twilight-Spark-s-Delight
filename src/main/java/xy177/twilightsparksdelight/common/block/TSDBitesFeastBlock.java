package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Stage feast variant whose legacy state key is {@code bites}.
 */
public class TSDBitesFeastBlock extends TSDStageFeastBlock {
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);

    public TSDBitesFeastBlock(Properties properties, int maxStage, Supplier<Item> servingItem,
                              boolean requiresBowl) {
        super(properties, maxStage, servingItem, requiresBowl, 1);
    }

    @Override
    protected IntegerProperty getStateProperty() {
        return BITES;
    }
}
