package si.perder;

import me.x150.renderer.event.RenderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ExampleModClient implements ClientModInitializer {
	static Handler h = new Handler();
	
    @Override
    public void onInitializeClient() {
        RenderEvents.WORLD.register(h::world);
        
        ClientTickEvents.START_CLIENT_TICK.register((client -> h.tick(client)));
    }
}