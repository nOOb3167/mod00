package si.perder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.SneakyThrows;
import lombok.val;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.BufferUtils;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Drawer {

	VertexBuffer vb;
	BufferedImage bi;
	Identifier bi_id;
	
	@SneakyThrows
	public boolean init() {
        if (vb == null) {            
            VertexFormat vmf = VertexFormats.POSITION_TEXTURE;
            BufferBuilder b = Tessellator.getInstance().getBuffer();
            b.begin(VertexFormat.DrawMode.TRIANGLES, vmf);
            
            VertexConsumer v;
            v = b.vertex(4, 81, 1);
            v.color(0x20800000);
            v.texture(0, 0);
            v.next();
            v = b.vertex(4, 82, 1);
            v.color(0x20800000);
            v.texture(0, 1);
            v.next();
            v = b.vertex(5, 82, 1);
            v.color(0x20800000);
            v.texture(1, 1);
            v.next();
            
            vb = BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);
        }
        
        if (bi == null) {
			try (InputStream is = getClass().getResourceAsStream("/abc.png")) {
				bi = ImageIO.read(is);
				bi_id = RendererUtils.randomIdentifier();
				RendererUtils.registerBufferedImageTexture(bi_id, bi);
			}
        }
        
        return true;
	}
	
	@SneakyThrows
	public void run(MatrixStack stack) {
		OutlineFramebuffer.useAndDraw(() -> {
		    Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 81, 10), new Vec3d(5, 5, 5));
		}, 1f, Color.GREEN, Color.BLACK);
        
		Vec3d origin = new Vec3d(0, 0, 0);

		Vec3d o = transformVec3d(origin);
		val projectionMatrix = RenderSystem.getProjectionMatrix();
		val m4f = new Matrix4f(stack.peek().getPositionMatrix());
		m4f.translate((float) o.x, (float) o.y, (float) o.z);
		// m4f.mul(new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1));

		try (val r = new SetupRender()) {
			// RenderSystem.enableCull();
	
			//ShaderProgram shader = GameRenderer.getPositionColorTexProgram();
			ShaderProgram shader = GameRenderer.getPositionTexProgram();

			RenderSystem.setShaderTexture(0, bi_id);
			
			try (val vb = new VertexBufferBind(this.vb)) {
				vb.vb.draw(m4f, projectionMatrix, shader);
			}
		}
	}
	
	class SetupRender implements AutoCloseable {
		public SetupRender() {
			RendererUtils.setupRender();
		}
		
		@Override
		public void close() throws Exception {
			RendererUtils.endRender();
		}
	}
	
	class VertexBufferBind implements AutoCloseable {
		public VertexBuffer vb;
		
		public VertexBufferBind(VertexBuffer vb) {
			this.vb = vb;
			this.vb.bind();
		}
		
		@Override
		public void close() throws Exception {
			VertexBuffer.unbind();
		}
	}
	
    private static Vec3d transformVec3d(Vec3d in) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();
        return in.subtract(camPos);
    }
}
