package xy177.twilightsparksdelight.registry;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import xy177.twilightsparksdelight.TwilightSparksDelight;

/** Keeps existing advancement IDs and player predicates on Forge 1.20.1. */
public final class TSDTriggers {
    private static final List<TSDTrigger> TRIGGERS = new ArrayList<>();

    public static final TSDTrigger TIME_TO_EVEN_THE_SCALES =
            register("time_to_even_the_scales");
    public static final TSDTrigger REDCAP_SPICE =
            register("redcap_spice");
    public static final TSDTrigger GATHERED_AROUND =
            register("gathered_around");
    public static final TSDTrigger RUTHLESS_IRON_MOUTH =
            register("ruthless_iron_mouth");
    public static final TSDTrigger EXECUTIVE_CHEF =
            register("executive_chef");
    public static final TSDTrigger MY_FALSE_TEETH =
            register("my_false_teeth");
    public static final TSDTrigger SLIME_BEETLE_GLAND =
            register("slime_beetle_gland");
    public static final TSDTrigger FIRE_BEETLE_SAC =
            register("fire_beetle_sac");
    public static final TSDTrigger MILLION_POUND_MEAL =
            register("million_pound_meal");
    public static final TSDTrigger MILK_QUEST_RAM =
            register("milk_quest_ram");
    public static final TSDTrigger QUIETLY_WRIGGLING =
            register("quietly_wriggling");
    public static final TSDTrigger MOSS_SPREAD =
            register("moss_spread");
    public static final TSDTrigger DONT_EAT_ME =
            register("dont_eat_me");
    public static final TSDTrigger BURNING_RAM_LORD =
            register("burning_ram_lord");
    public static final TSDTrigger GUNFIRE_BREAKS_RABBIT_WATCH =
            register("gunfire_breaks_rabbit_watch");
    public static final TSDTrigger FONDUE_FOREIGN_STYLE =
            register("fondue_foreign_style");
    public static final TSDTrigger SELF_CONTAINED_COOKWARE =
            register("self_contained_cookware");
    public static final TSDTrigger LATE_FOR_DATE =
            register("late_for_date");

    private TSDTriggers() {
    }

    private static TSDTrigger register(String id) {
        TSDTrigger trigger = new TSDTrigger(TwilightSparksDelight.id(id));
        TRIGGERS.add(trigger);
        return trigger;
    }

    public static void register() {
        TRIGGERS.forEach(CriteriaTriggers::register);
    }

    public static final class TSDTrigger extends SimpleCriterionTrigger<TSDTrigger.Instance> {
        private final ResourceLocation id;

        private TSDTrigger(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        protected Instance createInstance(JsonObject json, ContextAwarePredicate player,
                                          DeserializationContext context) {
            return new Instance(id, player);
        }

        public void trigger(net.minecraft.server.level.ServerPlayer player) {
            trigger(player, ignored -> true);
        }

        public static final class Instance extends AbstractCriterionTriggerInstance {
            private Instance(ResourceLocation id, ContextAwarePredicate player) {
                super(id, player);
            }
        }
    }
}
