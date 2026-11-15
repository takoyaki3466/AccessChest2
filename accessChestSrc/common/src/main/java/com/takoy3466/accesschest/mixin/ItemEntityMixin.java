package com.takoy3466.accesschest.mixin;

import com.takoy3466.accesschest.transfer.AutoCollectHandler;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Hooks auto collect into item pickup on every loader. Injected at every return so that it also runs when a loader's
 * patched pickup code returns early.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    @Nullable
    private UUID target;

    @Inject(method = "playerTouch", at = @At("RETURN"))
    private void accesschest$autoCollect(Player player, CallbackInfo ci) {
        AutoCollectHandler.afterPlayerTouch((ItemEntity) (Object) this, player, target);
    }
}
