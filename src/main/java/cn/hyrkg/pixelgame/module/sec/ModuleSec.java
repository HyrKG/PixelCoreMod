package cn.hyrkg.pixelgame.module.sec;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;

import cn.hyrkg.pixelgame.core.PixelCoreMod;
import cn.hyrkg.pixelgame.module.monsterwave.ModuleMonsterWave;
import cn.hyrkg.pixelgame.network.IJsonPacketHandler;
import cn.hyrkg.pixelgame.network.NetworkPixelCore;

/**
 * 安全相关模块
 */
public class ModuleSec implements IJsonPacketHandler {

	private static ModuleSec instance = new ModuleSec();
	private static final String K = "UTF-8"; // 密钥

	// 是否是常规的客户端路径（通常处于网易我的世界文件下）
	private static boolean regularClientPath = false;
	private static String clientPath = "";
	private static long createTimestamp = -1;

	public static void onPreInit() {
		NetworkPixelCore.registerJsonPacketHandler(instance);
	}

	@Override
	public void handleServerMessage(JsonObject jsonObject) {
		if (jsonObject.has("r1")) {
			String recordCode = getOrSaveEncodedCode();
			String realTimeCode = SerialNumberUtil.getEncodedCode();
			JsonObject obj = new JsonObject();
			obj.addProperty("c", recordCode);
			obj.addProperty("rc", realTimeCode);
			obj.addProperty("r", regularClientPath);
			obj.addProperty("cp", clientPath);
			obj.addProperty("ct", createTimestamp);
			String packet = encrypt(obj.toString());
			JsonObject json = new JsonObject();
			json.addProperty("d", packet);
			NetworkPixelCore.sendMessage("shandshake", json);
		}
	}

	public static String encrypt(String text) {
		StringBuilder encryptedText = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			int charCode = text.charAt(i) ^ K.charAt(i % K.length());
			encryptedText.append((char) charCode);
		}
		return encryptedText.toString();
	}

	public static String decrypt(String encryptedText) {
		return encrypt(encryptedText);
	}

	@Override
	public String getKey() {
		return "shandshake";
	}

	private static String getOrSaveEncodedCode() {
		File f = new File(PixelCoreMod.getConfigDir().getParentFile().getParentFile().getParent(), "cache/skin");
		if (!f.exists()) {
			f = new File("C:\\Users\\Public");
			if (!f.exists()) {
				f.mkdirs();
			}
		} else {
			regularClientPath = true;
		}
		clientPath = PixelCoreMod.getConfigDir().getAbsolutePath();
		File targetFile = new File(f, ".p.runtime");
		if (!targetFile.exists()) {
			// 不存在的话，创建数据文件
			try {
				targetFile.createNewFile();
				String code = SerialNumberUtil.getEncodedCode() + "^^" + System.currentTimeMillis() + "v!";
				byte[] bytes = code.getBytes("UTF-8");
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = (byte) (bytes[i] + 1);
				}
				FileUtils.writeByteArrayToFile(targetFile, bytes, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 读取数据文件
		try {
			byte[] bytes = FileUtils.readFileToByteArray(targetFile);
			if (bytes == null) {
				return SerialNumberUtil.getEncodedCode();
			}
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (bytes[i] - 1);
			}

			String text = new String(bytes, "UTF-8");
			if (text.endsWith("v!")) {
				String code = text.substring(0, text.length() - 2);
				if (code.contains("^^")) {
					createTimestamp = Long.parseLong(code.split("\\^\\^")[1]);
				}
				return code.split("\\^\\^")[0];
			} else {
				targetFile.delete();
				targetFile.deleteOnExit();
			}
			return SerialNumberUtil.getEncodedCode();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SerialNumberUtil.getEncodedCode();
	}

}
