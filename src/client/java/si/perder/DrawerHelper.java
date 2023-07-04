package si.perder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.joml.Matrix4f;

import lombok.val;
import me.x150.renderer.util.BufferUtils;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DrawerHelper {

	static VertexBuffer vboQuadXYCreate(Box q, VertexFormat vmf) {
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

	static VertexBuffer vboBoxCreateUnit(VertexFormat vmf) {
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
			DrawerHelper.oneQuad(b, Arrays.asList(new Vec3d[] { u[4*i+0], u[4*i+1], u[4*i+2], u[4*i+3] }));

		return BufferUtils.createVbo(b.end(), VertexBuffer.Usage.STATIC);
	}

	static void oneQuad(BufferBuilder b, List<Vec3d> q) {
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

	static Matrix4f laserTo(Vec3d c, Vec3d p) {
		Matrix4f m4f = new Matrix4f();
		m4f.translate((float)c.x, (float)c.y, (float)c.z);
		Vec3d xydif = p.subtract(c);
		Matrix4f ss = new Matrix4f(
		1, 0, (float)xydif.x, 0,
		0, 1, (float)xydif.y, 0,
		0, 0, 1, 0,
		0, 0, 0, 1);
		ss.transpose();
		m4f.mul(ss);
		m4f.scale(0.25f);
		m4f.translate(0, 0, 1);
		return m4f;
	}

	@SuppressWarnings("resource")
	static Vec3d transformVec3d(Vec3d in) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d camPos = camera.getPos();
		return in.subtract(camPos);
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
