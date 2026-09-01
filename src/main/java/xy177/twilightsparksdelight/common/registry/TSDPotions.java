package xy177.twilightsparksdelight.common.registry;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.potion.PotionCharge;
import xy177.twilightsparksdelight.common.potion.PotionEnlarge;
import xy177.twilightsparksdelight.common.potion.PotionShrink;
import xy177.twilightsparksdelight.common.potion.PotionSymbiosis;
import xy177.twilightsparksdelight.common.potion.TSDPotionBase;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDPotions
{
    public static Potion CHARGE;
    public static Potion SYMBIOSIS;
    public static Potion SHRINK;
    public static Potion ENLARGE;
    public static Potion ABYSS_CALL;
    public static Potion SORROW;
    public static Potion GRIEF;

    private TSDPotions()
    {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Potion> event)
    {
        CHARGE = register(event, "charge", new PotionCharge(false, 0xC88A2A));
        SYMBIOSIS = register(event, "symbiosis", new PotionSymbiosis(true, 0x945D76));
        SHRINK = register(event, "shrink", new PotionShrink(false, 0x7E5FB7));
        ENLARGE = register(event, "enlarge", new PotionEnlarge(false, 0xB78D3A));
        ABYSS_CALL = register(event, "abyss_call", new TSDPotionBase(false, 0x6D2B83,
            new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/effects/abyss_call.png")) { });
        SORROW = register(event, "sorrow", new TSDPotionBase(false, 0x4D7FB8,
            new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/effects/sorrow.png")) { });
        GRIEF = register(event, "grief", new TSDPotionBase(false, 0x8B3A5A,
            new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/effects/grief.png")) { });
    }

    private static Potion register(RegistryEvent.Register<Potion> event, String name, Potion potion)
    {
        potion.setPotionName(TwilightSparksDelight.MODID + ".effect." + name);
        potion.setRegistryName(TwilightSparksDelight.MODID, name);
        event.getRegistry().register(potion);
        return potion;
    }
}
