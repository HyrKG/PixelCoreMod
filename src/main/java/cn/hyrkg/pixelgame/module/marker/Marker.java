package cn.hyrkg.pixelgame.module.marker;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.JsonContent;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PropertyShader;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SharedProperty;

public class Marker extends PropertyShader {

	/* 移除距离，当玩家小于此距离，则移除。 */
	public int removeDistance = 3;

	public final JsonContent<String> worldName = cStr("wn"), shortName = cStr("sn"), markerName = cStr("mkn");
	public final JsonContent<Double> x = cDouble("x");
	public final JsonContent<Double> y = cDouble("y");
	public final JsonContent<Double> z = cDouble("z");

	public double cx, cy, cz;

	public Marker(String worldName, String makerShortName, String markerName, double x, double y, double z) {
		super(new SharedProperty());

		this.worldName.set(worldName);
		this.shortName.set(makerShortName);
		this.markerName.set(markerName);
	}

	public Marker(SharedProperty property) {
		super(property);
	}

	public Marker(JsonObject jsonObject) {
		super(new SharedProperty(jsonObject));
		cx = x.get();
		cy = y.get();
		cz = z.get();
	}

}
