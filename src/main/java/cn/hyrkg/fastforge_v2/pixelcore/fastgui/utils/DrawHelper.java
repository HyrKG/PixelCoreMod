package cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class DrawHelper {
	/**
	 * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height,
	 * textureWidth, textureHeight
	 */
	public static void drawModalRectWithCustomSizedTextureC(int x, int y, float u, float v, int width, int height,
			float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double) x - (width / 2), (double) (y + height) - (height / 2), 0.0D)
				.tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width) - (width / 2), (double) (y + height) - (height / 2), 0.0D)
				.tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width) - (width / 2), (double) y - (height / 2), 0.0D)
				.tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		vertexbuffer.pos((double) x - (width / 2), (double) y - (height / 2), 0.0D)
				.tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor,
			float zLevel) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;

		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

		bufferbuilder.pos((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();

		bufferbuilder.pos((double) left, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();

		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawScaledString(GuiScreen screen, FontRenderer fontRenderer, String string, double x, double y,
			double scaled, int color) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 0);
		GL11.glScaled(scaled, scaled, 1);
		screen.drawString(fontRenderer, string, 0, 0, color);
		GL11.glPopMatrix();
	}

	public static float getDistanceXFromCenter(FastGuiHandler gui) {
		return (gui.getLastMouseX() - gui.getGuiWidth() / 2.0f) / (gui.getGuiWidth() / 2.0f);
	}

	public static float getDistanceYFromCenter(FastGuiHandler gui) {
		return (gui.getLastMouseY() - gui.getGuiHeight() / 2.0f) / (gui.getGuiHeight() / 2.0f);
	}

	public static void drawCenterRolling(float distanceX, float distanceY, float rollRange) {
		/***
		 * 进行晃动
		 */
		double offsetX = 1920 / 2;
		double offsetY = 1028 / 2;
		GL11.glTranslated(offsetX, offsetY, 0);
		GL11.glRotated(distanceX * rollRange, 0, 1, 0);
		GL11.glRotated(-distanceY * rollRange, 1, 0, 0);
		GL11.glTranslated(-offsetX, -offsetY, 0);
	}

	public static void drawRolllingShadow(FastGuiHandler gui, int width, int height, float rollingRange,
			float rollingDistance) {
		GlStateManager.disableDepth();
		float disX = DrawHelper.getDistanceXFromCenter(gui);
		float disY = DrawHelper.getDistanceYFromCenter(gui);
		DrawHelper.drawCenterRolling(disX, disY, rollingRange);

		Color shadowColor = new Color(0f, 0f, 0f, 0.5f);
		gui.push("shadow").translate(-disX * rollingDistance, -disY * rollingDistance, 0);
		gui.drawRect(width, height, shadowColor.getRGB());

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
	}

	public static void drawRolllingShadow(FastGuiHandler gui, int width, int height) {
		GlStateManager.disableDepth();
		float disX = DrawHelper.getDistanceXFromCenter(gui);
		float disY = DrawHelper.getDistanceYFromCenter(gui);
		DrawHelper.drawCenterRolling(disX, disY, 4);

		Color shadowColor = new Color(0f, 0f, 0f, 0.5f);
		gui.push("shadow").translate(-disX * 20, -disY * 20, 0);
		gui.drawRect(width, height, shadowColor.getRGB());

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
	}
}
