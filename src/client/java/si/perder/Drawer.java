package si.perder;

import java.awt.Color;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import lombok.SneakyThrows;
import lombok.val;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Drawer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");

	VertexFormat vmf = DefaultVertexFormat.POSITION_TEX;

	VertexBuffer vb;
	VertexBuffer vb2;
	ResourceLocation bi_id;

	@SneakyThrows
	public boolean init() {
		if (vb == null) {
			vb = DrawerHelper.vboQuadXYCreate(new AABB(4, 81, -7, 5, 82, -7), vmf);
			vb2 = DrawerHelper.vboBoxCreateUnit(vmf);
		}

		if (bi_id == null) {
			bi_id = DrawerHelper.resource2image(getClass(), "/abc.png");
		}

		return true;
	}

	@SneakyThrows
	public void run(PoseStack stack) {
		OutlineFramebuffer.useAndDraw(() -> {
			Renderer3d.renderFilled(stack, Color.WHITE, new Vec3(0, 81, 10), new Vec3(5, 5, 5));
		}, 1f, Color.GREEN, Color.BLACK);

		RenderSystem.enableDepthTest();

		Vec3 origin = new Vec3(0, 0, 0);

		Vec3 o = DrawerHelper.transformVec3d(origin);
		val projectionMatrix = RenderSystem.getProjectionMatrix();
		Matrix4f m4f = new Matrix4f(stack.last().pose());
		m4f.translate((float) o.x, (float) o.y, (float) o.z);

		try (val r = new SetupRender()) {
			ShaderInstance shader = GameRenderer.getPositionTexShader();

			RenderSystem.setShaderTexture(0, bi_id);

			try (val vb = new VertexBufferBind(this.vb)) {
				vb.vb.drawWithShader(m4f, projectionMatrix, shader);
			}

			try (val vb2 = new VertexBufferBind(this.vb2)) {
				Vec3 quadCenter = new Vec3(4.5f, 81.5f, -7);
				Vec3 target = new Vec3(1, 1, 0).add(quadCenter);
				m4f.mul(DrawerHelper.laserTo(quadCenter, target));
				vb2.vb.drawWithShader(m4f, projectionMatrix, shader);
			}
		}
	}
}
