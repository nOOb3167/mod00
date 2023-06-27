package si.perder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.util.math.MatrixStack;

public class Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
	
    Drawer drawer = new Drawer();
        
    public void world(MatrixStack stack) {
        if (drawer.init())
        	drawer.run(stack);        
    }
}
