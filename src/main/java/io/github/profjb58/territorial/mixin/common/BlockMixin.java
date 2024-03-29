package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.misc.access.BlockAccess;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockAccess {

    @Inject(method = "appendProperties", at = @At("HEAD"))
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if(((Object) this).getClass().equals(BeaconBlock.class))
            builder.add(territorial$getAdditionalProperties().get(0));
    }
}
