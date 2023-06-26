package si.perder;

import net.minecraft.client.util.math.MatrixStack;

public class Handler {
    Drawer drawer = new Drawer();
        
    public void world(MatrixStack stack) {
        if (drawer.init())
        	drawer.run(stack);        
    }
}
