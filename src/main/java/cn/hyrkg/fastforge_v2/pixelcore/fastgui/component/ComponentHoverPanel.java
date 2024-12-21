package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import net.minecraft.client.renderer.GlStateManager;

public abstract class ComponentHoverPanel extends BaseComponent {

	private boolean lastHover = false;

	public ComponentHoverPanel(int x, int y, int width, int height) {
		this.transformSolution = TransformSolution.of(x, y, width, height);
	}

	@Override
	public void draw(FastGuiHandler gui) {
		if (isBuildMode()) {
			lastGui = gui;
			if (isDragging) {
				offsetToMouse(gui.getLastMouseX(), gui.getLastMouseY());
			}
		}

		GL11.glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);

		TransformSolution trans = getTransformSolution();

		GL11.glTranslated(trans.getX(), trans.getY(), trans.getZ());
		GL11.glScaled(trans.scaledX, trans.scaledY, trans.scaledZ);

		if (lastHover) {
			drawBeforeCompoents(gui);
			components.forEach(j -> {
				if (!j.isSkipDraw())
					j.draw(gui);
			});
		}

		drawDebug(trans, gui);

		GL11.glPopMatrix();
	}

	@Override
	public void onTick() {
		lastHover = isHover();
		if (lastHover) {
			super.onTick();
		}
	}

	@Override
	public void onMouseClick(int mouseButton) {
		if (lastHover) {
			super.onMouseClick(mouseButton);
		}
	}

	@Override
	public void onKeyInput(char typedChar, int keyCode) {
		if (lastHover) {
			super.onKeyInput(typedChar, keyCode);
		}
	}

	@Override
	public void onMouseInput() {
		if (lastHover) {
			super.onMouseInput();
		}
	}

	@Override
	public void onWheelInput(int value) {
		if (lastHover) {
			super.onWheelInput(value);
		}
	}
}
