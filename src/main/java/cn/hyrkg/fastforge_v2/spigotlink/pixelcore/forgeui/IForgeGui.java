package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.util.UUID;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;

public interface IForgeGui {
	void synProperty(JsonObject obj);

	UUID getUUID();

	default void closeForgeGui(IForgeGui gui) {
		ModForgeGuiHandler.sendCloseNotify(gui);
	}

	default SimpleMsg msg() {
		return SimpleMsg.create(this);
	}

	default void closeGuiSilently() {
		Minecraft.getMinecraft().displayGuiScreen(null);
		if (Minecraft.getMinecraft().currentScreen == null) {
			Minecraft.getMinecraft().setIngameFocus();
		}
	}

	default void onMessage(JsonObject jsonObject) {

	}

}
