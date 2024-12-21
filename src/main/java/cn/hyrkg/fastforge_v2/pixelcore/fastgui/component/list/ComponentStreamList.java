package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.stramer.PacketStreamer;

/**
 * 具有流特性的列表
 */
public class ComponentStreamList<T> extends ComponentList<T> {

	public long lastReachTopRequest = -1;
	public long lastReachTopInterval = 1000;

	public PacketStreamer<T> stramer;

	public BiFunction<Integer, T, ComponentListButton<T>> createListFunction = null;
	protected Consumer<JsonObject> consumerRequestPacket = null;

	public ComponentStreamList(int x, int y, int width, int height, PacketStreamer<T> stremer) {
		super(x, y, width, height);
		this.stramer = stremer;
		this.stramer.setOnFlush(this::onStreamerFlush);
		this.footerInfo = true;

	}

	public void initStreamer(BiFunction<Integer, T, ComponentListButton<T>> createListFunction,
			Consumer<JsonObject> consumerPacketRequest) {
		this.createListFunction = createListFunction;
		this.consumerRequestPacket = consumerPacketRequest;
	}

	public void setup() {
		onStreamerFlush(stramer);

	}

	public void onStreamerFlush(PacketStreamer<T> stramer) {
		// refreash all
		if (createListFunction != null) {
			List<ComponentListButton<T>> newContentList = new ArrayList<>();

			List<T> listByOrder = stramer.getContentByOrder();

			int index = 0;
			for (T t : listByOrder) {
				ComponentListButton<T> result = createListFunction.apply(index++, t);
				if (result != null) {
					newContentList.add(result);
				}
			}

			this.setup(newContentList);
		}
	}

	public PacketStreamer<T> getStramer() {
		return stramer;
	}

	/*
	 * 当触碰到顶端时，尝试更新
	 */
	public void onReachTop() {
		// prevent flush too quick
		if (lastReachTopRequest == -1 || (System.currentTimeMillis() - lastReachTopRequest) >= lastReachTopInterval) {
			this.requestNext();
			lastReachTopRequest = System.currentTimeMillis();
		}
	}

	public void requestNext() {
		if (consumerRequestPacket != null) {
			consumerRequestPacket.accept(stramer.requestNext());
		}
	}

	@Override
	public void checkAndCorrectY() {
		if (offsetY > 0)
			offsetY = 0;
		else if (offsetY < -getPageItemMax() * interval) {
			offsetY = -getPageItemMax() * interval;
			onReachTop();
		}
		init(fastGuiHandler);
	}
}
