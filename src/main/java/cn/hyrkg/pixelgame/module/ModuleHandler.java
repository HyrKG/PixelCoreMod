package cn.hyrkg.pixelgame.module;

import cn.hyrkg.pixelgame.module.dialog.ModuleDialog;
import cn.hyrkg.pixelgame.module.lottery.ModuleLottery;
import cn.hyrkg.pixelgame.module.lottery.csgo.GuiLotteryCsgo;
import cn.hyrkg.pixelgame.module.marker.ModuleMarker;
import cn.hyrkg.pixelgame.module.monsterwave.ModuleMonsterWave;
import cn.hyrkg.pixelgame.module.sec.ModuleSec;

public class ModuleHandler {
	public static void onPreInit(boolean clientSide) {
		if (clientSide) {
			ModuleDialog.onPreInit();
			ModuleMarker.onPreInit();
			ModuleMonsterWave.onPreInit();
			ModuleLottery.onPreInit();
			ModuleSec.onPreInit();
			GuiLotteryCsgo.tex.getTexture(); // 提前加载贴图
		}
	}
}
