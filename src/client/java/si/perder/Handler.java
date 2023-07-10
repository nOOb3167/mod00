package si.perder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.vertex.PoseStack;

public class Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");

	Board board;
	Drawer drawer = new Drawer();

	public void world(PoseStack stack) {
		if (board == null)
			board = new Board();
		board.draw(board.dd, stack);
//		if (drawer.init())
//			drawer.run(stack);
	}
}
