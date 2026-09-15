package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.tile.TSDGloryCrucibleBlockEntity;
import xy177.twilightsparksdelight.common.tile.TSDUnripePickledBrackenJarBlockEntity;
import xy177.twilightsparksdelight.common.tile.TSDGiantCookingPotBlockEntity;
import xy177.twilightsparksdelight.common.tile.TSDGiantStoveBlockEntity;
import xy177.twilightsparksdelight.common.tile.TSDSharingFeastBlockEntity;

public final class TSDBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TwilightSparksDelight.MOD_ID);
    public static final RegistryObject<BlockEntityType<xy177.twilightsparksdelight.common.tile.TSDMasonJarBlockEntity>>
            MASON_JAR = BLOCK_ENTITIES.register("mason_jar",
            () -> BlockEntityType.Builder.of(xy177.twilightsparksdelight.common.tile.TSDMasonJarBlockEntity::new,
                    TSDBlocks.MASON_JAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<xy177.twilightsparksdelight.common.tile.TSDDryingRackBlockEntity>>
            DRYING_RACK = BLOCK_ENTITIES.register("drying_rack",
            () -> BlockEntityType.Builder.of(xy177.twilightsparksdelight.common.tile.TSDDryingRackBlockEntity::new,
                    TSDBlocks.DRYING_RACK.get()).build(null));

    public static final RegistryObject<BlockEntityType<TSDGloryCrucibleBlockEntity>>
            GLORY_CRUCIBLE = BLOCK_ENTITIES.register("glory_crucible",
            () -> BlockEntityType.Builder.of(TSDGloryCrucibleBlockEntity::new,
                    TSDBlocks.GLORY_CRUCIBLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TSDUnripePickledBrackenJarBlockEntity>>
            UNRIPE_PICKLED_BRACKEN_JAR = BLOCK_ENTITIES.register("unripe_pickled_bracken_jar",
            () -> BlockEntityType.Builder.of(TSDUnripePickledBrackenJarBlockEntity::new,
                    TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<TSDGiantCookingPotBlockEntity>>
            GIANT_COOKING_POT = BLOCK_ENTITIES.register("giant_cooking_pot",
            () -> BlockEntityType.Builder.of(TSDGiantCookingPotBlockEntity::new,
                    TSDBlocks.GIANT_COOKING_POT.get(), TSDBlocks.GIANTS_COOKING_POT.get()).build(null));

    public static final RegistryObject<BlockEntityType<TSDGiantStoveBlockEntity>>
            GIANT_STOVE = BLOCK_ENTITIES.register("giant_stove",
            () -> BlockEntityType.Builder.of(TSDGiantStoveBlockEntity::new,
                    TSDBlocks.GIANT_STOVE.get(), TSDBlocks.GIANTS_STOVE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TSDSharingFeastBlockEntity>>
            SHARING_FEAST = BLOCK_ENTITIES.register("sharing_feast",
            () -> BlockEntityType.Builder.of(TSDSharingFeastBlockEntity::new,
                    TSDBlocks.SALT_HELMET_CRAB.get()).build(null));

    public static final RegistryObject<BlockEntityType<xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity>>
            LARGE_FEAST = BLOCK_ENTITIES.register("large_feast",
            () -> BlockEntityType.Builder.of(
                    xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity::new,
                    TSDBlocks.NAGA_MIXED_RICE.get(), TSDBlocks.TWILIGHT_BOAR_KNUCKLE.get()).build(null));

    private TSDBlockEntities() {
    }
}
