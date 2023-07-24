package si.perder;

import java.util.LinkedList;
import java.util.Queue;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.x150.renderer.event.RenderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.player.LocalPlayer;
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
		ClientPlayNetworking.registerGlobalReceiver(NetworkDefs.BOARD_PACKET_ID, (client, handler, buf, responseSender) -> {
			LOGGER.info("received [%s]".formatted(buf));
		});

		RenderEvents.WORLD.register(h::world);
		OnDamagedEvent.EVENT.register((livingEntity, damageSource) -> {
			if (livingEntity instanceof LocalPlayer) {
				onDamagedQueue.add(new D(livingEntity.blockPosition()));
				LOGGER.info(String.format("onDamaged %s", livingEntity.getClass()));
			}
			return InteractionResult.PASS;
		});
	}
}