package cn.hyrkg.pixelgame.module.lottery.csgo;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentScissorPanel;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.pixelgame.core.lib.LibSounds;
import cn.hyrkg.pixelgame.module.lottery.LotteryPrize;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class CompLotteryScrollView extends ComponentScissorPanel {
	private GuiLotteryCsgo lotteryGui;

	private int lastOffsetX = 0;
	private int offsetX = 0;
	private TickCounter slowdownTimer = new TickCounter(5 * 20, false);

	private ItemStack hoverStack = null;

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		// TODO Auto-generated method stub
		super.init(fastGuiHandler);
	}

	public CompLotteryScrollView(GuiLotteryCsgo gui) {
		super(43, 224, 1139, 225);
		this.lotteryGui = gui;
	}

	@Override
	public void drawPanel(FastGuiHandler gui) {

		if (lotteryGui.prizePool == null || lotteryGui.prizePool.isEmpty()) {
			return;
		}

		gui.pushKeep("").offset(64 - 40, 318 - 224);
		gui.glStateInvokeStart();
		double offset = lastOffsetX + (offsetX - lastOffsetX) * gui.getPartialTicks();
		hoverStack = null;
		for (int i = 0; i < 31; i++) {
			int index = i % lotteryGui.prizePool.size();

			LotteryPrize prize = lotteryGui.prizePool.get(index);
			prize.setSuperComponent(this);
			prize.getTransformSolution().translate((int) (64 - 40 + i * 200 - offset), (int) (318 - 224), 0).wh(192,
					95);
			prize.draw(gui, i * 200 - offset, 0);

			if (hoverStack == null && this.isHover() && prize.stack != null
					&& prize.isHover(gui.getLastMouseX(), gui.getLastMouseY())) {
				hoverStack = prize.stack;
			}

		}

		gui.pop("");

		gui.push("").translate(504 - 40, 286 - 224, 550);
		gui.drawTex(900, 857, 211, 159);
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		super.drawAfterAll(gui);
		if (hoverStack != null) {
			FontRenderer font = hoverStack.getItem().getFontRenderer(hoverStack);
			net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(hoverStack);
			gui.gui.drawHoveringText(gui.gui.getItemToolTip(hoverStack), gui.getLastMouseX(), gui.getLastMouseY());
			net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
		}
	}

	@Override
	public void onTick() {
		super.onTick();
		slowdownTimer.tick();

		lastOffsetX = offsetX;
		offsetX += 100 * (1f - slowdownTimer.percentage());

		if (!slowdownTimer.isDone() && slowdownTimer.tick % (int) (Math.max(1, 9 * slowdownTimer.percentage())) == 0) {
			LibSounds.play("lottery_tick");
		}
	}

	public boolean isCompleted() {
		return slowdownTimer.isDone();
	}
}
