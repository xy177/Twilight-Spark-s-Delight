package xy177.twilightsparksdelight.common.potion;

import net.minecraft.util.ResourceLocation;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public class PotionCharge extends TSDPotionBase
{
    public PotionCharge(boolean badEffect, int color)
    {
        super(badEffect, color, new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/effects/charge.png"));
    }
}
