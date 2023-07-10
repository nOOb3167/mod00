package si.perder.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void autoLoadLevel(CallbackInfo info) {
		Minecraft client = Minecraft.getInstance();

		if (client != null && client.getLevelSource().levelExists("autoload")) {
			//net.minecraft.client.gui.screens.worldselection.WorldSelectionList.WorldListEntry.loadWorld()
			//this.minecraft.createWorldOpenFlows().loadLevel(this.screen, this.summary.getLevelId());
			//net.minecraft.client.gui.screens.worldselection.WorldSelectionList.WorldListEntry.queueLoadScreen()
			GenericDirtMessageScreen screen = new GenericDirtMessageScreen((Component)Component.translatable((String)"selectWorld.data_read"));
			client.forceSetScreen(screen);
			client.createWorldOpenFlows().loadLevel(screen, "autoload");
		}
	}
}
