package cn.hyrkg.pixelgame.module.hud.toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGui;
import cn.hyrkg.pixelgame.dto.hud.ToastPacket;
import cn.hyrkg.pixelgame.module.hud.DurableItem;
import cn.hyrkg.pixelgame.module.hud.IHudComponent;

public class ToastManager implements IHudComponent {
	private static final Gson gson = new GsonBuilder().create();
	private Map<String, DurableItem<Toast>> toastMap = new LinkedHashMap();
	private double lastOffsetHeight = -1;
	private double targetOffsetHeight = -1;


	public void draw(FastGui gui) {

		if (toastMap.isEmpty()) {
			return;
		}

		List<DurableItem<Toast>> values = new ArrayList(toastMap.values());

		int index = 0;
		double totalHeight = values.size() * (23) * gui.computeScaleHeight();
		targetOffsetHeight = totalHeight;
		if (lastOffsetHeight == -1) {
			lastOffsetHeight = targetOffsetHeight;
		}
		double offsetHeight = lastOffsetHeight + ((targetOffsetHeight - lastOffsetHeight) * gui.partialTicks);
		for (DurableItem<Toast> item : values) {
			Toast toast = item.item;
			GL11.glPushMatrix();
			GL11.glTranslated(0, -offsetHeight / 2 + index * 23 * gui.computeScaleHeight(), 0);

			float progress = 1f;

			if (item.totalDurability != -1) {
				if (Math.abs(item.totalDurability - item.durability) < 10) {
					progress = (float) Math.abs(item.totalDurability - item.durability) / 10f;
				} else if (Math.abs(item.durability) < 10) {
					progress = (float) Math.abs(item.durability) / 10f;
				}
				progress = Math.max(progress, 0.1f);
			}

			toast.draw(gui, progress);
			GL11.glPopMatrix();
			index += 1;
		}
	}

	@Override
	public void update() {
		if (lastOffsetHeight < targetOffsetHeight) {
			lastOffsetHeight += 10;
			if (lastOffsetHeight > targetOffsetHeight) {
				lastOffsetHeight = targetOffsetHeight;
			}
		} else if (lastOffsetHeight > targetOffsetHeight) {
			lastOffsetHeight -= 10;
			if (lastOffsetHeight < targetOffsetHeight) {
				lastOffsetHeight = targetOffsetHeight;
			}
		}

		for (Map.Entry<String, DurableItem<Toast>> entry : new HashMap<String, DurableItem<Toast>>(toastMap)
				.entrySet()) {
			if (entry.getValue().durability != -1) {
				if (--entry.getValue().durability == 0) {
					toastMap.remove(entry.getKey());
					continue;
				}
			}
			entry.getValue().item.update();
		}
	}

	public void reset() {
		toastMap.clear();
	}

	public void handleToastPacket(JsonObject packet) {
		if (packet.has("clear")) {
			toastMap.clear();
		}
		if (packet.has("remove")) {
			for (JsonElement element : packet.getAsJsonArray("remove")) {
				String sn = element.getAsString();
				toastMap.remove(sn);
			}
		}
		if (packet.has("add")) {
			for (JsonElement element : packet.getAsJsonArray("add")) {
				ToastPacket toastPacket = gson.fromJson(element.getAsString(), ToastPacket.class);
				if (toastPacket.type == null) {
					continue;
				}
				Toast toast = gson.fromJson(toastPacket.j, toastPacket.type.getInstanceClass());
				DurableItem<Toast> dItem = DurableItem.of(toast, toastPacket.d);
				String sn = toastPacket.sn == null ? toast.getText() : toastPacket.sn;
				toastMap.put(toastPacket.sn, dItem);
			}
		}
	}

}
