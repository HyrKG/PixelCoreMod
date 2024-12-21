package cn.hyrkg.pixelgame.core;

import java.io.File;

import cn.hyrkg.pixelgame.core.lib.LibPixelCoreMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibPixelCoreMod.MODULE_ID, name = LibPixelCoreMod.MODULE_NAME, version = LibPixelCoreMod.MODULE_VERSION)
public class PixelCoreMod {

	// 配置文件夹（config）
	private static File dirConfig = null;

	@SidedProxy(clientSide = "cn.hyrkg.pixelgame.core.ClientProxy", serverSide = "cn.hyrkg.pixelgame.core.CommonProxy")
	public static CommonProxy proxy = null;
	@Mod.Instance(LibPixelCoreMod.MODULE_NAME)
	public static PixelCoreMod INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		dirConfig = event.getModConfigurationDirectory();
		proxy.onPreInit();
	}

	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		proxy.onInit();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.onPostInit();
	}

	public static File getConfigDir() {
		return dirConfig;
	}

}
