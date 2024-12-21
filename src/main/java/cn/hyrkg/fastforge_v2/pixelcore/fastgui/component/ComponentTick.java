package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.Consumer;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;

public class ComponentTick extends BaseComponent {

	protected Consumer<FastGuiHandler> consumer;

	public ComponentTick(Consumer<FastGuiHandler> csm) {
		this.consumer = csm;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {

	}

	@Override
	public void onTick() {
		if (consumer != null) {
			consumer.accept(fastGuiHandler);
		}
	}

}
