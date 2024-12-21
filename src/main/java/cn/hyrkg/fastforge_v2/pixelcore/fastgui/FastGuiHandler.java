package cn.hyrkg.fastforge_v2.pixelcore.fastgui;

import java.io.IOException;
import java.util.Stack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;
import com.mojang.realmsclient.gui.ChatFormatting;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.IComponent;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.IDrawable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.BaseFastGuiScreen;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class FastGuiHandler extends BaseComponent {
	public final GuiScreen gui;
	public final IDrawable drawableCallback;
	public boolean skipDrawAfterAll = false;
	private final Minecraft mc;
	private boolean isAlwaysHover = false;

	private boolean promise_dont_draw_components = false;

	public FastGuiHandler(GuiScreen gui, IDrawable drawable) {
		this.gui = gui;
		this.drawableCallback = drawable;
		mc = Minecraft.getMinecraft();

		transformSolution = new TransformSolution(0, 0, gui.width, gui.height);
		this.setGuiHandler(this);

	}

	/**
	 * Component Extends
	 */
	@Override
	public void draw(FastGuiHandler gui) {

		if (drawableCallback != null)
			drawableCallback.draw(gui);

		if (!promise_dont_draw_components) {
			components.forEach(j -> {
				if (!j.isSkipDraw())
					j.draw(gui);
			});
		}
	}

	@Override
	public boolean isActive(FastGuiHandler gui) {
		return true;
	}

	@Override
	public void setSuperComponent(IComponent superComponent) {

	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {

	}

	public void setAlwaysHover(boolean isAlwaysHover) {
		this.isAlwaysHover = isAlwaysHover;

	}

	@Override
	public void onWheelInput(int value) {
		super.onWheelInput(value);
		if (gui instanceof BaseFastGuiScreen) {
			((BaseFastGuiScreen) gui).onWheelInput(value);
		}
	}

	@Override
	public boolean isHover() {
		if (isAlwaysHover)
			return true;
		return super.isHover();
	}

	/**
	 * Need to bridge
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		this.partialTicks = partialTicks;
		// anti zero to million problem
		if (lastMouseX == -1 || lastMouseY == -1) {
			lastMouseX = mouseX;
			lastMouseY = mouseY;
		}

		if (isBuildMode()) {
			lastGui = this;
			if (isDragging())
				offsetToMouse(lastMouseX, lastMouseY);
		}

		GL11.glPushMatrix();

		GL11.glTranslated(getTransformSolution().getX(), getTransformSolution().getY(), getTransformSolution().getZ());
		GL11.glScaled(getTransformSolution().scaledX, getTransformSolution().scaledY, getTransformSolution().scaledZ);

		draw(this);

		drawDebug(getTransformSolution(), this);

		GL11.glPopMatrix();

		if (!skipDrawAfterAll) {
			components.forEach(j -> j.drawAfterAll(this));
		}

		// set last mouse
		lastMouseX = mouseX;
		lastMouseY = mouseY;

		if (isDebugMode()) {
			push("offset-mouse").translate(0, 8);
			drawString(mouseX, mouseY, lastMouseX + "/" + lastMouseY, -1);
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		components.forEach(j -> j.onMouseClick(mouseButton));

		if (mouseButton == 0) {

		} else if (mouseButton == 1) {

		}
	}

	public boolean handleMouseInput() throws IOException {
		this.onMouseInput();

		int i = Mouse.getEventDWheel();

		if (i != 0) {
			if (i > 1) {
				i = -1;
			} else if (i < -1) {
				i = 1;
			}
			final int value = i;

			this.onWheelInput(value);
		}

		return true;
	}

	@Override
	public void onKeyInput(char typedChar, int keyCode) {
		components.forEach(j -> j.onKeyInput(typedChar, keyCode));
	}

	@Override
	public void onTick() {
		components.forEach(j -> j.onTick());
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

	public GlState push(String note) {
		glStateStack.push(new GlState().times(1));
		return glStateStack.peek();
	}

	public GlState push(String note, int times) {
		glStateStack.push(new GlState()).times(times);
		return glStateStack.peek();
	}

	public GlState pushKeep(String note) {
		glStateStack.push(new GlState());
		return glStateStack.peek();
	}

	public void pop(String note) {
		if (!glStateStack.isEmpty())
			glStateStack.pop().invokeEnd();
	}

	public void pop() {
		if (!glStateStack.isEmpty())
			glStateStack.pop().invokeEnd();
	}

	/*
	 * Draw Texture
	 */

	public void bind(Tex tex) {
		Preconditions.checkNotNull(tex, "Couldn't bind null tex!");

		bindedTex = tex;
		mc.renderEngine.bindTexture(tex.getTexture());
	}

	public void drawTex(int x, int y, int u, int v, int w, int h) {
		Preconditions.checkNotNull(bindedTex, "Couldn't draw before bind tex!");

		glStateInvokeStart();

		gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, bindedTex.texWidth, bindedTex.texHeight);

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

		gui.drawRect(x, y, x + w, y + h, color);

		glStateInvokeEnd();
	}

	public void drawRect(int w, int h, int color) {
		drawRect(0, 0, w, h, color);

	}

	/**
	 * Others
	 */
	public Minecraft mc() {
		return gui.mc;
	}

	/**
	 * UI Info
	 **/
	private int lastMouseX = -1, lastMouseY = -1;
	private float partialTicks = 0;

	public int getLastMouseX() {
		return lastMouseX;
	}

	public int getLastMouseY() {
		return lastMouseY;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public int getGuiWidth() {
		return this.gui.width;
	}

	public int getGuiHeight() {
		return this.gui.height;
	}

	/**
	 * Promises
	 */
	public class Promise {
		public void notToDrawComponents() {
			promise_dont_draw_components = true;
		}

		public void allowDrawComponents() {
			promise_dont_draw_components = false;
		}
	}

	private Promise promise = new Promise();

	/**
	 * You should not to use it in regular situation
	 */
	@Deprecated
	public Promise promise() {
		return promise;
	}
}
