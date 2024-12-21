package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.Supplier;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;

public class ComponentPanel extends BaseComponent {

	public ComponentPanel(int x, int y, int width, int height) {
		this.transformSolution = TransformSolution.of(x, y, width, height);
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {

	}

	public IComponent setHoverPrecondition(Supplier<Boolean> hoverPrecondition) {
		this.hoverPrecondition = hoverPrecondition;
		return this;
	}

}
