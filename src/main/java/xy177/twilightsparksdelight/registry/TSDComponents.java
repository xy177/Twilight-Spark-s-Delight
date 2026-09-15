package xy177.twilightsparksdelight.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TwilightSparksDelight.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> NAGA_INGREDIENT =
            COMPONENTS.registerComponentType("naga_ingredient", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> COOKED_ADVANCEMENT =
            COMPONENTS.registerComponentType("cooked_advancement", builder -> builder.persistent(Codec.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EXPERIMENT_LEVEL =
            COMPONENTS.registerComponentType("experiment_level", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> EXPERIMENT_ACTIVITY =
            COMPONENTS.registerComponentType("experiment_activity", builder -> builder.persistent(Codec.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> EXPERIMENT_BOUND_MEAT =
            COMPONENTS.registerComponentType("experiment_bound_meat", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EXPERIMENT_REVIVES =
            COMPONENTS.registerComponentType("experiment_revives", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COMPANION_USES =
            COMPONENTS.registerComponentType("companion_uses", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HERMIT_CRAB_BITES =
            COMPONENTS.registerComponentType("hermit_crab_bites", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> COMPANION_CHEF =
            COMPONENTS.registerComponentType("companion_chef", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<java.util.List<String>>> COMPANION_DINERS =
            COMPONENTS.registerComponentType("companion_diners",
                    builder -> builder.persistent(Codec.STRING.listOf()));

    private TSDComponents() {
    }
}
