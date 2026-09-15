package xy177.twilightsparksdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure;
import xy177.twilightsparksdelight.common.tile.TSDGiantStoveBlockEntity;

@Mixin(value = AbstractStoveBlockEntity.class, remap = false)
public abstract class AbstractStoveBlockEntityMixin {
    @Redirect(method = "cookAndOutputItems", at = @At(value = "INVOKE",
            target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"))
    private void tsd$surfaceOutput(Level level, ItemStack stack, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ) {
        if ((Object) this instanceof TSDGiantStoveBlockEntity stove) {
            var bounds = TSDGiantKitchenStructure.bounds(stove.getBlockPos(), stove.getBlockState());
            x = bounds.getCenter().x;
            y = bounds.maxY + 0.1D;
            z = bounds.getCenter().z;
        }
        vectorwing.farmersdelight.common.utility.ItemUtils.spawnItemEntity(
                level, stack, x, y, z, velocityX, velocityY, velocityZ);
    }
}
