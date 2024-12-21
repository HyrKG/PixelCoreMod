package cn.hyrkg.pixelgame.module.lottery.flip_card;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.DrawHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.pixelgame.core.lib.LibSounds;
import cn.hyrkg.pixelgame.module.lottery.LotteryPrize;
import cn.hyrkg.pixelgame.module.lottery.QualityType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CompCard extends ComponentButton {

	private TickCounter aniClickCounter = new TickCounter(10, false);
	private TickCounter aniExpandCounter = new TickCounter(5, false);
	private TickCounter rorateCounter = new TickCounter(900, true);

	private GuiLotteryFlipCard lotteryGui;
	private LotteryPrize prize = null;
	private boolean selected = false;

	private boolean lastHover = false;

	public CompCard(GuiLotteryFlipCard gui, int id, int x, int y) {
		super(id, x, y, 100, 100);
		this.lotteryGui = gui;
		aniClickCounter.tick = aniClickCounter.tickMax;
		aniClickCounter.lastTick = aniClickCounter.tick;

		aniExpandCounter.tick = aniExpandCounter.tickMax;
		aniExpandCounter.lastTick = aniExpandCounter.tick;

	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		super.init(fastGuiHandler);
		this.getTransformSolution().scale2D(0.6f);
	}

	public void setLotteryPrize(LotteryPrize prize, boolean selected) {
		this.prize = prize;
		this.selected = selected;
		aniClickCounter.tick = 0;
		aniClickCounter.lastTick = 0;

	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		float scale = 1;
		double shake = 0;

		boolean hasResult = prize != null;

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		if (!aniClickCounter.isDone()) {
			scale *= Math.max(0.6f, (1.0 - aniClickCounter.percentage(gui.getPartialTicks())));
			shake = Math.sin(aniClickCounter.tick * 3);
		} else if (!aniExpandCounter.isDone()) {
			scale *= aniExpandCounter.percentage(gui.getPartialTicks());
		} else if (!hasResult && isHover(gui.getLastMouseX(), gui.getLastMouseY())) {
			scale = 1.2f;
		}

		Tex tex = Tex.of("lottery/flip_card/" + lotteryGui.getIcon());
		gui.bind(tex);

		if (!hasResult || !aniClickCounter.isDone()) {
			GL11.glPushMatrix();
			GL11.glTranslated((100 - 100 * scale) / 2.0, (100 - 100 * scale) / 2.0, 0);
			GL11.glScalef(scale, scale, 1);
			GL11.glRotated(shake, 0, 0, 1);
			gui.drawTex(0, 0, 100, 100);
			GL11.glPopMatrix();
		} else if (hasResult) {
			float ep = aniExpandCounter.percentage(gui.getPartialTicks());

			if (selected) {
				gui.bind(Tex.of("lottery/shine2"));
			} else {
				gui.bind(Tex.of("lottery/shine5"));

			}
			GL11.glPushMatrix();
			GL11.glTranslated(48, 50, 45);
			QualityType type = prize.quality;

			GL11.glRotated(rorateCounter.percentage(gui.getPartialTicks()) * 360f, 0, 0, 1);

			if (selected) {
				GL11.glScaled(0.7 * scale * ep, 0.7 * scale * ep, 1);
				GlStateManager.color(type.color.getRed() / 255f, type.color.getGreen() / 255f,
						type.color.getBlue() / 255f, ep * 1f);
			} else {
				GL11.glScaled(0.4 * scale * ep, 0.4 * scale * ep, 1);
				GlStateManager.color(type.color.getRed() / 255f, type.color.getGreen() / 255f,
						type.color.getBlue() / 255f, ep * 1f);
			}

			DrawHelper.drawModalRectWithCustomSizedTextureC(0, 0, 0, 0, 296, 287, 296, 287);

			GL11.glPopMatrix();

			if (hasResult) {
				GL11.glPushMatrix();

				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.enableDepth();

				GL11.glPushMatrix();
				GL11.glTranslated(18, 18, 0);
				int size = 64;
				GL11.glTranslated((size - size * scale) / 2.0, (size - size * scale) / 2.0, 15);

				double sc = 3.8 * scale;
				GL11.glScaled(sc, sc, 1);
				gui.gui.mc.getRenderItem().renderItemIntoGUI(prize.stack, 0, 0);
				GL11.glPopMatrix();
				RenderHelper.disableStandardItemLighting();

				GL11.glPopMatrix();

				gui.push().translate(50, 92).scale2D(ep * 2.2);
				List<String> splitName = gui.gui.mc.fontRenderer
						.listFormattedStringToWidth(prize.stack.getDisplayName(), 60);
				String name = splitName.get(0);
				if (splitName.size() > 1) {
					name += "...";
				}
				gui.drawCenteredString(name);
			}
		}
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {

		boolean isHover = isHover();
		if (lastHover == false && isHover && prize == null) {
			LibSounds.play("card_hover");
		}
		lastHover = isHover;

		if (prize != null && isHover() && aniExpandCounter.isDone() && aniClickCounter.isDone()
				&& prize.stack != null) {
			ItemStack hoverStack = prize.stack;
			FontRenderer font = hoverStack.getItem().getFontRenderer(hoverStack);
			net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(hoverStack);
			gui.gui.drawHoveringText(gui.gui.getItemToolTip(hoverStack), gui.getLastMouseX(), gui.getLastMouseY());
			net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {
		super.draw(gui);
	}

	@Override
	public void onTick() {
		super.onTick();

		rorateCounter.tick();

		if (!aniClickCounter.isDone()) {
			aniClickCounter.tick();

			if (aniClickCounter.isDone()) {
				aniExpandCounter.lastTick = 0;
				aniExpandCounter.tick = 0;
			}
		}
		aniExpandCounter.tick();

	}

	public TickCounter getAniClickCounter() {
		return aniClickCounter;
	}

}
