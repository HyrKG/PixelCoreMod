package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.Consumer;

public interface IShortcutComponent {

	Consumer<BaseComponent> getWhenLeaveConsumer();

	BaseComponent setWhenLeaveConsumer(Consumer<BaseComponent> whenLeave);

}
