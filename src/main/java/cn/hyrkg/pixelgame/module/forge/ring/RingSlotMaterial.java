package cn.hyrkg.pixelgame.module.forge.ring;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.CustomSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

public class RingSlotMaterial extends CustomSlot {

	private GuiRingBlueprint gui;
	private String text = "";// 数量（需求）文本

	public RingSlotMaterial(GuiRingBlueprint gui, String text, Slot slot, int id, int x, int y, int size, double scale) {
		super(slot, id, x, y, size, scale);
		this.gui = gui;
		this.text = text;
	}

	@Override
	public void drawItem(Minecraft mc, RenderItem itemRender, ItemStack item, String s) {
		int sx = offsetX + scaleX;
		int sy = offsetY + scaleY;
		// TODO drawItem
		GL11.glPushMatrix();
		GL11.glTranslated(sx, sy, 0);

		GlStateManager.color(1, 1, 1);
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		if (bindingSlot.getHasStack()) {
			GL11.glPushMatrix();

			GL11.glTranslated(-0.7, -0.6, 0);
			GL11.glScaled(size * 0.028, size * 0.028, 1);
			mc.renderEngine.bindTexture(GuiRingBlueprint.TEX_BLUEPRINT.getTexture());
			mc.ingameGUI.drawModalRectWithCustomSizedTexture(0, 0, 371, 23, 16, 15, GuiRingBlueprint.TEX_BLUEPRINT.texWidth,
					GuiRingBlueprint.TEX_BLUEPRINT.texHeight);
			GL11.glPopMatrix();
		} else if (!gui.hasBlueprint()) {
			GL11.glPushMatrix();
			GL11.glTranslated(-0.7, -0.6, 0);
			GL11.glScaled(size * 0.028, size * 0.028, 1);
			mc.renderEngine.bindTexture(GuiRingBlueprint.TEX_BLUEPRINT.getTexture());
			mc.ingameGUI.drawModalRectWithCustomSizedTexture(0, 0, 371, 23, 16, 15, GuiRingBlueprint.TEX_BLUEPRINT.texWidth,
					GuiRingBlueprint.TEX_BLUEPRINT.texHeight);

			GL11.glScaled(0.8, 0.8, 1);
			mc.ingameGUI.drawModalRectWithCustomSizedTexture(6, 4, 139, 240, 9, 11, GuiRingBlueprint.TEX_BLUEPRINT.texWidth,
					GuiRingBlueprint.TEX_BLUEPRINT.texHeight);
			GL11.glPopMatrix();
		}

		GL11.glScaled(size * 0.0235, size * 0.0235, 1);

		itemRender.renderItemAndEffectIntoGUI(mc.player, item, 0, 0);
		if (bindingSlot.getHasStack()) {
			GL11.glPushMatrix();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.disableBlend();
			gui.mc.fontRenderer.drawStringWithShadow("§f" + text, -2, 10, -1);
			GL11.glPopMatrix();
		}
		// itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, item, 0, 0, text);
		GlStateManager.disableDepth();
		GL11.glPopMatrix();
	}

	@Override
	public void drawRect(GuiScreen screen) {

	}

	@Override
	public void drawCover(GuiScreen screen, int mouseX, int mouseY) {
		if (!gui.hasBlueprint()) {
			GL11.glPushMatrix();
			screen.drawHoveringText(java.util.Arrays.asList("§9所需材料", "§7将会在选择有效图纸后显示"), mouseX, mouseY);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean isAllow() {
		return false;
	}

}
