package cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

public class TextureCacheHandler {
	private static String modid;
	private static HashMap<String, ResourceLocation> resourceCache = new HashMap<>();

	public static void init(String modid) {
		TextureCacheHandler.modid = modid;
	}

	public static ResourceLocation getTexture(String path) {
		if (!resourceCache.containsKey(path)) {
			String trimedPath = path.trim();
			ResourceLocation res = new ResourceLocation(modid, "textures/" + path + ".png");
			resourceCache.put(path, res);
		}
		return resourceCache.get(path);
	}
}
