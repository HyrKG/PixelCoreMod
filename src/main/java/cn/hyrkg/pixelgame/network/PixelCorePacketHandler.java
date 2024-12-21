package cn.hyrkg.pixelgame.network;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PacketMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PixelCorePacketHandler {
	protected static HashMap<String, IJsonPacketHandler> jsonHandlerMap = new HashMap<>();

	public static void handleServerCmd(String str) {
		try {
			JsonParser parser = new JsonParser();
			parser.parse(str);

			JsonObject json = (JsonObject) parser.parse(str);

			String cmd = json.get("$c").getAsString();

			if (jsonHandlerMap.containsKey(cmd)) {
				jsonHandlerMap.get(cmd).handleServerMessage(json);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error message: " + str);
		}
	}

	public static class MessageHandler implements IMessageHandler<PacketMessage, IMessage> {
		@Override
		public IMessage onMessage(PacketMessage message, MessageContext ctx) {
			handleServerCmd(message.msg);
			return message;
		}
	}
}
