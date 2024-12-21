package cn.hyrkg.pixelgame.module.hud;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGui;
import net.minecraft.client.Minecraft;

public interface IHudComponent {
	void draw(FastGui fastGui);

	void update();
}
