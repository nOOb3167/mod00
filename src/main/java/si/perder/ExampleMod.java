package si.perder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import me.x150.renderer.util.RendererUtils;

public class ExampleMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
    
    static Identifier identifier;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		
		ServerTickEvents.START_WORLD_TICK.register(serverworld -> {
			PlayerEntity player = serverworld.getClosestPlayer(4d, 81d, 1d, 10d, false);
	    	if (player != null) {
		    	val pos = player.getBlockPos();
		    	val dogebox = new Box(new BlockPos(4+5, 81+5, 1+5), new BlockPos(4-5, 81-5, 1-5));
		    	val contains = dogebox.contains(pos.getX(), pos.getY(), pos.getZ());
		    	LOGGER.info(String.format("r %s %s %s %s", pos.getX(), pos.getY(), pos.getZ(), contains));
		    	if (contains) {
		    		player.damage(player.getDamageSources().genericKill(), Float.MAX_VALUE);
		    	}
	    	}
		});
	}
}
