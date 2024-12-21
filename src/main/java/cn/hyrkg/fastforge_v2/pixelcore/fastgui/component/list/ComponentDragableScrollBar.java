package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list;

import java.util.function.Consumer;

import org.lwjgl.input.Mouse;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;

public class ComponentDragableScrollBar extends BaseComponent {
	public static final int GREY_COVER = 1694498815;

	protected boolean vertical = true;
	protected boolean dragging = false;
	public Consumer<Float> onScroll = null;
	public float lastProgress = -1;

	public ComponentDragableScrollBar(int x, int y, int width, int height) {
		transformSolution = TransformSolution.of(x, y, width, height);
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public ComponentDragableScrollBar setOnPageUpdate(Consumer<Float> onPageUpdate) {
		this.onScroll = onPageUpdate;
		return this;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (isHover() || dragging) {
			gui.drawRect(transformSolution.width, transformSolution.height, GREY_COVER);
		}
	}

	@Override
	public void onMouseInput() {
		if (Mouse.getEventButton() == 0) {
			if (Mouse.getEventButtonState() && isHover()) {
				dragging = true;
			} else if (dragging = true) {
				dragging = false;

			}
		}
		super.onMouseInput();
	}

	@Override
	public void onTick() {
		super.onTick();
		if (dragging) {

			TransformSolution trans = getTransformSolution();
			double[] transformOffset = getTransformFinalOffset();

			int mouse = 0;
			double translated = 0;
			double scaledWidth = 0;

			if (vertical) {
				mouse = fastGuiHandler.getLastMouseY();
				translated = transformOffset[1];
				scaledWidth = trans.height * transformOffset[3];

			} else {
				mouse = fastGuiHandler.getLastMouseX();
				translated = transformOffset[0];
				scaledWidth = trans.width * transformOffset[2];
			}

			float result = 0;

			if (mouse < translated) {
				result = 0;
			} else if (mouse > (translated + scaledWidth)) {
				result = 1;
			} else {
				double offset = translated - mouse;
				result = -(float) (offset / scaledWidth);
			}

			if (result != lastProgress) {
				lastProgress = result;
				if (onScroll != null)
					onScroll.accept(result);
			}
		} else {
			lastProgress = -1;
		}
	}
}
