package si.perder;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface OnDamagedEvent {
    Event<OnDamagedEvent> EVENT = EventFactory.createArrayBacked(OnDamagedEvent.class,
        (listeners) -> (livingEntity, damageSource) -> {
            for (OnDamagedEvent listener : listeners) {
                InteractionResult result = listener.onDamaged(livingEntity, damageSource);
 
                if(result != InteractionResult.PASS) {
                    return result;
                }
            }
 
        return InteractionResult.PASS;
    });
 
    InteractionResult onDamaged(LivingEntity livingEntity, DamageSource damageSource);
}
