package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SimpleMsg {

	private final IForgeGui forgeGui;
	private final JsonObject jsonObj;

	private SimpleMsg(IForgeGui gui) {
		this.forgeGui = gui;
		jsonObj = new JsonObject();
	}

	public SimpleMsg add(String key, Object value) {
		if (value instanceof Number)
			jsonObj.addProperty(key, (Number) value);
		else if (value instanceof Character)
			jsonObj.addProperty(key, (Character) value);
		else if (value instanceof String)
			jsonObj.addProperty(key, (String) value);
		else if (value instanceof Boolean)
			jsonObj.addProperty(key, (boolean) value);
		else if (value instanceof JsonElement) {
			jsonObj.add(key, (JsonElement) value);
		} else {
			return add(key, value.toString());
		}
		return this;
	}

	public void sent() {
		ModForgeGuiHandler.sendMessage(this, forgeGui);
	}

	public JsonObject getJsonObj() {
		return jsonObj;
	}

	public static SimpleMsg create(IForgeGui gui) {
		return new SimpleMsg(gui);
	}
}
