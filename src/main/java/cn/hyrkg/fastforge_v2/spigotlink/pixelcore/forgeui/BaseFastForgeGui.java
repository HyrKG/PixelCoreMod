package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.util.UUID;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.BaseFastGuiScreen;
import net.minecraft.client.Minecraft;

public abstract class BaseFastForgeGui extends BaseFastGuiScreen implements IForgeGui {

	private final UUID uuid;
	protected SharedProperty sharedProperty;
	private boolean directClose = false;

	public BaseFastForgeGui(UUID uuidIn) {
		sharedProperty = new SharedProperty();
		this.uuid = uuidIn;
	}

	@Override
	public void synProperty(JsonObject obj) {
		sharedProperty.synProperty(obj);
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public void closeGuiSilently() {
		directClose = true;
		Minecraft.getMinecraft().displayGuiScreen(null);
		if (this.mc.currentScreen == null) {
			this.mc.setIngameFocus();
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (!directClose)
			closeForgeGui(this);
	}

	public SharedProperty getSharedProperty() {
		return sharedProperty;
	}

}
