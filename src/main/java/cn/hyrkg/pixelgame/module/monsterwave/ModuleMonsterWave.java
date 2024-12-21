package cn.hyrkg.pixelgame.module.monsterwave;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.ModForgeGuiHandler;
import cn.hyrkg.pixelgame.module.monsterwave.ui.menu.GuiMonsterWaveMainMenu;
import cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc.GuiGameResult;
import cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc.GuiRankReward;
import cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc.GuiStartFail;
import cn.hyrkg.pixelgame.module.monsterwave.ui.rank.GuiMwScoreRank;
import cn.hyrkg.pixelgame.module.monsterwave.ui.rank.GuiMwWinRank;
import cn.hyrkg.pixelgame.network.IJsonPacketHandler;
import cn.hyrkg.pixelgame.network.NetworkPixelCore;

public class ModuleMonsterWave implements IJsonPacketHandler {
	public static HudMonsterWave hud = new HudMonsterWave();

	public static void onPreInit() {
		NetworkPixelCore.registerJsonPacketHandler(new ModuleMonsterWave());

		ModForgeGuiHandler.registerStandardGui(GuiMonsterWaveMainMenu.class, "monsterwave_mainmenu");
		ModForgeGuiHandler.registerStandardGui(GuiGameResult.class, "monsterwave_gameresult");
		ModForgeGuiHandler.registerStandardGui(GuiRankReward.class, "monsterwave_rankreward");
		ModForgeGuiHandler.registerStandardGui(GuiMwScoreRank.class, "monsterwave_scorerank");
		ModForgeGuiHandler.registerStandardGui(GuiMwWinRank.class, "monsterwave_winrank");
		ModForgeGuiHandler.registerStandardGui(GuiStartFail.class, "monsterwave_startfail");

		hud.init();
	}

	@Override
	public void handleServerMessage(JsonObject jsonObject) {
		if (jsonObject.has("hud")) {
			hud.handlePacket(jsonObject.getAsJsonObject("hud"));
		}
	}

	@Override
	public String getKey() {
		return "monster_wave";
	}

}
