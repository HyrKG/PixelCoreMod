package cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler;

public class FastGuiRegister {

	// Make sure you called this method before use
	public static void onPreInit(String modid) {
		TextureCacheHandler.init(modid);
		TickableHandler.onPreInit();
	}
}
