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
}
