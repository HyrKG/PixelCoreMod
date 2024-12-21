package cn.hyrkg.pixelgame.config;

import cn.hyrkg.pixelgame.core.lib.LibPixelCoreMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = LibPixelCoreMod.MODULE_ID)
public class PixelGameConfig {
	@Config.LangKey("pixelgame.marker.scale")
	public static double marker_scale = 1;

	@Config.LangKey("pixelgame.dialog.offset")
	public static int dialog_offset = 100;

	@Config.LangKey("pixelgame.dialog.word.scale")
	public static float dialog_words_scale = 1;

	
	@Mod.EventBusSubscriber(modid = LibPixelCoreMod.MODULE_ID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(LibPixelCoreMod.MODULE_ID)) {
				ConfigManager.sync(LibPixelCoreMod.MODULE_ID, Config.Type.INSTANCE);
			}
		}
	}

}
