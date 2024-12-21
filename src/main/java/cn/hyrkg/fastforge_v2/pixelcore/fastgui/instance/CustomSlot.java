package cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CustomSlot extends HoverCheck {

	public Slot bindingSlot;
	public int size;
	public double sc;

	public CustomSlot(Slot slot, int id, int x, int y, int size, double scale) {
		super(id, x, y, size, size, scale);
		this.sc = scale;
		this.bindingSlot = slot;
		this.size = size;
		this.size = (int) (this.size * scale * 2.5);
	}

	public CustomSlot(Slot slot, int id, int x, int y, int size, double scale, double possc) {
		super(id, x, y, size, size, possc);
		this.bindingSlot = slot;
		this.sc = scale;
		this.size = size;
	}

	public CustomSlot(Slot slot, int id, int x, int y, int sizeX, int sizeY, int sizeItem, double sc) {
		super(id, x, y, sizeX, sizeY, sc);
		this.bindingSlot = slot;
		this.sc = sc;
		this.size = sizeItem;
	}

	public void drawItem(Minecraft mc, RenderItem itemRender, ItemStack item, String s) {

		int sx = offsetX + scaleX;
		int sy = offsetY + scaleY;
		// TODO drawItem
		GL11.glPushMatrix();
		GlStateManager.enableDepth();
		GL11.glTranslated(sx, sy, 0);
		GL11.glScaled(size * 0.0235, size * 0.0235, 1);

		itemRender.renderItemAndEffectIntoGUI(mc.player, item, 0, 0);
		itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, item, 0, 0, s);
		GlStateManager.disableDepth();
		GL11.glPopMatrix();

	}

	public void drawRect(GuiScreen screen, int mouseX, int mouseY) {
		this.drawRect(screen);
	}

	public void drawRect(GuiScreen screen) {
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 300);
		drawHover(screen);
		GL11.glPopMatrix();
	}

	public void drawCover(GuiScreen screen, int mouseX, int mouseY) {

	}

	public boolean isAllow() {
		return true;
	}
}
