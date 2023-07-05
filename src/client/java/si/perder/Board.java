package si.perder;

import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Board {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
	public static final VertexFormat ddVertexFormat = VertexFormats.POSITION_TEXTURE;
	public static final Box posBox = new Box(4, 81, -7, 5, 82, -7);

	public DrawData dd;

	private record DrawData(VertexBuffer vertexBuffer, Identifier idImageTexture) {
	}

	@SneakyThrows
	public Board() {
		dd = new DrawData(DrawerHelper.vboQuadXYCreate(posBox, ddVertexFormat),
				DrawerHelper.resource2image(getClass(), "/abc.png"));
	}

	@SneakyThrows
	public void draw(DrawData dd, MatrixStack stack) {
		RenderSystem.enableDepthTest();

		Vec3d origin = new Vec3d(0, 0, 0);

		Vec3d o = DrawerHelper.transformVec3d(origin);
		val projectionMatrix = RenderSystem.getProjectionMatrix();
		Matrix4f m4f = new Matrix4f(stack.peek().getPositionMatrix());
		m4f.translate((float) o.x, (float) o.y, (float) o.z);

		try (val r = new SetupRender()) {
			ShaderProgram shader = GameRenderer.getPositionTexProgram();

			RenderSystem.setShaderTexture(0, dd.idImageTexture);

			try (val vb = new VertexBufferBind(dd.vertexBuffer)) {
				vb.vb.draw(m4f, projectionMatrix, shader);
			}
		}
	}
}
