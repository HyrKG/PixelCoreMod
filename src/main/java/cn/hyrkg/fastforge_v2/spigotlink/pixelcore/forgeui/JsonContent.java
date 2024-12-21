package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.lang.reflect.Constructor;
import java.util.UUID;

import com.google.gson.JsonArray;

public class JsonContent<T> {
	private final Class tClass;
	private Constructor shaderConstructor = null;

	public final SharedProperty property;
	public final String key;

	protected boolean flagEmptyStringReturn = false;

	public JsonContent(SharedProperty property, String key, Class returnClazz) {
		this.property = property;
		this.key = key;

		tClass = returnClazz;

		try {
			if (PropertyShader.class.isAssignableFrom(tClass)) {
				shaderConstructor = tClass.getConstructor(SharedProperty.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean has() {
		return property.hasProperty(key);
	}

	public void set(T value) {
		if (value != null && value instanceof UUID)
			property.setProperty(key, ((UUID) value).toString());
		else
			property.setProperty(key, value);
	}

	public T get() {
		if (tClass.equals(String.class))
			return (T) getString();
		else if (tClass.equals(Integer.class))
			return (T) getInt();
		else if (tClass.equals(Double.class))
			return (T) getDouble();
		else if (tClass.equals(Float.class))
			return (T) getFloat();
		else if (tClass.equals(Long.class))
			return (T) getLong();
		else if (tClass.equals(Boolean.class))
			return (T) getBoolean();
		if (tClass.equals(JsonArray.class))
			return (T) getJsonArray();
		else if (tClass.equals(UUID.class)) {
			return ((T) UUID.fromString(getString()));
		} else if (PropertyShader.class.isAssignableFrom(tClass)) {
			SharedProperty theProperty = property.getAsProperty(key);
			if (theProperty != null) {
				try {
					return (T) shaderConstructor.newInstance(theProperty);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

//		return null;
		return (T) getString();
	}

	public String getString() {
		if (!flagEmptyStringReturn) {
			return property.getAsString(key);
		} else {
			if (property.hasProperty(key))
				return property.getAsString(key);
			else
				return "";
		}
	}

	public Integer getInt() {
		return property.getAsInt(key);
	}

	public Double getDouble() {
		return property.getAsDouble(key);
	}

	public Float getFloat() {
		return property.getAsFloat(key);
	}

	public Long getLong() {
		return property.getAsLong(key);
	}

	public Boolean getBoolean() {
		return property.getAsBool(key);
	}

	public JsonArray getJsonArray() {
		if (has())
			return property.getAsJsonArray(key);
		else
			return new JsonArray();
	}

	public JsonContent setFlagEmptyStringReturn(boolean flagEmptyStringReturn) {
		this.flagEmptyStringReturn = flagEmptyStringReturn;
		return this;
	}
}
