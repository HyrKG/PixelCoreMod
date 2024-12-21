package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/*
 * 你所添加进此面板地内容都将会被进行裁剪
 * */
public class ComponentScissorPanel extends ComponentPanel {

	public ComponentScissorPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void draw(FastGuiHandler gui) {
		// TODO cut in here

		GL11.glPushMatrix();
		double[] offset = getTransformFinalOffset();

		Minecraft client = Minecraft.getMinecraft();
		ScaledResolution res = new ScaledResolution(client);
		double scaleW = client.displayWidth / res.getScaledWidth_double();
		double scaleH = client.displayHeight / res.getScaledHeight_double();

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) ((offset[0]) * scaleW),
				(int) (client.displayHeight - ((offset[1] + this.transformSolution.height * offset[3]) * scaleH)),

				(int) (this.transformSolution.width * offset[2] * scaleW),
				(int) (this.transformSolution.height * offset[3] * scaleH));

		if (isBuildMode()) {
			lastGui = gui;
			if (isDragging()) {
				offsetToMouse(gui.getLastMouseX(), gui.getLastMouseY());
			}
		}

		GL11.glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);

		TransformSolution trans = getTransformSolution();

		GL11.glTranslated(trans.getX(), trans.getY(), trans.getZ());
		GL11.glScaled(trans.scaledX, trans.scaledY, trans.scaledZ);

		drawPanel(gui);

		drawBeforeCompoents(gui);

		drawDebug(trans, gui);

		components.forEach(j -> {
			if (!j.isSkipDraw())
				j.draw(gui);
		});
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
	}

	public void drawPanel(FastGuiHandler gui) {

	}

}
