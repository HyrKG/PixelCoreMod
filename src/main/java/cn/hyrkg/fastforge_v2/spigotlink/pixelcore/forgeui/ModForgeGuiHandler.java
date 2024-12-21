package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonObject;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModForgeGuiHandler {
	private static HashMap<String, IForgeGuiFactory> guiFactoryMap = new HashMap<>();

	private static StandardGuiFactory standardGuiFactory = null;

	private static IForgeGui lastViewingGui = null;

	public static final String CHANNEL_FORGE_GUI = "ffg";

	public static SimpleNetworkWrapper forguiNet;

	public static void init(String index) {

		standardGuiFactory = new StandardGuiFactory();

		forguiNet = NetworkRegistry.INSTANCE.newSimpleChannel(index + "_" + CHANNEL_FORGE_GUI);
		forguiNet.registerMessage(ReactorForgeGui.MessageHandler.class, PacketMessage.class, '@', Side.CLIENT);

	}

	public static void sendMessage(JsonObject json) {
		forguiNet.sendToServer(new PacketMessage(json.toString()));
	}

	public static void handlePacket(JsonObject json) {

		String uuid = json.get("uuid").getAsString();

		if (json.has("gui")) {
			if (lastViewingGui != null && lastViewingGui.getUUID().toString().equals(uuid)) {

				JsonObject update = json.getAsJsonObject("property");
				lastViewingGui.synProperty(update);

			} else {
				lastViewingGui = null;
				// is create packet
				String gui = json.get("gui").getAsString();
				if (guiFactoryMap.containsKey(gui)) {
					IForgeGui newForgeGui = guiFactoryMap.get(gui).createAndDisplay(gui, UUID.fromString(uuid),
							json.getAsJsonObject("property"));
					lastViewingGui = newForgeGui;
				}
			}
		}

		if (json.has("update")) {
			JsonObject updates = json.getAsJsonObject("update");

			if (lastViewingGui != null && lastViewingGui.getUUID().toString().equalsIgnoreCase(uuid)) {
				lastViewingGui.synProperty(updates);
			}
		}

		if (json.has("close")) {
			if (lastViewingGui != null && lastViewingGui.getUUID().toString().equalsIgnoreCase(uuid)) {
				lastViewingGui.closeGuiSilently();
				lastViewingGui = null;
			}
		}

		if (json.has("msg")) {
			if (lastViewingGui != null && lastViewingGui.getUUID().toString().equalsIgnoreCase(uuid)) {
				lastViewingGui.onMessage(json.getAsJsonObject("msg"));
			}
		}
	}

	public static void registerGuiFactory(IForgeGuiFactory guiFactory, String... keys) {
		for (String key : keys)
			guiFactoryMap.put(key, guiFactory);
	}

	public static void registerStandardGui(Class<? extends IForgeGui> clazz, String... keys) {
		standardGuiFactory.registerClazz(clazz, keys);
		registerGuiFactory(standardGuiFactory, keys);
	}

	public static void sendCloseNotify(IForgeGui forgeGui) {
		if (lastViewingGui != null && lastViewingGui == forgeGui)
			lastViewingGui = null;
		JsonObject closePacket = new JsonObject();
		closePacket.addProperty("uuid", forgeGui.getUUID().toString());
		closePacket.addProperty("close", 0);
		sendMessage(closePacket);
	}

	public static void bindForgeGui(IForgeGui gui) {
		lastViewingGui = gui;
	}

	public static void sendMessage(SimpleMsg msg, IForgeGui forgeGui) {
		try {
			JsonObject msgObj = new JsonObject();
			msgObj.addProperty("uuid", forgeGui.getUUID().toString());
			msgObj.add("msg", msg.getJsonObj());
			sendMessage(msgObj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
