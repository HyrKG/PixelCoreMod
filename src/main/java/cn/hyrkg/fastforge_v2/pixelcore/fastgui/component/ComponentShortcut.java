package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.Consumer;

public abstract class ComponentShortcut extends BaseComponent implements IShortcutComponent {

	protected Consumer<BaseComponent> whenLeave;

	@Override
	public BaseComponent setWhenLeaveConsumer(Consumer<BaseComponent> whenLeave) {
		this.whenLeave = whenLeave;
		return this;
	}

	@Override
	public Consumer<BaseComponent> getWhenLeaveConsumer() {
		return whenLeave;
	}

	@Override
	public void onTick() {
		// check when leave call method!
		super.onTick();
		if (!this.isHover()) {
			if (getWhenLeaveConsumer() != null) {
				getWhenLeaveConsumer().accept(this);
			}
		}
	}

}
