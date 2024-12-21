package cn.hyrkg.pixelgame.module.audio;

import java.io.File;

import com.google.gson.JsonObject;

import cn.hyrkg.pixelgame.core.PixelCoreMod;
import cn.hyrkg.pixelgame.module.audio.music.BackgroundMusicManager;
import cn.hyrkg.pixelgame.network.IJsonPacketHandler;
import cn.hyrkg.pixelgame.network.NetworkPixelCore;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

/**
 * 音效管理器
 */
public class AudioManager implements IJsonPacketHandler {

	private static AudioManager instance;
	// 音频文件路径
	private static File dirAudios;

	public static void init() {
		instance = new AudioManager();
		NetworkPixelCore.registerJsonPacketHandler(instance);
		dirAudios = PixelCoreMod.getConfigDir();

		dirAudios = new File(PixelCoreMod.getConfigDir().getAbsolutePath(), "audios");
		if (!dirAudios.exists()) {
			dirAudios.mkdirs();
		}
	}

	/**
	 * 播放音频
	 */
	public void playAuido(String fileName, boolean looping) {
		File audioFile = getAudioFile(fileName);
		if (!audioFile.exists()) {
			Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT,
					new TextComponentString("[pCore] 找不到音频文件 " + audioFile.getName() + " ,无法正常播放!"));
			return;
		}
		BackgroundMusicManager.instance.playResource(getAudioFile(fileName).getAbsolutePath(), looping);
	}

	/**
	 * 停止音频
	 */
	public void stopAudio() {
		BackgroundMusicManager.instance.stopMusic();
	}

	public File getAudioFile(String fileName) {
		return new File(dirAudios, fileName);
	}

	@Override
	public void handleServerMessage(JsonObject jsonObject) {
		System.out.println(jsonObject);
		if (jsonObject.has("stop")) {
			this.stopAudio();
		}

		if (jsonObject.has("volume")) {
			BackgroundMusicManager.instance.setLastVolume(jsonObject.get("volume").getAsInt());
		}

		if (jsonObject.has("play")) {
			String fileName = jsonObject.get("play").getAsString();
			boolean loop = false;
			if (jsonObject.has("loop")) {
				loop = jsonObject.get("loop").getAsBoolean();
			}
			playAuido(fileName, loop);
		}

	}

	@Override
	public String getKey() {
		return "audio";
	}

	public static AudioManager getInstance() {
		return instance;
	}
}