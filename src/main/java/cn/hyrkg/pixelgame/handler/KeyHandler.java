package cn.hyrkg.pixelgame.handler;

import java.util.UUID;

import org.lwjgl.input.Keyboard;

import cn.hyrkg.pixelgame.module.audio.music.BackgroundMusicManager;
import cn.hyrkg.pixelgame.module.lottery.csgo.GuiLotteryCsgo;
import cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc.GuiStartFail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeyHandler {
	public static KeyBinding testKey;

	public static void onPreInit() {
		MinecraftForge.EVENT_BUS.register(new KeyHandler());

		testKey = new KeyBinding("pixelcore.test", Keyboard.KEY_NUMPAD9, "pixelcore");

		ClientRegistry.registerKeyBinding(testKey);
	}

	public static void keyInput(InputEvent.KeyInputEvent event) {
		if (testKey.isPressed()) {
			BackgroundMusicManager.instance.stopMusic();
			BackgroundMusicManager.instance.playResource(
					"D://Documents/Develop/HyrKG/GradleProject/PixelCore_1122/run/config/audios/丛林遗迹BGM.flac", false);
//			Minecraft.getMinecraft().displayGuiScreen(new GuiLotteryCsgo(UUID.randomUUID()));
//			Minecraft.getMinecraft().displayGuiScreen(new GuiDialog());
//			Minecraft.getMinecraft().displayGuiScreen(new GuiStartFail(UUID.randomUUID()));
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		keyInput(event);
	}
}