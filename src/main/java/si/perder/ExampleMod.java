package si.perder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.x150.renderer.util.RendererUtils;

public class ExampleMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
    
    static Identifier identifier;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
        try (InputStream in = getClass().getResourceAsStream("/abc.txt");
        		BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
        	LOGGER.info(reader.readLine());

        	Identifier identifier = RendererUtils.randomIdentifier();
        	BufferedImage read1 = ImageIO.read(in);
            RendererUtils.registerBufferedImageTexture(identifier, read1);
            ExampleMod.identifier = identifier;
        } catch (Exception e) {
		}
	}
}
