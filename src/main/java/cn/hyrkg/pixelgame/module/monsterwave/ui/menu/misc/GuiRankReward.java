package cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButtonTextable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.JsonHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

class RewardInfo {
	public String shortName;
	public String title;
	public int state = 0;
	public List<ItemStack> items = Lists.newArrayList();

	public void read(JsonObject json) {
		shortName = json.get("sn").getAsString();
		title = json.get("tt").getAsString();
		state = json.get("state").getAsInt();

		if (json.has("item")) {
			JsonArray array = json.getAsJsonArray("item");
			for (JsonElement element : array) {
				ItemStack item = JsonHelper.unpackJsonItem(element.getAsJsonObject());
				items.add(item);
			}
		}
	}
}

class ComponentRewardItem extends BaseComponent {

	public final RewardInfo info;
	public final GuiRankReward gui;
	private HashMap<BaseComponent, ItemStack> hoveredComp = Maps.newHashMap();

	public ComponentRewardItem(GuiRankReward gui, RewardInfo info) {
		this.gui = gui;
		this.info = info;
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		super.init(fastGuiHandler);
		this.transformSolution.wh(440, 44);
		this.clearComponents();
		HashMap<BaseComponent, ItemStack> hoverMapSwap = Maps.newHashMap();

		ComponentButtonTextable btn = new ComponentButtonTextable(0, 374, 10, 48, 23);
		if (info.state == 0) {
			btn.texEnable(ReadyTex.of(GuiRankReward.tex, 491, 179, 48, 23));
			btn.texHover(ReadyTex.of(GuiRankReward.tex, 491, 179, 48, 23));
			btn.setText("未完成目标");
		} else if (info.state == 2) {
			btn.texEnable(ReadyTex.of(GuiRankReward.tex, 605, 179, 48, 23));
			btn.texHover(ReadyTex.of(GuiRankReward.tex, 605, 179, 48, 23));
			btn.setText("已领取");
		} else if (info.state == 1) {
			btn.texEnable(ReadyTex.of(GuiRankReward.tex, 548, 179, 48, 23));
			btn.texHover(ReadyTex.of(GuiRankReward.tex, 605, 179, 48, 23));
			btn.setText("点击领取");
		}
		btn.whenClick(this::onClick);
		this.addComponent(btn);

		int index = 0;
		for (ItemStack item : info.items) {
			BaseComponent comp = new ComponentButton(0, 65 + index * 29, 11, 23, 22);
			index += 1;
			hoverMapSwap.put(comp, item);
			addComponent(comp);
		}

		hoveredComp = hoverMapSwap;
	}

	public void onClick(ComponentButton btn) {
		if (info.state == 1) {
			gui.msg().add("take", info.shortName).sent();
		}
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		gui.bind(GuiRankReward.tex);
		gui.drawTex(491, 114, 440, 44);

		gui.push("").translate(15, 9).scale2D(3);
//		gui.drawString("§l" + info.title);
		gui.drawString("§l" + info.title);

		int index = 0;
		for (ItemStack item : info.items) {
			GL11.glPushMatrix();
			GL11.glTranslated(65 + index * 29, 11, 0);

			gui.bind(GuiRankReward.tex);
			gui.drawTex(563, 76, 23, 22);

			GL11.glPushMatrix();
			GL11.glTranslated(1, 0, 0);
			GL11.glScaled(1.3, 1.3, 1);
			gui.gui.mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
			if (item.getCount() > 1) {
				GL11.glPushMatrix();
				GL11.glTranslated(15, 7, 0);
				GL11.glScaled(1.3, 1.3, 1);
				gui.gui.drawCenteredString(gui.gui.mc.fontRenderer, "" + item.getCount(), 0, 0, -1);
				GL11.glPopMatrix();

			}
			GL11.glPopMatrix();

			GL11.glPopMatrix();
			index += 1;
		}
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		super.drawAfterAll(gui);

		for (Entry<BaseComponent, ItemStack> entry : hoveredComp.entrySet()) {
			if (entry.getKey().isHover()) {
				gui.gui.drawHoveringText(gui.gui.getItemToolTip(entry.getValue()), gui.getLastMouseX(),
						gui.getLastMouseY());
			}
		}
	}

}

public class GuiRankReward extends BaseFastForgeGui {

	public static Tex tex = Tex.of("monsterwave/rank_reward", 967, 346);

	private List<RewardInfo> infos = null;

	public GuiRankReward(UUID uuidIn) {
		super(uuidIn);
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().wh(472, 327).fitScreen(1).translateToCenter(width, height);

		gui.clearComponents();
		List<RewardInfo> infos = getInfos();
		if (infos != null) {
			int index = 0;
			for (RewardInfo info : infos) {
				ComponentRewardItem comp = new ComponentRewardItem(this, info);
				comp.getTransformSolution().translate(15, 65 + index * 48, 0);
				handler.addComponent(comp);
				index += 1;
			}
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {
		GlStateManager.enableBlend();
		gui.bind(tex);
		gui.drawTex(0, 0, 472, 327);
	}

	public List<RewardInfo> getInfos() {
		if (infos == null) {
			if (getSharedProperty().hasProperty("infos")) {
				JsonArray array = getSharedProperty().get("infos").getAsJsonArray();
				List<RewardInfo> newList = Lists.newArrayList();
				for (JsonElement element : array) {
					RewardInfo info = new RewardInfo();
					info.read(element.getAsJsonObject());
					newList.add(info);
				}
				infos = newList;
			}
		}
		return infos;
	}

	@Override
	public void synProperty(JsonObject obj) {
		super.synProperty(obj);
		infos = null;
		fastInitGui(handler);
	}
}
