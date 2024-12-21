package cn.hyrkg.pixelgame.module.monsterwave.ui.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PropertyShader;
import net.minecraft.client.renderer.GlStateManager;

public class GuiMwWinRank extends BaseFastForgeGui {

	public static Tex tex = Tex.of("monsterwave/win_rank", 1566, 539);
	private TickCounter tick = new TickCounter(5, false);

	public GuiMwWinRank(UUID uuidIn) {
		super(uuidIn);
	}

	public GuiMwWinRank() {
		this(UUID.randomUUID());
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		tick.reset();
		gui.getTransformSolution().wh(1015, 488).fitScreen(1.25f).translateToCenter(width, height);
	}

	@Override
	public void draw(FastGuiHandler gui) {
		gui.bind(tex);
		gui.drawTex(0, 0, 1015, 488);

		float aniProgress = tick.percentage(gui.getPartialTicks());

		List<String> rankList = getList();
		for (int i = 0; i < 13; i++) {
			GL11.glPushMatrix();
			GL11.glTranslated(80 + i * 66, 71 + 15 - Math.min(1, aniProgress + (12 - i) * 0.05f) * 15, 0);
			GlStateManager.color(1, 1, 1, 0.7f + 0.3f * aniProgress);
			if (rankList.size() > i) {
				String name = rankList.get(i);
				int rank = nameToRankMap.get(name);
				drawFlagBackground(gui, i + 1);

				gui.push("").translate(33, 108).scale2D(1.5);
				gui.drawCenteredStringWithShadow(name);

				gui.push("").translate(33, 178).scale2D(2.5);
				gui.drawCenteredStringWithShadow(String.valueOf(rank));

			} else {
				drawFlagBackground(gui, -1);
			}

			GL11.glPopMatrix();
		}
		GlStateManager.enableBlend();
	}

	public void drawFlagBackground(FastGuiHandler gui, int rank) {
		gui.bind(tex);
		if (rank == -1) {
			gui.drawTex(1386, 10, 64, 397);
		} else {
			gui.drawTex(1078 + Math.min((rank - 1), 3) * 77, 10, 64, 397);
			gui.push("").translate(32, 8).scale2D(2);
			gui.drawCenteredStringWithShadow("§o" + rank);
		}
	}

	@Override
	public void tick() {
		super.tick();
		tick.tick();
	}

	//////////////////////////////////////////////////
	// Property Controller
	/////////////////////////////////////////////////
	private HashMap<String, Integer> nameToRankMap = null;
	private List<String> sortedList = null;

	public List<String> getList() {
		if (sortedList != null) {
			return sortedList;
		}
		nameToRankMap = new HashMap<>();
		sortedList = new ArrayList<>();
		if (getSharedProperty().hasProperty("rank")) {
			JsonObject rankJson = getSharedProperty().get("rank").getAsJsonObject();
			try {
				for (Entry<String, JsonElement> entry : rankJson.entrySet()) {
					nameToRankMap.put(entry.getKey(), entry.getValue().getAsInt());
					sortedList.add(entry.getKey());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sortedList.sort((j, k) -> nameToRankMap.get(k).compareTo(nameToRankMap.get(j)));
		return sortedList;
	}

	@Override
	public void synProperty(JsonObject obj) {
		super.synProperty(obj);
		sortedList = null;
		nameToRankMap = null;
		getList();
		fastInitGui(handler);
	}

}
