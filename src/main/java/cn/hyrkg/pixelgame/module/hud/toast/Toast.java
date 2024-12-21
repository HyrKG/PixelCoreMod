package cn.hyrkg.pixelgame.module.hud.toast;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGui;
import cn.hyrkg.pixelgame.module.hud.IHudComponent;
import net.minecraft.client.Minecraft;

public abstract class Toast {
	public void update() {
	}

	public abstract String getText();

	public abstract void draw(FastGui gui, float alpha);
}
