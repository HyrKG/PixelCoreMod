package cn.hyrkg.pixelgame.module.marker;

import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.DrawHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.pixelgame.config.PixelGameConfig;
import cn.hyrkg.pixelgame.network.IJsonPacketHandler;
import cn.hyrkg.pixelgame.network.NetworkPixelCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ModuleMarker implements IJsonPacketHandler {
	public static final String CHANNEL_MARKER = "px_marker";

	private static ConcurrentHashMap<String, Marker> markMap = new ConcurrentHashMap<>();

	public static void onPreInit() {
		ModuleMarker instance = new ModuleMarker();
		NetworkPixelCore.registerJsonPacketHandler(instance);
		MinecraftForge.EVENT_BUS.register(instance);
	}

	@Override
	public void handleServerMessage(JsonObject jsonObject) {
		if (jsonObject.has("clear")) {
			markMap.clear();
		}
		if (jsonObject.has("markers")) {
			JsonArray markerArray = jsonObject.getAsJsonArray("markers");
			markerArray.forEach(j -> {
				JsonObject json = j.getAsJsonObject();
				Marker marker = new Marker(json);
				markMap.put(marker.shortName.get(), marker);
			});

		}
	}

	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {
//		test();
		float partialTicks = event.getPartialTicks();

		Minecraft mc = Minecraft.getMinecraft();

		// make sure can render
		if (mc == null || mc.player == null || mc.player.world == null || mc.getRenderManager() == null
				|| mc.getRenderManager().options == null) {
			return;
		}
		// 如果没有标记则不进行渲染
		if (markMap.isEmpty()) {
			return;
		}
		EntityPlayer p = mc.player;

		GL11.glPushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

		GL11.glEnable(0x864F);
		GlStateManager.disableDepth();

		double px = p.prevPosX + (p.posX - p.prevPosX) * partialTicks;
		double py = p.prevPosY + (p.posY - p.prevPosY) * partialTicks;
		double pz = p.prevPosZ + (p.posZ - p.prevPosZ) * partialTicks;

		ResourceLocation tex = Tex.of("marker/marker", 128, 128).getTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();

		for (Marker pos : markMap.values()) {
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.color(1, 1, 1);

			GL11.glPushMatrix();
			double distance = p.getDistance(pos.cx, pos.cy, pos.cz);
			// always facing player
			GL11.glTranslated(pos.cx - px, pos.cy - py + 2, pos.cz - pz);
			GlStateManager.rotate(-(mc.getRenderManager().playerViewY), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-(float) (mc.getRenderManager().options.thirdPersonView == 2 ? -1 : 1)
					* -mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			double sc = 0.0002 * PixelGameConfig.marker_scale * distance;
//			double sc = 0.03 ;

			GL11.glScaled(sc, sc, sc);

			mc.renderEngine.bindTexture(tex);
			int size = 128 * 3;
			DrawHelper.drawModalRectWithCustomSizedTextureC(0, 0, 0, 0, size, size, size, size);

			// draw name and distance
			GL11.glPushMatrix();
			GL11.glTranslated(0, -150, 0);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glScaled(15, 15, 15);

			int distan2Int = (int) distance;
			mc.ingameGUI.drawCenteredString(mc.fontRenderer, "§0" + pos.markerName.get() + "(" + distan2Int + "m)", 0,
					0, -1);
			GL11.glTranslated(-0.2, -0.2, 0.1);
			mc.ingameGUI.drawCenteredString(mc.fontRenderer, "§e" + pos.markerName.get() + "(" + distan2Int + "m)", 0,
					0, -1);

			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}

		GL11.glDisable(0x864F);
		GlStateManager.enableDepth();

		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void onQuit(ClientDisconnectionFromServerEvent e) {
		markMap.clear();

	}

	@Override
	public String getKey() {
		return CHANNEL_MARKER;
	}

}
