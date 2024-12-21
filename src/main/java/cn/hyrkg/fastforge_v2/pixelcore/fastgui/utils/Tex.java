package cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler.TextureCacheHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Tex {

	private static HashMap<String, Tex> cacheTex = new HashMap<>();

	public final String path;
	public ResourceLocation tex = null;
	public final int texWidth, texHeight;

	public Tex(String thePath, int theTexWidth, int theTexHeight) {
		this.path = thePath;
		this.texWidth = theTexWidth;
		this.texHeight = theTexHeight;

	}

	public ResourceLocation getTexture() {
		if (tex == null)
			tex = TextureCacheHandler.getTexture(path);
		return tex;
	}

	public static Tex of(String path, int texWidth, int texHeight) {
		if (!cacheTex.containsKey(path)) {
			Tex tex = new Tex(path, texWidth, texHeight);
			cacheTex.put(path, tex);
			return tex;
		}
		return cacheTex.get(path);
	}

	public static Tex of(ResourceLocation res, int width, int height) {
		if (!cacheTex.containsKey(res.getResourcePath())) {
			Tex tex = new Tex(res.getResourcePath(), width, height);
			tex.tex = res;
			cacheTex.put(res.getResourcePath(), tex);
		}
		return cacheTex.get(res.getResourcePath());
	}

	public static Tex of(String path) {
//		System.out.println("dawwd");
//		cacheTex.clear();
		if (!cacheTex.containsKey(path)) {
			ResourceLocation res = TextureCacheHandler.getTexture(path);
			int width = 100;
			int height = 100;

//			Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(res);
			try {

				InputStream stream = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(res);

//					Tex.class.getClassLoader()
//					.getSystemResourceAsStream("assets/" + ModuleInfo.moduleID + "/" + res.getResourcePath());

				System.out.println("..stream " + stream);
				System.out.println("..path " + res.getResourcePath());

				if (stream == null) {

				}
				if (stream != null) {
					BufferedImage image = ImageIO.read(stream);
					width = image.getWidth();
					height = image.getHeight();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Tex tex = new Tex(path, width, height);
//			tex.tex = res;
			cacheTex.put(path, tex);
			return tex;
		}
		return cacheTex.get(path);
	}
}
