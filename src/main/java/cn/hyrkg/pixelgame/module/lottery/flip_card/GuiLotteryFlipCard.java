package cn.hyrkg.pixelgame.module.lottery.flip_card;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.DrawHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.JsonHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import cn.hyrkg.pixelgame.core.lib.LibSounds;
import cn.hyrkg.pixelgame.module.lottery.LotteryPrize;
import cn.hyrkg.pixelgame.module.lottery.QualityType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiLotteryFlipCard extends BaseFastForgeGui {

	private static final Tex TEX_FLIP_CARD = Tex.of("lottery/lottery_flip_card");

	private HashMap<Integer, CompCard> cardMap = new HashMap<Integer, CompCard>();

	// 是否至少开过一次奖
	private boolean isDrawn = false;

	private String icon = "card1";
	private boolean forceDraw = false;
	private List<String> leftTitleList = null, rightTitleList;

	public GuiLotteryFlipCard(UUID uuidIn) {
		super(uuidIn);
	}

	@Override
	public void synProperty(JsonObject obj) {
		super.synProperty(obj);

		Minecraft mc = Minecraft.getMinecraft();

		// 解析json
		JsonObject json = getSharedProperty().getCompleteJson();
		if (json.has("icon")) {
			icon = json.get("icon").getAsString();
		}
		if (json.has("force_draw")) {
			forceDraw = json.get("force_draw").getAsBoolean();
		}

		if (getSharedProperty().hasProperty("left_title")) {
			String text = getSharedProperty().getAsString("left_title");
			leftTitleList = mc.fontRenderer.listFormattedStringToWidth(text, 999);
		}

		if (getSharedProperty().hasProperty("right_title")) {
			String text = getSharedProperty().getAsString("right_title");
			rightTitleList = mc.fontRenderer.listFormattedStringToWidth(text, 999);
		}
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		// 放置组件
		gui.getTransformSolution().wh(533, 325).fitScaledScreen(1.7f).translateToCenter(width, height);
		gui.clearComponents();

		ComponentButton closeBtn = null;
		gui.addComponent(closeBtn = new ComponentButton(-1, 207, 253, 119, 26)
				.texSelected(ReadyTex.of(TEX_FLIP_CARD, 0, 0, 0, 0))
				.texHover(ReadyTex.of(TEX_FLIP_CARD, 7, 372, 119, 29))
				.texEnable(ReadyTex.of(TEX_FLIP_CARD, 7, 336, 119, 29)));
		closeBtn.whenClick(this::onClick);
		closeBtn.setSelected(!isDrawn);

		for (int i = 0; i < 10; i++) {
			int row = i % 5;
			int col = i / 5;

			CompCard compCard;
			if (cardMap.containsKey(i)) {
				gui.addComponent(compCard = cardMap.get(i));
			} else {
				gui.addComponent(compCard = new CompCard(this, i, 95 + row * 150, 135 + col * 135));
			}
			compCard.whenClick(this::onClick);
			cardMap.put(i, compCard);
		}
	}

	public void onClick(ComponentButton btn) {
		if (btn.id == -1 && !btn.isSelected()) {
			close();
		} else if (btn instanceof CompCard) {
			CompCard card = (CompCard) btn;
			if (card.getAniClickCounter().percentage() < 0.2) {
				return;
			}
			this.msg().add("draw", btn.id).sent();
		}
	}

	@Override
	public void onMessage(JsonObject jsonObject) {

		if (jsonObject.has("result")) {
			LibSounds.stopSound("flip_card");
			LibSounds.playMovingSound("flip_card", Minecraft.getMinecraft().player, "flip_card");

			int selected = jsonObject.get("selected").getAsInt();

			JsonArray array = jsonObject.getAsJsonArray("result");
			for (JsonElement element : array) {
				JsonObject itemJson = element.getAsJsonObject();
				ItemStack item = JsonHelper.unpackJsonItem(itemJson);
				LotteryPrize prize = new LotteryPrize(
						QualityType.getByNameOrCommon(itemJson.get("qualityType").getAsString()), item);
				if (!item.hasTagCompound()) {
					item.setTagCompound(new NBTTagCompound());
				}
				item.getTagCompound().setInteger("HideFlags", 2);

				int index = itemJson.get("index").getAsInt();

				new Thread(() -> {
					int distance = Math.min(3, caculateDistance(10, 5, selected, index));
					try {
						Thread.sleep(100 * distance);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					cardMap.get(index).setLotteryPrize(prize, selected == index);
				}).start();
			}
			new Thread(() -> {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isDrawn = true;
				fastInitGui(handler);
			}).start();
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {

		Color color = new Color(0, 0, 0, 0.1f);

		gui.drawRect(gui.getTransformSolution().width, gui.getTransformSolution().height, color.getRGB());
		double heightWeight = 0.65;

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		float disX = DrawHelper.getDistanceXFromCenter(gui);
		float disY = DrawHelper.getDistanceYFromCenter(gui);

		gui.bind(TEX_FLIP_CARD);
		gui.drawTex(0, 0, 533, 325);

		if (leftTitleList != null) {
			for (int i = 0; i < leftTitleList.size(); i++) {
				gui.push().translate(61, 263 - leftTitleList.size() * 3 + i * 9);
				gui.drawString(leftTitleList.get(i));
			}
		}

		if (rightTitleList != null) {
			int maxWidth = 0;
			for (String str : rightTitleList) {
				maxWidth = Math.max(maxWidth, mc.fontRenderer.getStringWidth(str));
			}
			for (int i = 0; i < rightTitleList.size(); i++) {
				gui.push().translate(480 - maxWidth, 263 - rightTitleList.size() * 3 + i * 9);
				gui.drawString(rightTitleList.get(i));
			}
		}

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (forceDraw && !isDrawn && keyCode == 1) {
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}

	public String getIcon() {
		return icon;
	}

	public static int caculateDistance(int size, int perRow, int x0, int x1) {
		// 计算x0的行和列
		int x0Row = x0 / perRow;
		int x0Col = x0 % perRow;

		// 计算x1的行和列
		int x1Row = x1 / perRow;
		int x1Col = x1 % perRow;

		// 曼哈顿距离 = 行之间的差值 + 列之间的差值
		return Math.abs(x1Row - x0Row) + Math.abs(x1Col - x0Col);
	}

}
