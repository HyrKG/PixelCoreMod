package cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc;

import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import net.minecraft.client.renderer.GlStateManager;

class PlayerInfo {
	public String name;
	public int kills;
	public int repires;
	public int score;

	public JsonObject save() {
		JsonObject json = new JsonObject();
		json.addProperty("n", name);
		json.addProperty("k", kills);
		json.addProperty("r", repires);
		json.addProperty("s", score);
		return json;
	}

	public void read(JsonObject json) {
		name = json.get("n").getAsString();
		kills = json.get("k").getAsInt();
		repires = json.get("r").getAsInt();
		score = json.get("s").getAsInt();
	}
}

public class GuiGameResult extends BaseFastForgeGui {
	public static Tex tex = Tex.of("monsterwave/game_result", 1216, 578);
	private List<PlayerInfo> scoreList = null;
	private Boolean success = null;

	public GuiGameResult(UUID uuidIn) {
		super(uuidIn);
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().wh(429, 507).fitScreen(1f).translateToCenter(width, height);

		if (isSuccess()) {
			gui.addComponent(new ComponentButton(0, 220, 96, 176, 36).texHover(ReadyTex.of(tex, 17, 531, 176, 36))
					.whenClick(it -> this.msg().add("reward", 1).sent()));
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {
		GlStateManager.enableBlend();
		gui.bind(tex);

		if (!isSuccess()) {
			gui.drawTex(17, 12, 429, 507);
		} else {
			gui.push("").translate(-159, -1);
			gui.drawTex(454, 13, 744, 515);

			gui.push("").translate(220, 96);
			gui.drawTex(212, 531, 176, 36);

		}

		List<PlayerInfo> list = getScoreList();
		if (list != null) {
			int index = 0;
			for (PlayerInfo info : list) {
				if (index > 9)
					break;

				gui.bind(tex);
				GL11.glPushMatrix();
				GL11.glTranslated(19, 200 + index * 29.3, 0);

				gui.drawTex(413, 539, 391, 28);

				gui.push("").translate(18.5, 5).scale2D(2);
				gui.drawCenteredString(String.valueOf(index + 1));

				gui.push("").translate(50, 5).scale2D(2);
				gui.drawString(info.name);

				gui.push("").translate(183, 5).scale2D(2);
				gui.drawString(String.valueOf(info.kills));

				gui.push("").translate(255, 5).scale2D(2);
				gui.drawString(String.valueOf(info.score));

				gui.push("").translate(325, 5).scale2D(2);
				gui.drawString(String.valueOf(info.repires));

				GL11.glPopMatrix();

				index += 1;
			}
		}
	}

	public List<PlayerInfo> getScoreList() {
		if (scoreList == null) {
			if (getSharedProperty().hasProperty("scores")) {
				List<PlayerInfo> infos = Lists.newArrayList();

				for (JsonElement element : getSharedProperty().get("scores").getAsJsonArray()) {
					PlayerInfo info = new PlayerInfo();
					info.read(element.getAsJsonObject());
					infos.add(info);
				}
				scoreList = infos;
			}
		}
		return scoreList;
	}

	public boolean isSuccess() {
		if (success == null) {
			if (getSharedProperty().hasProperty("success")) {
				success = getSharedProperty().get("success").getAsBoolean();
			} else {
				return false;
			}
		}
		return success;
	}

}
