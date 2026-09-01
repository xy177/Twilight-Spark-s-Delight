package xy177.twilightsparksdelight.common.potion;

import net.minecraft.util.ResourceLocation;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public class PotionSymbiosis extends TSDPotionBase
{
    public PotionSymbiosis(boolean badEffect, int color)
    {
        super(badEffect, color, new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/effects/symbiosis.png"));
    }
}
