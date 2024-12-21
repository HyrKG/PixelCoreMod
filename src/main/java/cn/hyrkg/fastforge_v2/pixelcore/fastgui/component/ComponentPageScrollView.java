package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.Consumer;

import org.lwjgl.input.Mouse;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.PageHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;

public class ComponentPageScrollView extends BaseComponent {

	private final PageHelper<?> page;

	private float distance = 0;
	private boolean horizontal = false;
	private ReadyTex btnTex = null;

	public boolean dragging = false;
	public Consumer<ComponentPageScrollView> onPageUpdate = null;

	public ComponentPageScrollView(int x, int y, int w, int h, PageHelper<?> pageHelper) {
		this.transformSolution = new TransformSolution(x, y, w, h);
		this.page = pageHelper;
	}

	public ComponentPageScrollView setOnPageUpdate(Consumer<ComponentPageScrollView> onPageUpdate) {
		this.onPageUpdate = onPageUpdate;
		return this;
	}

	public ComponentPageScrollView btn(ReadyTex readyTex) {
		btnTex = readyTex;
		return this;
	}

	public ComponentPageScrollView horizontal() {
		horizontal = true;
		this.distance = getTransformSolution().width - btnTex.width;
		return this;
	}

	public ComponentPageScrollView vertical() {
		horizontal = false;
		this.distance = getTransformSolution().height - btnTex.height;
		return this;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (dragging || isHover()) {
			gui.drawRect(getTransformSolution().width, getTransformSolution().height, 1694498815);
		}

		float pageProgress = page.getProgress();

		if (horizontal)
			gui.push("scorllbar_offset").translate(distance * pageProgress, 0);
		else
			gui.push("scorllbar_offset").translate(0, distance * pageProgress);

		gui.bind(btnTex);
		gui.drawReadyTex();

	}

	@Override
	public void onTick() {
		super.onTick();

		if (dragging) {
			int mouseY = fastGuiHandler.getLastMouseY();

			TransformSolution trans = getTransformSolution();

			double[] transformOffset = getTransformFinalOffset();

			double translatedY = transformOffset[1];
			double scaledHeight = trans.height * transformOffset[3];

			float result = 0;
			if (mouseY < translatedY) {
				result = 0;
			} else if (mouseY > (translatedY + scaledHeight)) {
				result = 1;
			} else {
				double offset = translatedY - mouseY;
				result = -(float) (offset / scaledHeight);
			}

			int targetPage = (int) (page.getPageMax() * (result)) + 1;
			if (targetPage >= page.getPageMax())
				targetPage = page.getPageMax();
			if (targetPage != page.getPageNow()) {
				page.setPage(targetPage);
			}
		}
	}

	@Override
	public void onMouseInput() {
		if (Mouse.getEventButton() == 0) {
			if (Mouse.getEventButtonState() && isHover()) {
				dragging = true;
			} else if (dragging = true) {
				dragging = false;
				if (onPageUpdate != null)
					onPageUpdate.accept(this);
			}
		}

		super.onMouseInput();
	}

	@Override
	public void onMouseClick(int mouseButton) {
		super.onMouseClick(mouseButton);

	}

}
