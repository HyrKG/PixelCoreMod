package cn.hyrkg.pixelgame.module.monsterwave.ui.menu;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.JsonContent;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PropertyShader;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SharedProperty;
import scala.swing.Orientable;

public class PropertyMwMainMenu extends PropertyShader {

	public PropertyMwMainMenu(SharedProperty property) {
		super(property);
	}

	public final JsonContent<Integer> state = cInt("state");

	public int getState() {
		int orginState = state.get();
		if (orginState == -1)
			return 0;
		return orginState;
	}
}
