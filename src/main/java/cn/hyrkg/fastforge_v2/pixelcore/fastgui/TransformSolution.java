package cn.hyrkg.fastforge_v2.pixelcore.fastgui;

import com.ibm.icu.math.BigDecimal;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class TransformSolution {

	public static final TransformSolution EMPTY = new TransformSolution(0, 0, 0, 0);

	public TransformSolution(int x, int y, int w, int h) {
		this.translatedX = x;
		this.translatedY = y;
		this.width = w;
		this.height = h;
	}

	public int width = 0, height = 0;

	public double scaledX = 1, scaledY = 1, scaledZ = 1;
	public double translatedX = 0, translatedY = 0, translatedZ = 0;

	@Override
	public String toString() {
		return
//				Integer.toHexString(hashCode())
//				+ "\npos: " + x + "-" + y + "-" + z 
//				+ 
		"\ntpos: " + cutDouble(translatedX) + "-" + cutDouble(translatedY) + "-" + cutDouble(translatedZ) + "\nsc: "
				+ scaledX + "-" + scaledY + "-" + scaledZ + "\nwh: " + width + "-" + height;
	}

	public double cutDouble(double input) {
		return new BigDecimal(input).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
	}

	public int getX() {
		return (int) ((translatedX) * scaledX);
	}

	public int getY() {
		return (int) ((translatedY) * scaledY);
	}

	public int getZ() {
		return (int) ((translatedZ) * scaledZ);
	}

	public double getWidth() {
		return width * scaledX;
	}

	public double getHeight() {
		return height * scaledY;
	}

	public int getWidthI() {
		return (int) getWidth();
	}

	public int getHeightI() {
		return (int) getHeight();
	}

	public TransformSolution reset() {

		this.width = 0;
		this.height = 0;
		scaledX = 1;
		scaledY = 1;
		scaledZ = 1;
		translatedX = 0;
		translatedY = 0;
		translatedZ = 0;
		return this;
	}

	public TransformSolution fit(ReadyTex tex) {
		this.width = tex.width;
		this.height = tex.height;
		return this;
	}

	public TransformSolution wh(int w, int h) {
		this.width = w;
		this.height = h;
		return this;
	}

	public TransformSolution scale2D(double scale) {
		scaledX = scale;
		scaledY = scale;
		return this;
	}

	public TransformSolution scale3D(double scale) {
		scaledX = scale;
		scaledY = scale;
		scaledZ = scale;
		return this;
	}

	public TransformSolution translate(double x, double y, double z) {
		this.translatedX = x;
		this.translatedY = y;
		this.translatedZ = z;
		return this;
	}

	public TransformSolution translateToCenter(double screenWidth, double screenHeight) {
		double csx = getCenterStartX(screenWidth);
		double csy = getCenterStartY(screenHeight);
		translate(csx, csy, 0);
		return this;
	}

	public TransformSolution translateAdd(double x, double y, double z) {
		this.translatedX += x;
		this.translatedY += y;
		this.translatedZ += z;
		return this;
	}

	public TransformSolution fitScaledScreen(float pertancage) {

		if (Minecraft.getMinecraft() == null)
			return this;
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());

		float f = 2f / (float) scaledresolution.getScaleFactor();

		float scale = Math.min(Minecraft.getMinecraft().displayHeight / 1080f,
				Minecraft.getMinecraft().displayWidth / 1600f);

//		float scale = Minecraft.getMinecraft().displayHeight / 1080f;
		this.scale2D(scale * 0.6 * pertancage * f);
		return this;
	}

	public TransformSolution fitScreen(float pertancage) {
		return fitScaledScreen(pertancage);
	}

	public double getCenterStartX(double screenWidth) {
		return (screenWidth - getWidth()) / 2 * (1 / scaledX);
	}

	public double getCenterStartY(double screenHeight) {
		return (screenHeight - getHeight()) / 2 * (1 / scaledX);
	}

	public static TransformSolution of(int x, int y, int w, int h) {
		return new TransformSolution(x, y, w, h);
	}
}
