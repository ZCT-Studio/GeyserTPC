package io.github.wjiangzhi.geyser_tpc.client;

import net.fabricmc.api.ClientModInitializer;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class GeyserTPC implements ClientModInitializer {
	public static KeyMapping.Category Category = KeyMapping.Category.register(Identifier.withDefaultNamespace("geyser_tpc"));
	public static final KeyMapping Tpa_Key = KeyMappingHelper.registerKeyMapping(
			new KeyMapping(
					"geyser_tpc.key.gtpa",
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_BACKSLASH,
					Category
			)
	);

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (Tpa_Key.consumeClick()) {

				if (client.player != null) {
					client.player.connection.sendCommand("gtpa");
				}
			}
		});
	}
}