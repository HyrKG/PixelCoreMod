package cn.hyrkg.pixelgame.dto.hud;

import java.util.HashMap;

import cn.hyrkg.pixelgame.module.hud.toast.Toast;

public enum ToastType {
	CARD(CardToast.class);

	private final Class<? extends Toast> clazz;

	ToastType(Class<? extends Toast> clazz) {
		this.clazz = clazz;
	}

	private static HashMap<String, ToastType> typeMap = new HashMap<String, ToastType>();
	static {
		for (ToastType type : ToastType.values()) {
			typeMap.put(type.name(), type);
		}
	}

	public static ToastType get(String typeName) {
		return typeMap.get(typeName);
	}
	
	public Class<? extends Toast> getInstanceClass() {
		return clazz;
	}
}
