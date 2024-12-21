package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list;

import java.util.function.Consumer;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.LibColor;

public class ComponentListButton<T> extends ComponentButton {

	protected int interval;
	protected Consumer<ComponentListButton<T>> consumerToDraw = null;
	protected T info;

	private String hoverText = null;

	public ComponentListButton(int id, int width, int height, int interval) {
		super(id, 0, 0, width, height);
		this.interval = height + interval;
	}

	public ComponentListButton<T> info(T info) {
		this.info = info;
		return this;
	}

	public ComponentListButton<T> setHoverText(String hoverText) {
		this.hoverText = hoverText;
		return this;
	}

	public T getInfo() {
		return info;
	}

	public ComponentListButton<T> setConsumerToDraw(Consumer<ComponentListButton<T>> consumerToDraw) {
		this.consumerToDraw = consumerToDraw;
		return this;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (consumerToDraw != null)
			consumerToDraw.accept(this);
		super.drawBeforeCompoents(gui);
	}

	public int getInterval() {
		return interval;
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		if (hoverText != null && isHover()) {
			int length = gui.gui.mc.fontRenderer.getStringWidth(hoverText);

			gui.pushKeep("after").translate(gui.getLastMouseX() - length, gui.getLastMouseY() - 20, 200).scale2D(1.2);
			gui.drawRect(-3, -2, length + 6, 8 + 4, LibColor.black70);
			gui.drawString(0, 0, hoverText, -1);
			gui.pop("after");
		}
	}

}
