package si.perder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
	
    Drawer drawer = new Drawer();
        
    public void world(MatrixStack stack) {
        if (drawer.init())
        	drawer.run(stack);        
    }
    
    public void tick(MinecraftClient client) {
    	ClientPlayerEntity player = client.player;
    	if (player != null) {
	    	val pos = player.getBlockPos();
	    	val dogebox = new Box(new BlockPos(4+5, 81+5, 1+5), new BlockPos(4-5, 81-5, 1-5));
	    	val contains = dogebox.contains(pos.getX(), pos.getY(), pos.getZ());
	    	LOGGER.info(String.format("p %s %s %s %s", pos.getX(), pos.getY(), pos.getZ(), contains));
	    	if (contains) {
	    		//val dmgs = client.world.getDamageSources();
	    		//player.damage(dmgs.onFire(), 1);
	    		//livingentity damage(this.getDamageSources().genericKill(), Float.MAX_VALUE)
	    		player.damage(player.getDamageSources().genericKill(), Float.MAX_VALUE);
	    	}
    	}
    }
}
