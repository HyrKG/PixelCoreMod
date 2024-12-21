package cn.hyrkg.pixelgame.core.lib;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import cn.hyrkg.pixelgame.util.MovingSoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.FMLClientHandler;

public class LibSounds {
	public static int size = 0;

	private static ConcurrentHashMap<String, ISound> soundMap = new ConcurrentHashMap();

	private static HashMap<String, SoundEvent> soundEventMap = new HashMap<>();

	public static void play(SoundEvent sound) {
		Minecraft.getMinecraft().player.playSound(sound, 1f, 1f);
	}

	public static void play(String name) {
		play(get(name));
	}

	private static SoundEvent register(String name) {
		ResourceLocation location = new ResourceLocation(LibPixelCoreMod.MODULE_ID, name);
		SoundEvent event = new SoundEvent(location);

		size++;
		return event;
	}

	public static SoundEvent get(String name) {
		if (!soundEventMap.containsKey(name)) {
			soundEventMap.put(name, register(name));
		}
		return soundEventMap.get(name);

	}

	public static void stopSound(String key) {
		if (soundMap.containsKey(key)) {
			FMLClientHandler.instance().getClient().getSoundHandler().stopSound(soundMap.get(key));
			soundMap.remove(key);
		}
	}

	public static void playMovingSound(String key, EntityPlayer player, String soundName) {
		ISound sound = new MovingSoundPlayer(player, get(soundName), SoundCategory.PLAYERS);
		FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
		soundMap.put(key, sound);
	}
}
