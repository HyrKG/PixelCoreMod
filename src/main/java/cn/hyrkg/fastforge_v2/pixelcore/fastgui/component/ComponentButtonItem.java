package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * @author HyrKG
 *
 */
public class ComponentButtonItem extends ComponentButton {

	protected ItemStack item;

	private boolean cacheIsHover;

	private int bgColor = -999;
	protected final int size;

	public ComponentButtonItem(int id, int x, int y, int size, ItemStack item) {
		super(id, x, y, size, size);

		this.item = item;
		this.size = size;
	}

	public ComponentButtonItem setBgColor(int bgColor) {
		this.bgColor = bgColor;
		return this;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (!isEnable())
			return;

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);

		if (bgColor != -999) {
			gui.drawRect(size, size, bgColor);
			GlStateManager.color(1, 1, 1);
		}

		if (item != null) {
			double sc = size / 20d;
			double offset = 2 * sc;

			gui.push("item").translate(offset, offset, 1).scale2D(sc);
			gui.glStateInvokeStart();
			this.getGuiHandler().gui.mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
			gui.glStateInvokeEnd();
		}

		if (cacheIsHover = isHover()) {
			gui.drawRect(transformSolution.width, transformSolution.height, GREY_COVER);
		}
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		if (cacheIsHover && item != null) {
			gui.gui.drawHoveringText(gui.gui.getItemToolTip(item), gui.getLastMouseX(), gui.getLastMouseY());
		}
	}

	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,
			EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
		ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
}
