package cn.hyrkg.pixelgame.module.hud;

import org.lwjgl.input.Mouse;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGui;
import cn.hyrkg.pixelgame.module.hud.toast.ToastManager;
import cn.hyrkg.pixelgame.network.IJsonPacketHandler;
import cn.hyrkg.pixelgame.network.NetworkPixelCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ModuleHud implements IJsonPacketHandler {
	public static final String CHANNEL_HUD = "pc_hud";
	private static ModuleHud instance;

	private final ToastManager toastManager = new ToastManager();

	// 最后的服务器UID，如果UID不相同，则重置所有再执行。
	private String lastServerUid = null;

	public static void onPreInit() {
		instance = new ModuleHud();
		MinecraftForge.EVENT_BUS.register(instance);
		NetworkPixelCore.registerJsonPacketHandler(instance);
	}

	@Override
	public void handleServerMessage(JsonObject jsonObject) {

		if (jsonObject.has("uid")) {
			String uid = jsonObject.get("uid").getAsString();
			if (lastServerUid != null && !lastServerUid.equals(uid)) {
				resetAll();
			}
			lastServerUid = uid;
		}

		if (jsonObject.has("toast")) {
			toastManager.handleToastPacket(jsonObject.get("toast").getAsJsonObject());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onDrawOverload(RenderGameOverlayEvent e) {

		if (e.getType() != ElementType.HOTBAR) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null) {
			return;
		}
		GuiIngameForge ingame = (GuiIngameForge) mc.ingameGUI;
		if (ingame == null) {
			return;
		}
		FastGui gui = new FastGui(mc, ingame);
		gui.width = ingame.getResolution().getScaledWidth();
		gui.height = ingame.getResolution().getScaledHeight();
		gui.lastMouseX = Mouse.getX() * gui.width / mc.displayWidth;
		gui.lastMouseY = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;
		gui.partialTicks = e.getPartialTicks();
		toastManager.draw(gui);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onUpdate(ClientTickEvent event) {
		if (event.phase == Phase.END) {
			return;
		}
		toastManager.update();
	}

	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent event) {
		lastServerUid = null;
		resetAll();
	}

	public void resetAll() {
		toastManager.reset();
	}

	@Override
	public String getKey() {
		return CHANNEL_HUD;
	}
}
