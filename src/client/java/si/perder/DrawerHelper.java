package si.perder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import lombok.val;
import lombok.experimental.Delegate;
import me.x150.renderer.util.BufferUtils;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DrawerHelper {

	static VertexBuffer vboQuadXYCreate(AABB q, VertexFormat vmf) {
		BufferBuilder b = Tesselator.getInstance().getBuilder();
		b.begin(VertexFormat.Mode.QUADS, vmf);

		VertexConsumer v;
		v = b.vertex(q.minX, q.minY, q.minZ);
		v.uv(0, 0);
		v.endVertex();
		v = b.vertex(q.maxX, q.minY, q.minZ);
		v.uv(1, 0);
		v.endVertex();
		v = b.vertex(q.maxX, q.maxY, q.minZ);
		v.uv(1, 1);
		v.endVertex();
		v = b.vertex(q.minX, q.maxY, q.minZ);
		v.uv(0, 1);
		v.endVertex();

		return BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);
	}

	static VertexBuffer vboBoxCreateUnit(VertexFormat vmf) {
		BufferBuilder b = Tesselator.getInstance().getBuilder();
		b.begin(VertexFormat.Mode.QUADS, vmf);

		//@formatter:off
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
		//@formatter:on
		if (v.length != 3 * 4 * 6)
			throw new RuntimeException("quad length");
		Vec3 u[] = new Vec3[4*6];
		for (int i = 0; i < 4 * 6; i++)
			u[i] = new Vec3(v[3*i+0], v[3*i+1], v[3*i+2]);
		for (int i = 0; i < 6; i++)
			DrawerHelper.oneQuad(b, Arrays.asList(new Vec3[] { u[4*i+0], u[4*i+1], u[4*i+2], u[4*i+3] }));

		return BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);
	}

	static void oneQuad(BufferBuilder b, List<Vec3> q) {
		VertexConsumer v;
		Queue<Vec3> texs = new LinkedList<>();
		texs.add(new Vec3(0, 0, 0));
		texs.add(new Vec3(1, 0, 0));
		texs.add(new Vec3(1, 1, 0));
		texs.add(new Vec3(0, 1, 0));
		for (val x: q) {
			v = b.vertex(x.x, x.y, x.z);
			val t = texs.remove();
			v.uv((float)t.x, (float)t.y);
			v.endVertex();
		}
	}

	static Matrix4f laserTo(Vec3 c, Vec3 p) {
		Matrix4f m4f = new Matrix4f();
		m4f.translate((float)c.x, (float)c.y, (float)c.z);
		Vec3 xydif = p.subtract(c);
		//@formatter:off
		Matrix4f ss = new Matrix4f(
		1, 0, (float)xydif.x, 0,
		0, 1, (float)xydif.y, 0,
		0, 0, 1, 0,
		0, 0, 0, 1);
		//@formatter:on
		ss.transpose();
		m4f.mul(ss);
		m4f.scale(0.25f);
		m4f.translate(0, 0, 1);
		return m4f;
	}

	@SuppressWarnings("resource")
	static Vec3 transformVec3d(Vec3 in) {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 camPos = camera.getPosition();
		return in.subtract(camPos);
	}

	public static ResourceLocation resource2image(Class<?> clazz, String rname) throws IOException {
		try (InputStream is = clazz.getResourceAsStream(rname)) {
			BufferedImage bi = ImageIO.read(is);
			ResourceLocation bi_id = RendererUtils.randomIdentifier();
			RendererUtils.registerBufferedImageTexture(bi_id, bi);
			return bi_id;
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
	@Delegate(excludes = AutoCloseable.class)
	private final VertexBuffer vb;
	
	public VertexBufferBind(VertexBuffer vb) {
		this.vb = vb;
		this.vb.bind();
	}

	@Override
	public void close() throws Exception {
		VertexBuffer.unbind();
	}
}
