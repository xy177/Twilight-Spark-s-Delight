package xy177.twilightsparksdelight.common.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class RabbitPocketWatchItem extends net.minecraft.item.Item implements IBauble
{
    @Override
    @Optional.Method(modid = "baubles")
    public BaubleType getBaubleType(ItemStack stack)
    {
        return BaubleType.CHARM;
    }
}
