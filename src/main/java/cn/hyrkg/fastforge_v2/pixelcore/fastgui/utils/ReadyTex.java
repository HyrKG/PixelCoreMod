package cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils;

public class ReadyTex extends Tex {

	public final int u, v, width, height;

	public ReadyTex(String thePath, int u, int v, int width, int height, int theTexWidth, int theTexHeight) {
		super(thePath, theTexWidth, theTexHeight);
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}

	public static ReadyTex of(String path, int u, int v, int width, int height, int texWidth, int texHeight) {
		ReadyTex tex = new ReadyTex(path, u, v, width, height, texWidth, texHeight);
		return tex;
	}

	public static ReadyTex of(Tex tex, int u, int v, int width, int height) {
		ReadyTex readyTex = new ReadyTex(tex.path, u, v, width, height, tex.texWidth, tex.texHeight);
		return readyTex;

	}
}
