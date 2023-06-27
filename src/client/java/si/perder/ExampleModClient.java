package si.perder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.x150.renderer.event.RenderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.ActionResult;

public class ExampleModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
	
	static Handler h = new Handler();
	
    @Override
    public void onInitializeClient() {
        RenderEvents.WORLD.register(h::world);
        OnDamagedEvent.EVENT.register((livingEntity, damageSource) -> {
        	LOGGER.info("onDamaged");
        	return ActionResult.PASS;
        });
    }
}