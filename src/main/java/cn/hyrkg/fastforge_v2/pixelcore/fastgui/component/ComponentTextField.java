package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

/**
 * 该组件将会提供一个可供输入地文本区域
 */
public class ComponentTextField extends BaseComponent {

	protected GuiTextField textField;

	protected boolean isInited = false;
	protected boolean initTwice = true;
	protected double scaled = 1;
	protected int width, height;
	protected String defaultText = "";

	protected boolean onlyNumber = false;

	protected Consumer<ComponentTextField> onPressEnter = null;
	protected Consumer<ComponentTextField> onPressEsc = null;
	protected BiConsumer<Character, Integer> onPressKey = null;
	protected Consumer<ComponentTextField> onClickFoucus = null;

	public void setInitTwice(boolean initTwice) {
		this.initTwice = initTwice;
	}

	public void setOnlyNumber(boolean onlyNumber) {
		this.onlyNumber = onlyNumber;
	}

	public ComponentTextField setOnClickFoucus(Consumer<ComponentTextField> onClickFoucus) {
		this.onClickFoucus = onClickFoucus;
		return this;
	}

	public ComponentTextField setOnPressEnter(Consumer<ComponentTextField> onPressEnter) {
		this.onPressEnter = onPressEnter;
		return this;
	}

	public ComponentTextField setOnPressEsc(Consumer<ComponentTextField> onPressEsc) {
		this.onPressEsc = onPressEsc;
		return this;
	}

	public ComponentTextField setOnPressKey(BiConsumer<Character, Integer> onPressKey) {
		this.onPressKey = onPressKey;
		return this;
	}

	public ComponentTextField(int width, int height, double scale) {
		this.transformSolution = new TransformSolution(0, 0, width, height);
		this.height = height;
		this.width = width;
		this.scaled = scale;
	}

	public ComponentTextField(int width, int height, double scale, String defaultText) {
		this(width, height, scale);
		this.defaultText = defaultText;
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		if (isInited && !initTwice) {
			return;
		}

		textField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, 0, 0, (int) (width * 0.32), height);
		textField.setEnableBackgroundDrawing(false);
		textField.setFocused(true);
		textField.setText(defaultText);
		isInited = true;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		if (textField == null) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 2);
		GL11.glScaled(scaled * 3, scaled * 3, 1);
		textField.drawTextBox();
		GL11.glPopMatrix();

	}

	public GuiTextField getTextField() {
		return textField;
	}

	public void setDefault() {
		this.textField.setText(defaultText);
	}

	@Override
	public void onMouseClick(int mouseButton) {
		if (textField != null && isHover()) {

			if (onClickFoucus != null) {
				onClickFoucus.accept(this);
			}
			if (this.getTextField().getText().equals(defaultText)) {
				this.getTextField().setText("");
			}
			this.getTextField().setFocused(true);

		}
	}

	@Override
	public void onTick() {
		if (textField != null)
			this.textField.updateCursorCounter();
	}

	@Override
	public void onKeyInput(char typedChar, int keyCode) {

		if (onPressKey != null) {
			onPressKey.accept(typedChar, keyCode);
		}

		if (keyCode == 28 || keyCode == 56) {
			if (onPressEnter != null) {
				onPressEnter.accept(this);
			}
		}

		if (textField != null) {
			if (onlyNumber && (!Character.isDigit(typedChar) && !isSpecialKey(keyCode))) {
				return;
			}
			this.textField.textboxKeyTyped(typedChar, keyCode);
		}
	}

	private static int[] SPECIAL_KEYS = new int[] { 14, 28, 42, 29 };

	public boolean isSpecialKey(int keyCode) {
		for (int i : SPECIAL_KEYS)
			if (i == keyCode)
				return true;
		return false;
	}

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

}