package si.perder;

import java.awt.Color;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.SneakyThrows;
import lombok.val;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Drawer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");

	VertexFormat vmf = VertexFormats.POSITION_TEXTURE;

	VertexBuffer vb;
	VertexBuffer vb2;
	Identifier bi_id;

	@SneakyThrows
	public boolean init() {
		if (vb == null) {
			vb = DrawerHelper.vboQuadXYCreate(new Box(4, 81, -7, 5, 82, -7), vmf);
			vb2 = DrawerHelper.vboBoxCreateUnit(vmf);
		}

		if (bi_id == null) {
			bi_id = DrawerHelper.resource2image(getClass(), "/abc.png");
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

		Vec3d o = DrawerHelper.transformVec3d(origin);
		val projectionMatrix = RenderSystem.getProjectionMatrix();
		Matrix4f m4f = new Matrix4f(stack.peek().getPositionMatrix());
		m4f.translate((float) o.x, (float) o.y, (float) o.z);

		try (val r = new SetupRender()) {
			ShaderProgram shader = GameRenderer.getPositionTexProgram();

			RenderSystem.setShaderTexture(0, bi_id);

			try (val vb = new VertexBufferBind(this.vb)) {
				vb.vb.draw(m4f, projectionMatrix, shader);
			}

			try (val vb2 = new VertexBufferBind(this.vb2)) {
				Vec3d quadCenter = new Vec3d(4.5f, 81.5f, -7);
				Vec3d target = new Vec3d(1, 1, 0).add(quadCenter);
				m4f.mul(DrawerHelper.laserTo(quadCenter, target));
				vb2.vb.draw(m4f, projectionMatrix, shader);
			}
		}
	}
}
