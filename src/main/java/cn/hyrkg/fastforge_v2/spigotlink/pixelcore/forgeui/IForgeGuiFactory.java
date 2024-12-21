package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.util.UUID;

import com.google.gson.JsonObject;

public interface IForgeGuiFactory {
	IForgeGui createAndDisplay(String gui, UUID uuid, JsonObject property);
}
