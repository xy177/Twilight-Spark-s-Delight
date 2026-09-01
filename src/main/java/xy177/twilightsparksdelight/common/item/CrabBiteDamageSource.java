package xy177.twilightsparksdelight.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class CrabBiteDamageSource extends EntityDamageSource
{
    public CrabBiteDamageSource(Entity source)
    {
        super("twilight_spark_delight.crab_bite", source);
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity)
    {
        String key = entity.world.rand.nextBoolean()
            ? "death.attack.twilight_spark_delight.crab_bite.0"
            : "death.attack.twilight_spark_delight.crab_bite.1";
        return new TextComponentTranslation(key, entity.getDisplayName());
    }
}
