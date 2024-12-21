package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.List;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.LibColor;
import net.minecraft.client.renderer.GlStateManager;

public class ComponentButtonTextable extends ComponentButton {

	private String text = "empty!";
	private boolean leftTop = true;

	public ComponentButtonTextable(int id, int x, int y, int width, int height) {
		super(id, x, y, width, height);

	}

	public ComponentButtonTextable rightDown() {
		leftTop = false;
		return this;
	}

	public ComponentButtonTextable setText(String text) {
		this.text = text;
		return this;
	}

	public String getText() {
		return text;
	}

	boolean isHover = false;

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (!isEnable())
			return;

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);

		isHover = isHover();

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
			if (isHover) {
				gui.drawRect(transformSolution.width, transformSolution.height, GREY_COVER);
			}
		} else {

			if (isHover) {
				gui.bind(texHover);
				gui.drawReadyTex();
			} else {
				if (texEnable != null) {
					gui.bind(texEnable);
					gui.drawReadyTex();
				}
			}
		}

	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {

		if (isHover) {
			if (text == null) {
				return;
			}

			List<String> list = gui.gui.mc.fontRenderer.listFormattedStringToWidth(text, 200);
			gui.gui.drawHoveringText(list, gui.getLastMouseX(), gui.getLastMouseY());
//
//			int length = 0;
//			for (String str : list) {
//				int nl = gui.gui.mc.fontRenderer.getStringWidth(str);
//				if (nl > length) {
//					length = nl;
//				}
//			}
//			int height = (list.size() - 1) * 10;
//
//			GlStateManager.enableDepth();
//			if (leftTop) {
//				gui.pushKeep("after").translate(gui.getLastMouseX() - length, gui.getLastMouseY() - 20, 500)
//						.scale2D(1.2);
//			} else {
//				gui.pushKeep("after").translate(gui.getLastMouseX() + 0, gui.getLastMouseY() + 12, 500).scale2D(1.2);
//
//			}
//			gui.drawRect(-3, -2, length + 6, height + 8 + 4, LibColor.black70);
//			for (String str : list) {
//				gui.drawString(0, 0, str, -1);
//				GL11.glTranslated(0, 10, 0);
//			}
//			gui.pop("after");
//			GlStateManager.disableDepth();
		}
	}

}
