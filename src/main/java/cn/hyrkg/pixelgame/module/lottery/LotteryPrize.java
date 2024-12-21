package cn.hyrkg.pixelgame.module.lottery;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.pixelgame.module.lottery.csgo.GuiLotteryCsgo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class LotteryPrize extends BaseComponent {

	public ItemStack stack;
	public QualityType quality;

	public LotteryPrize(QualityType quality, ItemStack stack) {
		this.stack = stack;
		this.quality = quality;
	}

	public void draw(FastGuiHandler gui, double x, double y) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 60);
		gui.drawTex(quality.x, quality.y, 192, 95);

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslated(70, 18, 15);
		double sc = 3.5;
		GL11.glScaled(sc, sc, 1);
		gui.gui.mc.getRenderItem().renderItemIntoGUI(stack, 0, 0);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();

		gui.bind(GuiLotteryCsgo.tex);
		GL11.glPopMatrix();
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {

	}
}
