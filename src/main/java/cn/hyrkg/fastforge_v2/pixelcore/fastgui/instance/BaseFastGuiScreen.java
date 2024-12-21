package cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.IDrawable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler.ITickable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler.TickableHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public abstract class BaseFastGuiScreen extends GuiScreen implements IDrawable, ITickable {

	protected FastGuiHandler handler;

	public BaseFastGuiScreen() {
		handler = new FastGuiHandler(this, this);
		TickableHandler.registerTickable(this);
	}

	@Override
	public void initGui() {
		handler.getTransformSolution().wh(width, height);
		fastInitGui(handler);
	}

	public abstract void fastInitGui(FastGuiHandler gui);

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
		GlStateManager.enableTexture2D();
		GlStateManager.disableDepth();

		handler.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Functional Methods
	 **/

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		handler.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseInput() throws IOException {
		if (handler.handleMouseInput())
			super.handleMouseInput();
	}

	public void onWheelInput(int value) {
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		handler.getComponents().forEach(j -> j.onClose());
		TickableHandler.unregisterTickable(this);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		handler.onKeyInput(typedChar, keyCode);
	}

	@Override
	public void tick() {
		handler.onTick();
	}

	public void close() {
		this.mc.displayGuiScreen((GuiScreen) null);

		if (this.mc.currentScreen == null) {
			this.mc.setIngameFocus();
		}
	}

	public FastGuiHandler getHandler() {
		return handler;
	}

}
