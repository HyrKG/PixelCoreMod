package cn.hyrkg.pixelgame.dto.hud;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGui;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.pixelgame.module.hud.toast.Toast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class CardToast extends Toast {
	private Tex tex = Tex.of("hud/misc/rect");

	PositionType position = PositionType.BOTTOM;
	public String icon = null;
	public String text;

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void draw(FastGui gui, float a) {

		// draw background
		gui.pushKeep().offset(gui.width / 2, gui.height * 0.7).scale2D(1.2 * gui.computeScaleHeight());
		gui.glStateInvokeStart();
		int charWidth = gui.mc.fontRenderer.getStringWidth(text);
		if (charWidth % 2 == 1) {
			charWidth += 1;
		}
		int iconWidth = 15;
		int iconMargin = 3;
		int bgPadingY = 5, bgPadingX = 0;
		if (icon == null) {
			iconWidth = 0;
			iconMargin = 0;
			bgPadingX = 0;
			bgPadingX = 0;
		}

		int totalWidth = charWidth + iconWidth + iconMargin;

		int color = new Color(0f, 0f, 0f, 0.7f * a).getRGB();
//		int color = new Color(0f, 0f, 0f, 0.2f * a).getRGB();

		gui.push().translate(-totalWidth / 2, 0, -5);
		gui.drawRect(-bgPadingX, -bgPadingY, totalWidth + bgPadingX * 2, 8 + bgPadingY * 2, color);

		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		gui.bind(tex);
		gui.push().translate(-totalWidth / 2 - bgPadingX - 7 * 0.667, -5).scale2D(0.667);
		gui.drawTex(5, 6, 7, 27);

		gui.push().translate(totalWidth / 2 + bgPadingX, -5).scale2D(0.667);
		gui.drawTex(113, 6, 7, 27);

		GlStateManager.color(1, 1, 1, a);
		GlStateManager.disableDepth();
		if (icon != null) {
			gui.bind(Tex.of("hud/icon/" + icon));
			gui.ref.drawModalRectWithCustomSizedTexture(-totalWidth / 2, -4, 0, 0, 15, 15, 15, 15);
		}

		gui.push().translate(-totalWidth / 2 + iconWidth + iconMargin, 0);
		gui.drawString(text, new Color(1, 1, 1, a).getRGB());

		gui.pop();
	}

}
