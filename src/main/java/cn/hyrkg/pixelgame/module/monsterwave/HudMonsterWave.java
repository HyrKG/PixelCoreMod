package cn.hyrkg.pixelgame.module.monsterwave;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.pixelgame.util.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class HudMonsterWave {

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	//////////////////////////////////////////////////
	public static Tex tex = Tex.of("monsterwave/widgets", 1051, 663);
	private long lastUpdate = -1;

	private int gemSize = 1;
	private float gemHealthProgress = -1;

	private int timeState = 0; // 0 to start,1 to end
	private long timeEnd = -1;

	private long respawnTime = -1;

	public void handlePacket(JsonObject json) {
		lastUpdate = System.currentTimeMillis();
		if (json.has("reset")) {
			reset();
		}

		if (json.has("time")) {
			timeState = json.get("time").getAsInt();
			int sec = json.get("sec").getAsInt();
			timeEnd = System.currentTimeMillis() + sec * 1000;
		}

		if (json.has("respawn")) {
			int timeLeft = json.get("respawn").getAsInt();
			respawnTime = System.currentTimeMillis() + timeLeft * 1000;
		}

		if (json.has("gem")) {
			gemHealthProgress = json.get("gem").getAsFloat();
			if (json.has("size")) {
				gemSize = json.get("size").getAsInt();
			}
		}

	}

	public void reset() {
		lastUpdate = -1;
		gemHealthProgress = -1;

		timeState = 0;
		timeEnd = -1;
		respawnTime = -1;
	}

	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent e) {
		reset();
	}

	@SubscribeEvent
	public void drawHud(RenderGameOverlayEvent.Pre event) {
		long currentTime = System.currentTimeMillis();

		if (currentTime - lastUpdate > 5000) {
			return;
		}

		if (event.getType() != ElementType.HOTBAR) {
			return;
		}

		GuiIngameForge ingameGui = (GuiIngameForge) Minecraft.getMinecraft().ingameGUI;
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.player == null)
			return;

		int width = ingameGui.getResolution().getScaledWidth();
		int height = ingameGui.getResolution().getScaledHeight();

		double scaledGlobal = 1;
		double scAutofit = (double) width / 1980d;
		scaledGlobal *= scAutofit * 1.5;

		GlStateManager.enableBlend();

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 0);
		GL11.glScaled(scaledGlobal, scaledGlobal, 1);

		// to start or to end
		if (currentTime < timeEnd) {
			GL11.glPushMatrix();
			mc.renderEngine.bindTexture(tex.getTexture());
			if (timeState == 0) {
				GL11.glTranslated((width / 2) * 1 / scaledGlobal - 299 / 2, 0, 0);
				Gui.drawModalRectWithCustomSizedTexture(0, 0, 7, 431, 299, 87, 1051, 663);
			} else {
				GL11.glTranslated((width / 2) * 1 / scaledGlobal - 450 / 2, 0, 0);
				Gui.drawModalRectWithCustomSizedTexture(0, 0, 7, 544, 299, 87, 1051, 663);
				if (gemHealthProgress != -1) {
					// draw gem
					GL11.glPushMatrix();
					GL11.glTranslated(310, 0, 0);
					GL11.glScaled(0.8, 0.8, 1);
					mc.renderEngine.bindTexture(tex.getTexture());
					Gui.drawModalRectWithCustomSizedTexture(0, 0, 8, 4, 129, 65, 1051, 663);

					// gem hp
					GL11.glPushMatrix();
					GL11.glTranslated(85, 35, 0);
					GL11.glScaled(3, 3, 1);
					if (gemHealthProgress > 0.2f) {
						ingameGui.drawCenteredString(mc.fontRenderer, "§l§o" + (int) (gemHealthProgress * 100), 0, 0,
								-1);
					} else {
						ingameGui.drawCenteredString(mc.fontRenderer, "§c§l§o" + (int) (gemHealthProgress * 100), 0, 0,
								-1);

					}
					GL11.glPopMatrix();

					// gem szie
					GL11.glPushMatrix();
					GL11.glTranslated(48, 45, 0);
					GL11.glScaled(2.5, 2.5, 1);
					ingameGui.drawCenteredString(mc.fontRenderer, "§l§ox" + (int) (gemSize), 0, 0, -1);

					GL11.glPopMatrix();

					GL11.glPopMatrix();
				}
			}

			// gem time
			GL11.glPushMatrix();
			GL11.glTranslated(226, 10, 0);
			GL11.glScaled(2, 2, 1);
			ingameGui.drawCenteredString(mc.fontRenderer,
					"§l§o" + TimeUtil.getTimeLeftAsChinese((timeEnd - currentTime) / 1000), 0, 0, -1);
			GL11.glPopMatrix();

			GL11.glPopMatrix();
		}

		if (currentTime < respawnTime) {
			// draw respawn
			GL11.glPushMatrix();
			GL11.glTranslated((width / 2) * 1 / scaledGlobal - 352 / 2, (height / 2) * 1 / scaledGlobal - 250 / 2, 0);
			mc.renderEngine.bindTexture(tex.getTexture());
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 394, 400, 352, 190, 1051, 663);

			// gem time
			GL11.glPushMatrix();
			GL11.glTranslated(175, 152, 0);
			GL11.glScaled(3, 3, 1);
			ingameGui.drawCenteredString(mc.fontRenderer,
					"§l§o" + TimeUtil.getTimeLeftAsChinese((respawnTime - currentTime) / 1000), 0, 0, -1);
			GL11.glPopMatrix();

			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();

	}

}
