package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import net.minecraft.util.ChatAllowedCharacters;

public class ComponentTextArea extends BaseComponent {

	protected String textCurrent = "";
	protected int blinkTick = 0;
	protected boolean blink = false;
	protected int maxLength = 64;
	protected int split = 200;
	protected boolean foucsing = false;
	protected double sc = 1;

	public ComponentTextArea(int w, int h, double sc) {
		this.transformSolution.translate(0, 0, 0).wh(w, h);
		this.sc = sc;

		split = (int) (w * 0.24);
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		super.init(fastGuiHandler);
	}

	public void setSplit(int split) {
		this.split = split;
	}

	public void setTextCurrent(String textCurrent) {
		this.textCurrent = textCurrent;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getTextCurrent() {
		return textCurrent;
	}

	public void setFoucsing(boolean foucsing) {
		this.foucsing = foucsing;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {

		gui.push("text").translate(0, 0).scale2D(4 * sc);
		String endWith = "";
		if (foucsing && blink) {
			endWith = "|";
		}
		gui.drawSplitString(textCurrent + endWith, split);
	}

	@Override
	public void onKeyInput(char typedChar, int keyCode) {

		if (!foucsing)
			return;
		if (keyCode == 28 && getTextCurrent().length() < maxLength) {
			if (!textCurrent.isEmpty() && !textCurrent.endsWith("\n"))
				textCurrent += "\n";
		} else if (keyCode == 14) {
			if (textCurrent.length() > 0) {
				textCurrent = textCurrent.substring(0, textCurrent.length() - 1);
				if (textCurrent.endsWith("\\"))
					textCurrent = textCurrent.substring(0, textCurrent.length() - 1);
				textCurrent = textCurrent.trim();
			}
		} else {

			String text = String.valueOf(typedChar);
			if (!text.isEmpty() && ChatAllowedCharacters.isAllowedCharacter(typedChar)
					&& getTextCurrent().length() < maxLength) {
				textCurrent += text;
				textCurrent = textCurrent.trim();
			}
		}
		super.onKeyInput(typedChar, keyCode);
	}

	@Override
	public void onMouseClick(int mouseButton) {
		super.onMouseClick(mouseButton);
		if (!foucsing && isHover()) {
			setFoucsing(true);
		}
	}

	@Override
	public void onTick() {
		blinkTick += 1;
		if (blinkTick == 10) {
			blinkTick = 0;
			blink = !blink;
		}
		super.onTick();
	}
}
