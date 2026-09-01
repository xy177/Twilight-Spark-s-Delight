package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.advancements.CriteriaTriggers;
import com.wdcftgg.farmersdelightlegacy.common.advancement.SimplePlayerTrigger;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDAdvancements
{
    public static final SimplePlayerTrigger FOREST_SWEET = register("forest_sweet");
    public static final SimplePlayerTrigger MILK_QUEST_RAM = register("milk_quest_ram");
    public static final SimplePlayerTrigger BURNING_RAM_LORD = register("burning_ram_lord");
    public static final SimplePlayerTrigger FIRE_BEETLE_SAC = register("fire_beetle_sac");
    public static final SimplePlayerTrigger SLIME_BEETLE_GLAND = register("slime_beetle_gland");
    public static final SimplePlayerTrigger MY_FALSE_TEETH = register("my_false_teeth");
    public static final SimplePlayerTrigger RUTHLESS_IRON_MOUTH = register("ruthless_iron_mouth");
    public static final SimplePlayerTrigger QUIETLY_WRIGGLING = register("quietly_wriggling");
    public static final SimplePlayerTrigger FONDUE_FOREIGN_STYLE = register("fondue_foreign_style");
    public static final SimplePlayerTrigger SELF_CONTAINED_COOKWARE = register("self_contained_cookware");
    public static final SimplePlayerTrigger GATHERED_AROUND = register("gathered_around");
    public static final SimplePlayerTrigger EXECUTIVE_CHEF = register("executive_chef");
    public static final SimplePlayerTrigger MOSS_SPREAD = register("moss_spread");
    public static final SimplePlayerTrigger LATE_FOR_DATE = register("late_for_date");
    public static final SimplePlayerTrigger GUNFIRE_BREAKS_RABBIT_WATCH = register("gunfire_breaks_rabbit_watch");
    public static final SimplePlayerTrigger REDCAP_SPICE = register("redcap_spice");
    public static final SimplePlayerTrigger DONT_EAT_ME = register("dont_eat_me");
    public static final SimplePlayerTrigger MILLION_POUND_MEAL = register("million_pound_meal");
    public static final SimplePlayerTrigger TIME_TO_EVEN_THE_SCALES = register("time_to_even_the_scales");

    private TSDAdvancements()
    {
    }

    public static void init()
    {
    }

    private static SimplePlayerTrigger register(String path)
    {
        return CriteriaTriggers.register(new SimplePlayerTrigger(new ResourceLocation(TwilightSparksDelight.MODID, path)));
    }
}
