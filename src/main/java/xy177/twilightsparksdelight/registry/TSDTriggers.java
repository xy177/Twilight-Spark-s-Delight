package xy177.twilightsparksdelight.registry;

import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;

/**
 * Legacy advancement trigger ids kept as real 1.21 trigger types.
 *
 * The runtime handlers still award the matching advancement criterion directly;
 * these simple triggers make the data pack loadable and leave room for more
 * precise predicate payloads later.
 */
public final class TSDTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, TwilightSparksDelight.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> TIME_TO_EVEN_THE_SCALES =
            register("time_to_even_the_scales");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> REDCAP_SPICE =
            register("redcap_spice");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> GATHERED_AROUND =
            register("gathered_around");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> RUTHLESS_IRON_MOUTH =
            register("ruthless_iron_mouth");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> EXECUTIVE_CHEF =
            register("executive_chef");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> MY_FALSE_TEETH =
            register("my_false_teeth");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> SLIME_BEETLE_GLAND =
            register("slime_beetle_gland");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> FIRE_BEETLE_SAC =
            register("fire_beetle_sac");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> MILLION_POUND_MEAL =
            register("million_pound_meal");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> MILK_QUEST_RAM =
            register("milk_quest_ram");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> QUIETLY_WRIGGLING =
            register("quietly_wriggling");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> MOSS_SPREAD =
            register("moss_spread");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> DONT_EAT_ME =
            register("dont_eat_me");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> BURNING_RAM_LORD =
            register("burning_ram_lord");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> GUNFIRE_BREAKS_RABBIT_WATCH =
            register("gunfire_breaks_rabbit_watch");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> FONDUE_FOREIGN_STYLE =
            register("fondue_foreign_style");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> SELF_CONTAINED_COOKWARE =
            register("self_contained_cookware");
    public static final DeferredHolder<CriterionTrigger<?>, TSDTrigger> LATE_FOR_DATE =
            register("late_for_date");

    private TSDTriggers() {
    }

    private static DeferredHolder<CriterionTrigger<?>, TSDTrigger> register(String id) {
        return TRIGGERS.register(id, TSDTrigger::new);
    }

    public static final class TSDTrigger extends SimpleCriterionTrigger<TSDTrigger.Instance> {
        @Override
        public com.mojang.serialization.Codec<Instance> codec() {
            return Instance.CODEC;
        }

        public void trigger(net.minecraft.server.level.ServerPlayer player) {
            trigger(player, ignored -> true);
        }

        public record Instance(java.util.Optional<net.minecraft.advancements.critereon.ContextAwarePredicate> player)
                implements SimpleInstance {
            public static final com.mojang.serialization.Codec<Instance> CODEC =
                    com.mojang.serialization.codecs.RecordCodecBuilder.create(instance -> instance.group(
                            net.minecraft.advancements.critereon.EntityPredicate.ADVANCEMENT_CODEC
                                    .optionalFieldOf("player")
                                    .forGetter(Instance::player)
                    ).apply(instance, Instance::new));
        }
    }
}
