package xy177.twilightsparksdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure;
import xy177.twilightsparksdelight.common.tile.TSDGiantStoveBlockEntity;

@Mixin(value = AbstractStoveBlockEntity.class, remap = false)
public abstract class AbstractStoveBlockEntityMixin {
    @ModifyArgs(method = "cookAndOutputItems", at = @At(value = "INVOKE",
            target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"))
    private void tsd$surfaceOutput(Args args) {
        if ((Object) this instanceof TSDGiantStoveBlockEntity stove) {
            var bounds = TSDGiantKitchenStructure.bounds(stove.getBlockPos(), stove.getBlockState());
            args.set(2, bounds.getCenter().x);
            args.set(3, bounds.maxY + 0.1D);
            args.set(4, bounds.getCenter().z);
        }
    }
}
