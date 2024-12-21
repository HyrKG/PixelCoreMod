package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.Consumer;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import net.minecraft.client.renderer.GlStateManager;

public class ComponentButton extends BaseComponent {

	public static final int GREY_COVER = 1694498815;

	public final int id;
	protected ReadyTex texEnable = null, texHover = null, texSelected = null;

	protected boolean selected = false, enable = true;

	protected boolean drawHoverRect = true;

	protected Consumer<ComponentButton> whenClick = null;

	protected String buttonText = null;

	public ComponentButton(int id, int x, int y, int width, int height) {
		this.id = id;
		this.transformSolution = TransformSolution.of(x, y, width, height);
	}

	public ComponentButton setButtonText(String buttonText) {
		this.buttonText = buttonText;
		return this;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public ComponentButton setDrawHoverRect(boolean drawHoverRect) {
		this.drawHoverRect = drawHoverRect;
		return this;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	public ComponentButton whenClick(Consumer<ComponentButton> clickAction) {
		whenClick = clickAction;
		return this;
	}

	public ComponentButton texEnable(ReadyTex enable) {
		this.texEnable = enable;
		return this;
	}

	public ComponentButton texHover(ReadyTex texHover) {
		this.texHover = texHover;
		return this;
	}

	public ComponentButton texSelected(ReadyTex texSelected) {
		this.texSelected = texSelected;
		return this;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (!isEnable())
			return;
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);

		if (isSelected()) {
			if (texSelected == null) {
				drawHover(gui);
			} else {
				gui.bind(texSelected);
				gui.drawReadyTex();
			}
		} else if (texHover == null) {
			if (texEnable != null) {
				gui.bind(texEnable);
				gui.drawReadyTex();
			}

			if (drawHoverRect && isHover()) {
				gui.drawRect(transformSolution.width, transformSolution.height, GREY_COVER);
			}
		} else {

			if (isHover()) {
				gui.bind(texHover);
				gui.drawReadyTex();
			} else {
				if (texEnable != null) {
					gui.bind(texEnable);
					gui.drawReadyTex();
				}
			}
		}

		if (buttonText != null) {
			double sc = transformSolution.width / 55d;

			gui.push("wordsc").translate(transformSolution.width / 2, transformSolution.height / 2 - sc * 3.5)
					.scale2D(sc);
			gui.drawCenteredString(buttonText, -1);
			gui.pop("wordsc");
		}
	}

	public void drawHover(FastGuiHandler gui) {
		if (texHover == null) {
			if (drawHoverRect)
				gui.drawRect(transformSolution.width, transformSolution.height, GREY_COVER);
		} else {
			gui.bind(texHover);
			gui.drawReadyTex();
		}
	}

	@Override
	public void onMouseClick(int mouseButton) {

		super.onMouseClick(mouseButton);

		if (!isEnable())
			return;

		if (isHover() && whenClick != null) {
			whenClick.accept(this);
		}
	}

}
