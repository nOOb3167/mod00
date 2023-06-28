package si.perder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;

import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Drawer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
	
	VertexBuffer vb;
	VertexBuffer vb2;
	BufferedImage bi;
	Identifier bi_id;
	
	private static VertexBuffer vboQuadXYCreate(Box q, VertexFormat vmf) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(VertexFormat.DrawMode.QUADS, vmf);
        
        VertexConsumer v;
        v = b.vertex(q.minX, q.minY, q.minZ);
        v.texture(0, 0);
        v.next();
        v = b.vertex(q.maxX, q.minY, q.minZ);
        v.texture(1, 0);
        v.next();
        v = b.vertex(q.maxX, q.maxY, q.minZ);
        v.texture(1, 1);
        v.next();
        v = b.vertex(q.minX, q.maxY, q.minZ);
        v.texture(0, 1);
        v.next();
        
        return BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);		
	}
	
	private static VertexBuffer vboBoxCreateUnit(VertexFormat vmf) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(VertexFormat.DrawMode.QUADS, vmf);
        
        float v[] = {
        	// fr
        	-1, +1, -1,
        	+1, +1, -1,
        	+1, +1, +1,
        	-1, +1, +1,
        	// bk
        	-1, -1, -1,
        	-1, -1, +1,
        	+1, -1, +1,
        	+1, -1, -1,
        	// up
        	-1, +1, +1,
        	+1, +1, +1,
        	+1, -1, +1,
        	-1, -1, +1,
        	// do
        	+1, +1, -1,
        	-1, +1, -1,
        	-1, -1, -1,
        	+1, -1, -1,
        	// lt
        	-1, -1, -1,
        	-1, +1, -1,
        	-1, +1, +1,
        	-1, -1, +1,
        	// rt
        	+1, +1, -1,
        	+1, -1, -1,
        	+1, -1, +1,
        	+1, +1, +1,
        };
        if (v.length != 3 * 4 * 6)
        	throw new RuntimeException("quad length");
        Vec3d u[] = new Vec3d[4*6];
        for (int i = 0; i < 4 * 6; i++)
        	u[i] = new Vec3d(v[3*i+0], v[3*i+1], v[3*i+2]);
        for (int i = 0; i < 6; i++)
        	oneQuad(b, Arrays.asList(new Vec3d[] { u[4*i+0], u[4*i+1], u[4*i+2], u[4*i+3] }));
        
        return BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);
	}
	
	private static VertexBuffer vboBoxCreate(Vec3d s, Vec3d e, double radius, VertexFormat vmf) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(VertexFormat.DrawMode.QUADS, vmf);
        
        val a = e.subtract(s);
        val up_ = new Vec3d(0, 1, 0);
        val side_ = a.crossProduct(up_).normalize();
        
        val up = up_.multiply(radius);
        val side = side_.multiply(radius);
        
        val sdl = s.add(up.negate()).add(side.negate());
        val sul = s.add(up).add(side.negate());
        val sur = s.add(up).add(side);
        val sdr = s.add(up.negate()).add(side);

        val edl = e.add(up.negate()).add(side.negate());
        val eul = e.add(up).add(side.negate());
        val eur = e.add(up).add(side);
        val edr = e.add(up.negate()).add(side);
        
        val plane_up = Arrays.asList(eul, eur, sur, sul);
        val plane_rt = Arrays.asList(edr, sdr, sur, eur);
        val plane_fr = Arrays.asList(edl, edr, eur, eul);

        List<Vec3d> plane_do = new ArrayList<>(plane_up);
        List<Vec3d> plane_lt = new ArrayList<>(plane_rt);
        List<Vec3d> plane_bk = new ArrayList<>(plane_fr);
        
        Collections.reverse(plane_do);
        Collections.reverse(plane_lt);
        Collections.reverse(plane_bk);

        plane_do = plane_do.stream().map(z -> z.add(up.multiply(-2))).collect(Collectors.toList());
        plane_lt = plane_lt.stream().map(z -> z.add(side.multiply(-2))).collect(Collectors.toList());
        plane_bk = plane_bk.stream().map(z -> z.add(a.multiply(-1))).collect(Collectors.toList());
        
        oneQuad(b, plane_up);
        oneQuad(b, plane_rt);
        oneQuad(b, plane_fr);

        oneQuad(b, plane_do);
        oneQuad(b, plane_lt);
        oneQuad(b, plane_bk);
        
        return BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);
	}
	
	private static void oneQuad(BufferBuilder b, List<Vec3d> q) {
		VertexConsumer v;
		Queue<Vec3d> texs = new LinkedList<>();
		texs.add(new Vec3d(0, 0, 0));
		texs.add(new Vec3d(1, 0, 0));
		texs.add(new Vec3d(1, 1, 0));
		texs.add(new Vec3d(0, 1, 0));
		for (val x: q) {
			v = b.vertex(x.x, x.y, x.z);
			val t = texs.remove();
			v.texture((float)t.x, (float)t.y);
			v.next();
		}
	}
	
	@SneakyThrows
	public boolean init() {
        if (vb == null) {            
            VertexFormat vmf = VertexFormats.POSITION_TEXTURE;
            
            vb = vboQuadXYCreate(new Box(4, 81, 1, 5, 82, 1), vmf);
            //vb2 = vboBoxCreate(new Vec3d(5, 81, 3), new Vec3d(10, 81, 3), 2, vmf);
            vb2 = vboBoxCreateUnit(vmf);
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
        
		RenderSystem.enableDepthTest();
		
		Vec3d origin = new Vec3d(0, 0, 0);

		Vec3d o = transformVec3d(origin);
		val projectionMatrix = RenderSystem.getProjectionMatrix();
		val m4f = new Matrix4f(stack.peek().getPositionMatrix());
		m4f.translate((float) o.x, (float) o.y, (float) o.z);

		try (val r = new SetupRender()) {	
			ShaderProgram shader = GameRenderer.getPositionTexProgram();

			RenderSystem.setShaderTexture(0, bi_id);
			
			try (val vb = new VertexBufferBind(this.vb)) {
				vb.vb.draw(m4f, projectionMatrix, shader);
			}

			try (val vb2 = new VertexBufferBind(this.vb2)) {
				m4f.translate(0, 81, 0);
				vb2.vb.draw(m4f, projectionMatrix, shader);
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
