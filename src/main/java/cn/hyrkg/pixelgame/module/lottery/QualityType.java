package cn.hyrkg.pixelgame.module.lottery;

import java.awt.Color;
import java.util.HashMap;

/**
 * 质量类型
 */
public enum QualityType {
	LEGEND(410, 857, new Color(242, 101, 34)), EPIC(409, 1081, new Color(222, 0, 255)),
	RARE(409, 969, new Color(74, 178, 255)), COMMON(634, 857, new Color(255, 255, 255));

	public final int x, y;
	public final Color color;

	private QualityType(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	private static HashMap<String, QualityType> map = new HashMap<String, QualityType>();

	static {
		for (QualityType type : QualityType.values()) {
			map.put(type.name(), type);
		}
	}

	public static QualityType getByNameOrCommon(String name) {
		return map.getOrDefault(name, QualityType.COMMON);
	}
}
