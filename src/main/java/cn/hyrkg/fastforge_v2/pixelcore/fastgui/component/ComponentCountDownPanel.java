package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;

public class ComponentCountDownPanel extends BaseComponent {
	public BiConsumer<ComponentCountDownPanel, FastGuiHandler> drawConsumer = null;
	public BiConsumer<ComponentCountDownPanel, FastGuiHandler> drawAfterAllConsumer = null;

	public Consumer<ComponentCountDownPanel> finishConsumer = null;

	protected TickCounter tickCounter = null;

	public ComponentCountDownPanel(int x, int y, int w, int h, int tick) {
		this.transformSolution = TransformSolution.of(x, y, w, h);

		tickCounter = new TickCounter(tick, false);
	}

	public TickCounter getTickCounter() {
		return tickCounter;
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		if (drawAfterAllConsumer != null) {
			drawAfterAllConsumer.accept(this, gui);
		}
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (drawConsumer != null) {
			drawConsumer.accept(this, gui);
		}
	}

	public void onTick() {
		tickCounter.tick();
		if (tickCounter.isDone()) {
			if (finishConsumer != null) {
				finishConsumer.accept(this);
			}
		}
	}

	public ComponentCountDownPanel setDrawAfterAllConsumer(
			BiConsumer<ComponentCountDownPanel, FastGuiHandler> drawAfterAllConsumer) {
		this.drawAfterAllConsumer = drawAfterAllConsumer;
		return this;
	}

	public ComponentCountDownPanel setDrawConsumer(BiConsumer<ComponentCountDownPanel, FastGuiHandler> drawConsumer) {
		this.drawConsumer = drawConsumer;
		return this;
	}

	public ComponentCountDownPanel setFinishConsumer(Consumer<ComponentCountDownPanel> finishConsumer) {
		this.finishConsumer = finishConsumer;
		return this;
	}

}
