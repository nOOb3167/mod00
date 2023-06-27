package si.perder;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;

public interface OnDamagedEvent {
    Event<OnDamagedEvent> EVENT = EventFactory.createArrayBacked(OnDamagedEvent.class,
        (listeners) -> (livingEntity, damageSource) -> {
            for (OnDamagedEvent listener : listeners) {
                ActionResult result = listener.onDamaged(livingEntity, damageSource);
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult onDamaged(LivingEntity livingEntity, DamageSource damageSource);
}
