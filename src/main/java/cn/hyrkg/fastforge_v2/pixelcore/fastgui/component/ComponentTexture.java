package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;

/**
 * 该组件将会填充一个图片区域
 */
public class ComponentTexture extends BaseComponent {

	public final ReadyTex tex;

	public ComponentTexture(int x, int y, ReadyTex tex) {
		this.tex = tex;
		this.transformSolution = new TransformSolution(x, y, tex.width, tex.height);
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		gui.bind(tex);
		gui.drawReadyTex();
	}

}
