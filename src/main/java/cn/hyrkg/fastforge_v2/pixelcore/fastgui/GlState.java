package cn.hyrkg.fastforge_v2.pixelcore.fastgui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;

public class GlState {

	private double translateX = 0, translateY = 0, translateZ = 0;
	private double scaleX = 1, scaleY = 1, scaleZ = 1;
	private double rotateX = 0, rotateY = 0, rotateZ = 0;
	private float r = -1, g = -1, b = -1, a = -1;

	public GlState times(int times) {
		this.keepTimes = times;
		return this;
	}

	public GlState scale2D(double scaled) {
		scaleX = scaled;
		scaleY = scaled;
		return this;

	}

	public GlState scale3D(double scaled) {
		scaleX = scaled;
		scaleY = scaled;
		scaleZ = scaled;
		return this;
	}

	public GlState scale(double sx, double sy, double sz) {
		scaleX = sx;
		scaleY = sy;
		scaleZ = sz;
		return this;
	}

	public GlState translate(double x, double y) {
		translate(x, y, 1);
		return this;
	}

	public GlState translate(double x, double y, double z) {
		this.translateX = x;
		this.translateY = y;
		this.translateZ = z;
		return this;
	}

	public GlState rotate(double x, double y, double z) {
		this.rotateX = x;
		this.rotateY = y;
		this.rotateZ = z;
		return this;
	}

	public GlState offset(double x, double y) {
		translate(x, y, 0);
		return this;
	}

	public GlState rgba(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	/**
	 * Controller
	 **/
	private int keepTimes = -9;
	private boolean isStarted = false;

	public void invokeStart() {
		keepTimes -= 1;
		if (!isStarted) {
			GL11.glPushMatrix();
			GL11.glTranslated(translateX, translateY, translateZ);
			GL11.glScaled(scaleX, scaleY, scaleZ);

			if (rotateX != 0) {
				GL11.glRotated(rotateX, 1, 0, 0);
			}
			if (rotateY != 0) {
				GL11.glRotated(rotateY, 0, 1, 0);
			}
			if (rotateZ != 0) {
				GL11.glRotated(rotateZ, 0, 0, 1);
			}

			if (r != -1) {
				GlStateManager.color(r, g, b, a);
			}
			isStarted = true;
		}
	}

	public boolean checkEnd() {
		if (keepTimes <= -9)
			return false;
		keepTimes -= 1;
		if (keepTimes <= 0)
			return true;
		return false;
	}

	protected void invokeEnd() {
		GL11.glPopMatrix();
		if (r != -1) {
			GlStateManager.color(1, 1, 1, 1);
		}
	}

}
