package si.perder;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.x150.renderer.event.RenderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;

public class ExampleModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");

	static Handler h = new Handler();
	static Queue<D> onDamagedQueue = new LinkedList<>();

	public record D(BlockPos p) {
	}
	
	@Override
	public void onInitializeClient() {
		RenderEvents.WORLD.register(h::world);
		OnDamagedEvent.EVENT.register((livingEntity, damageSource) -> {
			if (livingEntity instanceof ClientPlayerEntity) {
				onDamagedQueue.add(new D(livingEntity.getBlockPos()));
				LOGGER.info(String.format("onDamaged %s", livingEntity.getClass()));
			}
			return InteractionResult.PASS;
		});
	}
}