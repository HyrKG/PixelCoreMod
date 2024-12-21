package cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class StandardGuiFactory implements IForgeGuiFactory {

	private HashMap<String, Class<? extends IForgeGui>> indexMap = new HashMap<>();
	private HashMap<Class<? extends IForgeGui>, Constructor<IForgeGui>> constructorMap = new HashMap<>();

	@Override
	public IForgeGui createAndDisplay(String gui, UUID uuid, JsonObject property) {

		if (!indexMap.containsKey(gui)) {
			System.out.println("索引不存在> " + gui);
			return null;
		}
		Class<? extends IForgeGui> clazz = indexMap.get(gui);
		Constructor<IForgeGui> constructor = constructorMap.get(clazz);
		if (constructor == null) {
			System.out.println("构造函数不存在> " + gui);
			return null;
		}

		try {

			IForgeGui instanceForgeGui = constructor.newInstance(new Object[] { uuid });

			instanceForgeGui.synProperty(property);

			if (instanceForgeGui instanceof GuiScreen) {
				Minecraft.getMinecraft().displayGuiScreen((GuiScreen) instanceForgeGui);
			}
			return instanceForgeGui;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("初始化错误> " + gui);
		}

		return null;
	}

	public void registerClazz(Class<? extends IForgeGui> clazz, String... keys) {
		try {

			Constructor constructor = clazz.getDeclaredConstructor(UUID.class);
			constructor.setAccessible(true);

			constructorMap.put(clazz, constructor);

			for (String str : keys) {
				indexMap.put(str, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
