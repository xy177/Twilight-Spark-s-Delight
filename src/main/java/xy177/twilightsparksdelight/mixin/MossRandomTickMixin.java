package xy177.twilightsparksdelight.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.block.MossPatchBlock;
import xy177.twilightsparksdelight.common.event.TSDMossSpreadEvents;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MossRandomTickMixin {
    @Inject(method = "isRandomlyTicking", at = @At("RETURN"), cancellable = true)
    private void tsd$tickingMoss(CallbackInfoReturnable<Boolean> callback) {
        if (currentBlock() instanceof MossPatchBlock) callback.setReturnValue(true);
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void tsd$spreadMoss(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callback) {
        TSDMossSpreadEvents.onRandomTick(level, pos);
    }

    private BlockState currentState() {
        return (BlockState) (Object) this;
    }

    private net.minecraft.world.level.block.Block currentBlock() {
        return currentState().getBlock();
    }
}
