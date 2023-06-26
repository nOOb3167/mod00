package si.perder;

import me.x150.renderer.event.RenderEvents;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModClient implements ClientModInitializer {
	static Handler h = new Handler();
	
    @Override
    public void onInitializeClient() {
        RenderEvents.WORLD.register(h::world);
    }
}