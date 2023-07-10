package si.perder;

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
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Board {
	public static final Logger LOGGER = LoggerFactory.getLogger("mod00");
	public static final VertexFormat ddVertexFormat = DefaultVertexFormat.POSITION_TEX;
	public static final AABB posBox = new AABB(4, 81, -7, 5, 82, -7);

	public DrawData dd;

	private record DrawData(VertexBuffer vertexBuffer, ResourceLocation idImageTexture) {
	}

	@SneakyThrows
	public Board() {
		dd = new DrawData(DrawerHelper.vboQuadXYCreate(posBox, ddVertexFormat),
				DrawerHelper.resource2image(getClass(), "/abc.png"));
	}

	@SneakyThrows
	public void draw(DrawData dd, PoseStack stack) {
		RenderSystem.enableDepthTest();

		Vec3 origin = new Vec3(0, 0, 0);

		Vec3 o = DrawerHelper.transformVec3d(origin);
		val projectionMatrix = RenderSystem.getProjectionMatrix();
		Matrix4f m4f = new Matrix4f(stack.last().pose());
		m4f.translate((float) o.x, (float) o.y, (float) o.z);

		try (val r = new SetupRender()) {
			ShaderInstance shader = GameRenderer.getPositionTexShader();

			RenderSystem.setShaderTexture(0, dd.idImageTexture);

			try (val vb = new VertexBufferBind(dd.vertexBuffer)) {
				vb.drawWithShader(m4f, projectionMatrix, shader);
			}
		}
	}
}
