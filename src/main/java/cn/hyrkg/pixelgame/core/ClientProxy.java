package cn.hyrkg.pixelgame.core;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler.TextureCacheHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler.TickableHandler;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.ModForgeGuiHandler;
import cn.hyrkg.pixelgame.core.lib.LibPixelCoreMod;
import cn.hyrkg.pixelgame.handler.CustomGuiHandler;
import cn.hyrkg.pixelgame.handler.KeyHandler;
import cn.hyrkg.pixelgame.module.ModuleHandler;
import cn.hyrkg.pixelgame.module.audio.AudioManager;
import cn.hyrkg.pixelgame.module.hud.ModuleHud;
import cn.hyrkg.pixelgame.module.lottery.ModuleLottery;
import cn.hyrkg.pixelgame.network.NetworkPixelCore;

public class ClientProxy extends CommonProxy {
	@Override
	public void onPreInit() {
		super.onPreInit();
		TextureCacheHandler.init(LibPixelCoreMod.MODULE_ID);

		TickableHandler.onPreInit();
		ModForgeGuiHandler.init(LibPixelCoreMod.INDEX_FORGE_GUI);

		NetworkPixelCore.onInit();
		CustomGuiHandler.onInit();
		ModuleHandler.onPreInit(true);

		KeyHandler.onPreInit();
		ModuleHud.onPreInit();

		AudioManager.init();
	}
}
