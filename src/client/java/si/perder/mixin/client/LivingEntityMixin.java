package si.perder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import si.perder.OnDamagedEvent;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onDamaged", at = @At("HEAD"), cancellable = true)
    private void onDamaged(DamageSource damageSource, final CallbackInfo info) {
    	ActionResult result = OnDamagedEvent.EVENT.invoker().onDamaged(((LivingEntity)(Object)this), damageSource);
    	
    	if (result == ActionResult.FAIL) {
    		info.cancel();
    	}
    }
}
