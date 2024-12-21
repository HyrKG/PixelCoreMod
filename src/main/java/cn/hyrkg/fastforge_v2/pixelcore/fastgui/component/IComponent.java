package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.List;
import java.util.function.Supplier;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;

public interface IComponent extends IDrawable {

	/**
	 * */
	public default void drawAfterAll(FastGuiHandler gui) {

	}

	/**
	 * 获得转换方案
	 */
	TransformSolution getTransformSolution();

	/**
	 * 判断是否悬浮
	 */
	boolean isHover(int mouseX, int mouseY);

	IComponent setHoverPrecondition(Supplier<Boolean> hoverPrecondition);

	double[] getTransformFinalOffset();

	double[] getRelatedOffsetFromMouse(int offsetX, int offsetY);

	/**
	 * 获得控件
	 */
	IComponent getSuperComponent();

	void setSuperComponent(IComponent superComponent);

	void addComponent(IComponent component);

	void removeComponent(IComponent component);

	void setGuiHandler(FastGuiHandler guiHandler);

	FastGuiHandler getGuiHandler();

	void tryDismiss(IComponent component);

	/**
	 * 获得子控件
	 */
	List<IComponent> getComponents();

	/**
	 * 判断是否激活
	 */
	boolean isActive(FastGuiHandler gui);

	boolean isSkipDraw();

	/**
	 * 方法
	 */

	default void onMouseInput() {

	}

	default void onWheelInput(int value) {

	}

	default void onMouseClick(int mouseButton) {

	}

	default void onKeyInput(char typedChar, int keyCode) {

	}

	default void onTick() {

	}

	default void onEnter() {

	}

	default void onRemove() {

	}

	default void onClose() {

	}

	default void onDismiss() {

	}
}
