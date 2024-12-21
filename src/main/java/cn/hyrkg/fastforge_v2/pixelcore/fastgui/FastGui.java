package cn.hyrkg.fastforge_v2.pixelcore.fastgui;

import java.util.Stack;

import com.google.common.base.Preconditions;
import com.mojang.realmsclient.gui.ChatFormatting;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class FastGui {

	public final Minecraft mc;
	public final Gui ref;

	public int lastMouseX, lastMouseY;
	public int width, height;
	public float partialTicks;

	public float scale = 1;

	private Float scaledWeight = null, scaledHeight = null;

	public FastGui(Minecraft mc, Gui gui) {
		this.mc = mc;
		this.ref = gui;
	}

	/**
	 * Texture Function
	 */
	private Tex bindedTex;

	/*
	 * Gl State
	 */
	private Stack<GlState> glStateStack = new Stack<>();

	public void glStateInvokeStart() {
		if (!glStateStack.isEmpty())
			glStateStack.peek().invokeStart();
	}

	public void glStateInvokeEnd() {
		if (!glStateStack.isEmpty()) {
			if (glStateStack.peek().checkEnd()) {
				glStateStack.pop().invokeEnd();
			}
		}
	}

	public GlState push() {
		glStateStack.push(new GlState().times(1));
		return glStateStack.peek();
	}

	public GlState push(int times) {
		glStateStack.push(new GlState()).times(times);
		return glStateStack.peek();
	}

	public GlState pushKeep() {
		glStateStack.push(new GlState());
		return glStateStack.peek();
	}

	public void pop() {
		if (!glStateStack.isEmpty())
			glStateStack.pop().invokeEnd();
	}

	public void bind(Tex tex) {
		Preconditions.checkNotNull(tex, "Couldn't bind null tex!");

		bindedTex = tex;
		mc.renderEngine.bindTexture(tex.getTexture());
	}

	public void drawTex(int x, int y, int u, int v, int w, int h) {
		Preconditions.checkNotNull(bindedTex, "Couldn't draw before bind tex!");

		glStateInvokeStart();

		ref.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, bindedTex.texWidth, bindedTex.texHeight);

		glStateInvokeEnd();
	}

	public void drawTex(int u, int v, int w, int h) {
		Preconditions.checkNotNull(bindedTex, "Couldn't draw before bind tex!");
		drawTex(0, 0, u, v, w, h);

	}

	public void drawFullTex(int x, int y) {
		Preconditions.checkNotNull(bindedTex, "Couldn't draw before bind tex!");
		drawTex(x, y, 0, 0, bindedTex.texWidth, bindedTex.texHeight);
	}

	public void drawFullTex() {
		drawTex(0, 0, 0, 0, bindedTex.texWidth, bindedTex.texHeight);
	}

	public void drawTex(int x, int y, ReadyTex readyTex) {
		if (bindedTex == null || bindedTex != readyTex)
			bind(readyTex);
		drawTex(x, y, readyTex.u, readyTex.v, readyTex.width, readyTex.height);
	}

	public void drawTex(ReadyTex readyTex) {
		drawTex(0, 0, readyTex);
	}

	public void drawReadyTex(int x, int y) {
		Preconditions.checkNotNull(bindedTex, "Couldn't draw before bind tex!");
		Preconditions.checkNotNull(bindedTex instanceof ReadyTex,
				"You should bind ready-tex before draw it without give it");

		drawTex(x, y, (ReadyTex) bindedTex);
	}

	public void drawReadyTex() {
		Preconditions.checkNotNull(bindedTex, "Couldn't draw before bind tex!");
		Preconditions.checkNotNull(bindedTex instanceof ReadyTex,
				"You should bind ready-tex before draw it without give it");

		drawTex(0, 0, (ReadyTex) bindedTex);
	}

	/*
	 * Draw String
	 */
	public void drawString(int x, int y, String string, int color) {
		glStateInvokeStart();

		mc.fontRenderer.drawString(string, x, y, color);

		glStateInvokeEnd();
	}

	public void drawString(String string) {
		drawString(0, 0, string, -1);
	}

	public void drawString(String string, int color) {
		drawString(0, 0, string, color);
	}

	public void drawStringLimitWidth(String string, int width) {
		int charWidth = mc.fontRenderer.getStringWidth(string);
		String stringToDraw = string;
		if (charWidth > width) {
			stringToDraw = mc.fontRenderer.listFormattedStringToWidth(stringToDraw, width).get(0) + "...";
		}
		drawString(stringToDraw);
	}

	public void drawStringWithShadow(String string) {
		glStateInvokeStart();
		mc.fontRenderer.drawStringWithShadow(string, 0, 0, -1);
		glStateInvokeEnd();
	}

	/*
	 * Draw Centered String
	 */
	public void drawCenteredString(int x, int y, String string, int color) {
		glStateInvokeStart();

//		mc.fontRenderer.drawString(text, x, y, color, dropShadow)
		mc.fontRenderer.drawString(string,
				(int) (x - mc.fontRenderer.getStringWidth(ChatFormatting.stripFormatting(string)) / 2), (int) y, color);

		glStateInvokeEnd();
	}

	public void drawCenteredStringWithShadow(String string) {
		glStateInvokeStart();

		mc.fontRenderer.drawStringWithShadow(string, (int) (0 - mc.fontRenderer.getStringWidth(string) / 2), (int) 0,
				-1);

		glStateInvokeEnd();
	}

	public void drawCenteredString(String string) {
		drawCenteredString(0, 0, string, -1);
	}

	public void drawCenteredString(String string, int color) {
		drawCenteredString(0, 0, string, color);
	}

	public void drawSplitString(String string, int split) {
		glStateInvokeStart();
		mc.fontRenderer.drawSplitString(string, 0, 0, split, -1);
		glStateInvokeEnd();
	}

	public void drawSplitStringWithShadow(String string, int split) {
		glStateInvokeStart();
		String str = trimStringNewline(string);
		int y = 0;
		for (String s : mc.fontRenderer.listFormattedStringToWidth(str, split)) {
			mc.fontRenderer.drawStringWithShadow(s, 0, y, -1);
			y += mc.fontRenderer.FONT_HEIGHT;
		}
		glStateInvokeEnd();
	}

	private String trimStringNewline(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	/*
	 * Draw Rect
	 */
	public void drawRect(int x, int y, int w, int h, int color) {
		glStateInvokeStart();

		ref.drawRect(x, y, x + w, y + h, color);

		glStateInvokeEnd();
	}

	public void drawRect(int w, int h, int color) {
		drawRect(0, 0, w, h, color);

	}

	public float computeScaleWeight() {
		if (scaledWeight != null) {
			return scaledWeight;
		}
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		float f = 2f / (float) scaledresolution.getScaleFactor();
		return scaledWeight = Minecraft.getMinecraft().displayWidth / 1920F * f;
	}

	public float computeScaleHeight() {
		if (scaledHeight != null) {
			return scaledHeight;
		}
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		float f = 2f / (float) scaledresolution.getScaleFactor();
		return scaledHeight = Minecraft.getMinecraft().displayHeight / 1080F * f;
	}
}
