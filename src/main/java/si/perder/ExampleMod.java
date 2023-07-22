package si.perder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;

public class ExampleMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
    
    static ResourceLocation identifier;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		
		ServerTickEvents.START_WORLD_TICK.register(serverworld -> {
			long time = serverworld.getLevelData().getGameTime();
			Player player = serverworld.getNearestPlayer(4d, 81d, 1d, 10d, false);
	    	if (player != null) {
		    	val pos = player.blockPosition();
		    	val dogebox = new AABB(new BlockPos(4+5, 81+5, 1+5), new BlockPos(4-5, 81-5, 1-5));
		    	val contains = dogebox.contains(pos.getX(), pos.getY(), pos.getZ());
		    	if (contains && (time % 40 == 0)) {
		    		LOGGER.info(String.format("r %s %s %s %s %s", pos.getX(), pos.getY(), pos.getZ(), contains, time));
		    		player.hurt(player.damageSources().cactus(), 0.1f);
		    	}
	    	}

			val spawn = new Vec3(serverworld.getLevelData().getXSpawn(), serverworld.getLevelData().getYSpawn(), serverworld.getLevelData().getZSpawn());
			val spawnAngle = serverworld.getLevelData().getSpawnAngle();
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			LOGGER.info("JOINED [%s] [%s]".formatted(handler.getPlayer().getName().getString(), handler.getPlayer().getDisplayName().getString()));
		});
	}
}
