package cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;

public class HoverCheck {

	public String text = null;

	public boolean display = true;
	public boolean enable = true;
	public boolean alwayDisplay = false;

	public int color = 1694498815;

	public final double sc;
	public int id, x, y, width, height;

	public int scaleX, scaleY, scaleW, scaleH;

	protected int offsetX = 0, offsetY = 0;

	public Runnable exHoverDraw = null;

	private boolean debugMode = false;

	public HoverCheck(int id, int x, int y, int width, int height, double sc) {
		this.id = id;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sc = sc;

		scaleX = (int) ((double) x * sc);
		scaleY = (int) ((double) y * sc);
		scaleW = (int) ((double) width * sc);
		scaleH = (int) ((double) height * sc);

	}

	public void xy(int x, int y) {
		this.x = x;
		this.y = y;
		scaleX = (int) (x * sc);
		scaleY = (int) (y * sc);
	}

	public void wh(int w, int h) {
		this.width = w;
		this.height = h;
		scaleW = (int) (width * sc);
		scaleH = (int) (height * sc);
	}

	public HoverCheck debug() {
		debugMode = true;
		return this;
	}

	public HoverCheck offset(int x, int y) {
		this.offsetX = x;
		this.offsetY = y;
		return this;
	}

	public HoverCheck color(Color color) {
		this.color = color.getRGB();
		return this;
	}

	public HoverCheck color(int color) {
		this.color = color;
		return this;
	}

	public HoverCheck exHoverDraw(Runnable run) {
		exHoverDraw = run;
		return this;
	}

	public boolean isHover(int mouseX, int mouseY) {
		mouseX -= offsetX;
		if (!(mouseX > (scaleX) && mouseX < (scaleX + scaleW))) {
			return false;
		}
		mouseY -= offsetY;
		if (!(mouseY > (scaleY) && mouseY < (scaleY + scaleH)))
			return false;
		return true;
	}

	public void draw(GuiScreen screen, int mouseX, int mouseY) {

		if (debugMode) {
			drawHover(screen);
		} else if (display) {
			if (alwayDisplay) {
				drawHover(screen);
			} else if (enable && isHover(mouseX, mouseY)) {
				drawHover(screen);
			}
		}
	}

	public void drawHover(GuiScreen screen) {
		drawHover(screen, color);
	}

	public void drawHover(GuiScreen screen, int customColor) {
		int sx = offsetX + scaleX;
		int sy = offsetY + scaleY;
		screen.drawRect(sx, sy, sx + scaleW, sy + scaleH, customColor);
		if (exHoverDraw != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(sx, sy, 0);
			exHoverDraw.run();
			GL11.glPopMatrix();
		}
	}

	public void playClickSound(GuiScreen gui) {
	}

}
