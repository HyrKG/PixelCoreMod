package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.stramer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

public class PacketStreamer<T> {

	/**
	 * 流转化器的UUID。通常情况下或许用不着
	 */
	protected UUID uuid;

	/**
	 * 主要内容，同步或需要同步的内容存储于此。
	 * <p>
	 * 内容将会被解码并缓存于此。
	 */
	protected HashMap<Integer, T> content = new HashMap<>();

	/**
	 * 最大索引，当达到索引上限后不会再索引
	 */
	protected int indexMax = -1;

	/**
	 * 内容是否发生改变，当前用于判断在客户端包后内容是否更新
	 */
	protected boolean isContentChanged = false;

	/**
	 * ########### 基础构造方法 ###########
	 */
	public PacketStreamer(UUID uuid) {
		this.uuid = uuid;
	}

	public PacketStreamer() {
		this.uuid = UUID.randomUUID();
	}

	/*
	 * ########### 变量调用方法 ###########
	 */

	/**
	 * 设置当前流转换器的UUID
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * 设置步长
	 */
	public PacketStreamer<T> setIndexStep(int indexStep) {
		this.indexStep = indexStep;
		return this;
	}

	/**
	 * 设置编译器
	 */
	public PacketStreamer<T> setObjectEncoder(Function<T, JsonObject> objectEncoder) {
		this.objectEncoder = objectEncoder;
		return this;
	}

	/**
	 * 设置解码器
	 */
	public PacketStreamer<T> setObjectParser(Function<JsonObject, T> objectParser) {
		this.objectParser = objectParser;
		return this;
	}

	/**
	 * 判断是否能够继续进行索引/是否到头
	 */
	public boolean hasNext(int indexNow) {
		return indexMax == -1 || indexNow <= indexMax;
	}

	/**
	 * 判断是否能够继续进行索引/是否到头
	 */
	public boolean hasNext() {
		return hasNext(content.size());
	}

	public boolean isRecentlyChanged() {
		return isContentChanged;
	}

	/**
	 * 获取所有内容
	 */
	public HashMap<Integer, T> getContent() {
		return content;
	}

	/**
	 * 获取所有内容，根据传递顺序排序
	 */
	public List<T> getContentByOrder() {
		List<Integer> keyList = new ArrayList<>();
		keyList.addAll(getContent().keySet());
		java.util.Collections.sort(keyList);

		return keyList.stream().map(content::get).collect(Collectors.toList());
	}

	/*
	 * ############################### Client Side ###############################
	 */

	/*
	 * 客户端刷新时执行
	 */
	protected Consumer<PacketStreamer<T>> onFlush = null;
	protected Function<JsonObject, T> objectParser = null;

	public PacketStreamer<T> setOnFlush(Consumer<PacketStreamer<T>> onFlush) {
		this.onFlush = onFlush;
		return this;
	}

	public JsonObject requestNext() {
		return request(content.size(), indexStep);
	}

	public JsonObject request(int startIndex, int length) {
		JsonObject json = new JsonObject();
		json.addProperty("$uid", uuid.toString());
		json.addProperty("$index", startIndex);

		return json;
	}

	public JsonObject requestAll() {
		JsonObject json = request(content.size(), indexStep);
		json.addProperty("$all", 0);
		return json;
	}

	public boolean handlerServerPacket(JsonObject jsonPacket) {
		if (!isStreamerPacket(jsonPacket))
			return false;
		int index = jsonPacket.get("$index").getAsInt();

		if (jsonPacket.has("$maxlength")) {
			int serverIndexMax = jsonPacket.get("$maxlength").getAsInt();
			this.indexMax = serverIndexMax;
		}

		// make sure can read
		if (hasNext() && jsonPacket.has("$ct")) {
			JsonObject contentJson = jsonPacket.getAsJsonObject("$ct");
			contentJson.entrySet().forEach(j -> {
				content.put(Integer.parseInt(j.getKey()), objectParser.apply(j.getValue().getAsJsonObject()));
			});
			isContentChanged = true;
		} else {
			isContentChanged = false;
		}

		if (onFlush != null)
			onFlush.accept(this);

		if (jsonPacket.has("$all") && hasNext()) {
			return true;
		}
		return false;
	}

	/*
	 * ############################### Server Side ###############################
	 */

	// packet sender
	protected Function<T, JsonObject> objectEncoder = null;
	protected BiFunction<Integer, Integer, List<T>> objectProvider = null;
	protected int indexStep = 10;

	public void setupContent(List<T> content) {
		this.content.clear();
		int i = 0;
		for (T t : content) {
			this.content.put(i++, t);
		}
		indexMax = content.size() - 1;
	}

	public PacketStreamer<T> setObjectProvider(BiFunction<Integer, Integer, List<T>> objectProvider) {
		this.objectProvider = objectProvider;
		return this;
	}

	public JsonObject respondClientPacket(JsonObject jsonPacket) {
		// check valid
		if (!isStreamerPacket(jsonPacket))
			return null;

		// generate packet and send it back

		int index = jsonPacket.get("$index").getAsInt();
		if (index == -1)
			index = 0;
//        int length = jsonPacket.get("$length").getAsInt();

		JsonObject json = jsonPacket;
		json.addProperty("$maxlength", indexMax);

		if (objectProvider == null) {
			if (hasNext(index)) {
				int indexEnd = index + this.indexStep;
				// fix index end
				if (indexEnd > indexMax + 1)
					indexEnd = indexMax + 1;
				int realLength = indexEnd - index;
				jsonPacket.addProperty("$length", realLength);

				JsonObject contentObj = new JsonObject();
				for (int i = 0; i < realLength; i++) {
					if (content.containsKey(index + i)) {
						contentObj.add(String.valueOf(index + i), objectEncoder.apply(this.content.get(index + i)));
					}
				}
				json.add("$ct", contentObj);
			}
		} else {
			List<T> objectResult = objectProvider.apply(index, indexStep);
			if (objectResult != null && !objectResult.isEmpty()) {
				int indexOffset = 0;
				JsonObject contentObj = new JsonObject();

				for (T t : objectResult) {
					contentObj.add(String.valueOf(index + indexOffset), objectEncoder.apply(t));
					indexOffset += 1;
				}
				json.add("$ct", contentObj);
			}
		}
		return json;
	}

	/**
	 * 快速构造方法
	 */
	public static <T> PacketStreamer<T> asClient(Function<JsonObject, T> parser) {
		PacketStreamer<T> streamer = new PacketStreamer<>();
		streamer.setObjectParser(parser);
		return streamer;
	}

	public static <T> PacketStreamer<T> asServer(Function<T, JsonObject> encoder, List<T> content) {
		PacketStreamer<T> streamer = new PacketStreamer<>();
		streamer.setObjectEncoder(encoder);
		streamer.setupContent(content);
		return streamer;
	}

	public static <T> PacketStreamer<T> asServer(Function<T, JsonObject> encoder,
			BiFunction<Integer, Integer, List<T>> objectProvider) {
		PacketStreamer<T> streamer = new PacketStreamer<>();
		streamer.setObjectEncoder(encoder);
		streamer.setObjectProvider(objectProvider);
		return streamer;
	}

	public static boolean isStreamerPacket(JsonObject jsonPacket) {
		return jsonPacket.has("$index");
	}
}