package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMessage implements IMessage {

	public String msg;

	public PacketMessage() {
	}

	public PacketMessage(String msg) {
		this.msg = msg;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);

		try {
			msg = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try {
			buf.writeBytes(msg.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
