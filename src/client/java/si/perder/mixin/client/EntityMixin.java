package si.perder.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import si.perder.OnDamagedEvent;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "handleDamageEvent", at = @At("HEAD"), cancellable = true)
	private void onDamaged(DamageSource damageSource, final CallbackInfo info) {
		if (!(((Object) this) instanceof LivingEntity))
			return;

		InteractionResult result = OnDamagedEvent.EVENT.invoker().onDamaged(((LivingEntity) (Object) this), damageSource);

		if (result == InteractionResult.FAIL) {
			info.cancel();
		}
	}
}
