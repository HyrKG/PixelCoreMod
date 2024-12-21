package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorForgeGui {
	public static void handleServerCmd(String str) {

		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(str).getAsJsonObject();

			ModForgeGuiHandler.handlePacket(json);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error message: " + str);
		}
	}

	public static class MessageHandler implements IMessageHandler<PacketMessage, IMessage> {
		@Override
		public IMessage onMessage(PacketMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					handleServerCmd(message.msg);
				}
			});
			return null;
		}
	}
}
