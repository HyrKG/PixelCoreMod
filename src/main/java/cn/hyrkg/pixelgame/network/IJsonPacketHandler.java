package cn.hyrkg.pixelgame.network;

import com.google.gson.JsonObject;

public interface IJsonPacketHandler {
	String getKey();

	default void handleClientMessage(JsonObject jsonObject) {
		throw new RuntimeException("unhandled client message of" + jsonObject);
	}

	default void handleServerMessage(JsonObject jsonObject) {
		throw new RuntimeException("unhandled server message of" + jsonObject);
	}
}
