package cn.hyrkg.pixelgame.module.audio.music;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is used to handle client-side events of the mod.
 * 
 * @author Yuti
 *
 */
@EventBusSubscriber(value = Side.CLIENT)
public class ClientHandler {

	private static boolean stopMusicDelayedState = false;

	private static final BackgroundMusicManager manager = BackgroundMusicManager.instance;

	/**
	 * Stops the BGM when the player leave the server (or the singleplayer world)
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		manager.stopMusic();
	}

	/**
	 * Handles configuration update. It will update the volume set in the
	 * configuration. If the player disable the mod, it will stop the music, if it's
	 * reactivated, it will ask for a refresh on server-side.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onConfigUpdate(ConfigChangedEvent.PostConfigChangedEvent event) {

	}

	/**
	 * Updates mod's volume if Minecraft's master volume is updated.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onMasterVolumeUpdated(GuiScreenEvent.MouseInputEvent.Post event) {
		if (event.getGui() instanceof GuiScreenOptionsSounds) {
			manager.updateConfiguredMusicVolume();
		}
	
	}

	/**
	 * Detects when a Minecraft's music is playing in order to stopping it if the
	 * mod's music player is currently playing a music. The music can't be stopped
	 * here because it's not loaded into the miencraft's sound engine yet. That's
	 * why the action needs to be delayed.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onMinecraftMusicStarts(PlaySoundEvent event) {
		ISound sound = event.getSound();
		if (sound != null && sound.getCategory() == SoundCategory.MUSIC) {
			if (manager.isPlayerActive()) {
				stopMusicDelayedState = true;
			}
		}
	}

	/**
	 * Stop minecraft's music if the action as been requested (see
	 * {@link ClientHandler#onMinecraftMusicStarts(PlaySoundEvent)})
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void cancelMinecraftMusic(PlayerTickEvent event) {
		if (stopMusicDelayedState) {
			manager.stopMinecraftMusic();
			stopMusicDelayedState = false;
		}
	}
}
