package cn.hyrkg.pixelgame.network;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PacketMessage;
import cn.hyrkg.pixelgame.core.lib.LibPixelCoreMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkPixelCore {

	public static SimpleNetworkWrapper network;

	public static void onInit() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(LibPixelCoreMod.PIXEL_CHANNEL);
		network.registerMessage(PixelCorePacketHandler.MessageHandler.class, PacketMessage.class, '@', Side.CLIENT);
	}

	public static void registerJsonPacketHandler(IJsonPacketHandler jsonPacketHandler) {
		PixelCorePacketHandler.jsonHandlerMap.put(jsonPacketHandler.getKey(), jsonPacketHandler);
	}

	public static void sendMessage(String command, JsonObject json) {
		json.addProperty("$c", command);
		network.sendToServer(new PacketMessage(json.toString()));
	}

}
