package cn.hyrkg.pixelgame.util;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class MovingSoundPlayer extends MovingSound {

	private final EntityPlayer player;

	public MovingSoundPlayer(EntityPlayer player, SoundEvent soundIn, SoundCategory categoryIn) {
		super(soundIn, categoryIn);
		this.player = player;

		setRepeat(false, 0);
	}

	public MovingSoundPlayer setRepeat(boolean flag, int repeatDealy) {
		this.repeat = flag;
		this.repeatDelay = repeatDealy;
		return this;
	}

	@Override
	public void update() {
		this.xPosF = (float) this.player.posX;
		this.yPosF = (float) this.player.posY;
		this.zPosF = (float) this.player.posZ;

		this.volume = 1f;
	}

}
