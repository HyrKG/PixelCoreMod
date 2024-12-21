package cn.hyrkg.pixelgame.module.monsterwave.ui.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PropertyShader;
import net.minecraft.client.renderer.GlStateManager;

public class GuiMwScoreRank extends BaseFastForgeGui {

	public static Tex tex = Tex.of("monsterwave/score_rank", 857, 698);

	public boolean isLoaded = false;
	public String selected = null;

	private HashMap<String, ComponentButton> regionCompMap = new HashMap<>();

	public GuiMwScoreRank(UUID uuidIn) {
		super(uuidIn);
	}

	public GuiMwScoreRank() {
		this(UUID.randomUUID());
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().wh(848, 488).fitScreen(1.25f).translateToCenter(width, height);

		gui.clearComponents();

		List<String> regins = getRegions();
		if (regins != null) {
			int index = 0;
			for (String region : regins) {
				if (selected == null) {
					selected = region;
				}

				ComponentButton btn = new ComponentButton(index, 62, (int) (68d + index * 30.5), 189, 29);
				btn.whenClick(it -> onSideBarClick(region, it));
				btn.texHover(ReadyTex.of(tex, 0, 0, 0, 0));
				btn.texSelected(ReadyTex.of(tex, 0, 0, 0, 0));
				if (selected != null && region.equals(selected)) {
					btn.setSelected(true);
				}
				handler.addComponent(btn);
				regionCompMap.put(region, btn);
				index += 1;

			}
		}

		if (selected != null && getRegionMap(selected) == null) {
			this.msg().add("request-rank", selected).sent();
		}
	}

	public void onSideBarClick(String region, ComponentButton btn) {
		selected = region;
		fastInitGui(handler);
	}

	@Override
	public void draw(FastGuiHandler gui) {
		GlStateManager.enableBlend();
		gui.bind(tex);
		gui.drawTex(0, 0, 848, 488);

		List<String> regins = getRegions();
		if (regins != null) {
			int index = 0;
			for (String str : regins) {
				gui.bind(tex);
				GL11.glPushMatrix();
				GL11.glTranslated(62, 68 + index * 30.5, 0);

				gui.push("").translate(0, 0);
				if (regionCompMap.containsKey(str)
						&& (regionCompMap.get(str).isSelected() || regionCompMap.get(str).isHover())) {
					gui.drawTex(37, 580, 189, 29);
				} else {
					gui.drawTex(37, 540, 189, 29);
				}

				gui.push("").translate(10, 6).scale2D(1.8d);
				gui.drawString("§f" + str + "玩家排行");

				GL11.glPopMatrix();
				index += 1;

				GlStateManager.color(1, 1, 1);
			}
		}

		if (selected != null) {
			Map<String, Integer> map = getRegionMap(selected);
			if (map != null) {
				int index = 0;
				for (Entry<String, Integer> entry : map.entrySet()) {
					gui.bind(tex);

					GL11.glPushMatrix();
					GL11.glTranslated(277, 100 + index * 30.5, 0);
					drawBackground(index + 1, gui);

					gui.push("").translate(21, 6).scale2D(2);
					gui.drawCenteredString(String.valueOf(index + 1));

					gui.push("").translate(48, 6).scale2D(2);
					gui.drawString(entry.getKey());

					gui.push("").translate(233, 6).scale2D(2);
					gui.drawString(String.valueOf(entry.getValue()));

					GL11.glPopMatrix();
					index += 1;
				}
			}
		}
	}

	public void drawBackground(int rank, FastGuiHandler gui) {
		gui.drawTex(257, 540 + Math.min(rank - 1, 3) * 32, 501, 28);
	}

	//////////////////////////////////////////
	//
	// Property
	//
	//////////////////////////////////////////

	private List<String> regions = null;
	private HashMap<String, LinkedHashMap<String, Integer>> regionRankMap = Maps.newHashMap();

	public List<String> getRegions() {
		if (isLoaded && regions == null) {
			if (!getSharedProperty().hasProperty("regions")) {
				return null;
			}
			List<String> regionList = Lists.newArrayList();
			JsonArray jsonObj = getSharedProperty().get("regions").getAsJsonArray();
			for (JsonElement element : jsonObj) {
				regionList.add(element.getAsString());
			}
			regions = regionList;
		}
		return regions;
	}

	public HashMap<String, Integer> getRegionMap(String region) {
		if (!regionRankMap.containsKey(region)) {
			return null;
		}
		return regionRankMap.get(region);
	}

	public void reset() {
		regions = null;
	}

	@Override
	public void synProperty(JsonObject obj) {
		super.synProperty(obj);
		isLoaded = true;
		fastInitGui(handler);
	}

	@Override
	public void onMessage(JsonObject jsonObject) {
		super.onMessage(jsonObject);

		if (jsonObject.has("region-rank")) {
			String region = jsonObject.get("region").getAsString();

			LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
			HashMap<String, Integer> cache = new HashMap<>();
			List<String> list = Lists.newArrayList();

			JsonObject rankJson = jsonObject.getAsJsonObject("region-rank");
			for (Entry<String, JsonElement> entry : rankJson.entrySet()) {
				list.add(entry.getKey());
				cache.put(entry.getKey(), entry.getValue().getAsInt());
			}
			list.sort((j, k) -> cache.get(k).compareTo(cache.get(j)));
			for (String key : list) {
				result.put(key, cache.get(key));
			}
			regionRankMap.put(region, result);
		}
	}
}
