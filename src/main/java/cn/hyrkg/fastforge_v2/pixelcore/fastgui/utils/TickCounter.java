package cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils;

public class TickCounter {

	public final int tickMax;
	public final boolean loop;
	public int tick = 0;
	public float lastTick = 0;

	public TickCounter(int max, boolean loop) {
		this.tickMax = max;
		this.loop = loop;
	}

	public void reset() {
		tick = 0;
		lastTick = 0;
	}

	public boolean isDone() {
		return tick >= tickMax;
	}

	public boolean isTicking() {
		return tick > 0 && tick < tickMax;
	}

	public void tick() {
		lastTick = tick;
		if (tick < tickMax) {
			tick += 1;
		} else if (loop) {
			tick = 0;
			lastTick = tick;
		}
	}

	public void tickBack() {
		if (tick > 0)
			tick -= 1;
	}

	public float percentage() {
		return (float) tick / (float) tickMax;
	}

	public float percentage(float partialTicks) {

		float newTick = lastTick + (tick - lastTick) * partialTicks;
		float per = (float) newTick / (float) tickMax;

		if (per > 1)
			per = 1;
		if (per < 0)
			per = 0;
		return per;
	}
}
