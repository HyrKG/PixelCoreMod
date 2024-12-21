package cn.hyrkg.pixelgame.module.lottery.csgo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentScissorPanel;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.JsonHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import cn.hyrkg.pixelgame.module.lottery.LotteryPrize;
import cn.hyrkg.pixelgame.module.lottery.QualityType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiLotteryCsgo extends BaseFastForgeGui {

	public static Tex tex = Tex.of("lottery/lottery", 1623, 1263);

	public List<LotteryPrize> prizePool = new ArrayList<LotteryPrize>();
	private CompLotteryScrollView compLotteryScroll = new CompLotteryScrollView(this);

	private ComponentButton btnConfirm;

	public GuiLotteryCsgo(UUID uuidIn) {
		super(uuidIn);
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		Minecraft.getMinecraft().gameSettings.hideGUI = true;
		gui.clearComponents();

		gui.getTransformSolution().wh(1220, 795).fitScaledScreen(0.8f).translateToCenter(width, height);
		gui.addComponent(compLotteryScroll);

		gui.addComponent(btnConfirm = new ComponentButton(0, 464, 510, 297, 85));
		btnConfirm.texHover(ReadyTex.of(tex, 0, 0, 0, 0));
		btnConfirm.whenClick(this::onClick);
	}

	public void onClick(ComponentButton btn) {
		close();
	}

	@Override
	public void draw(FastGuiHandler gui) {

		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		gui.bind(tex);
		gui.drawTex(0, 0, 1220, 795);

		if (compLotteryScroll.isCompleted()) {
			gui.push(null).translate(464, 510, 0);
			if (btnConfirm != null && btnConfirm.isHover()) {
				gui.drawTex(40, 866, 297, 85);
			} else {
				gui.drawTex(40, 969, 297, 85);
			}
		}

	}

	@Override
	public void onGuiClosed() {
		Minecraft.getMinecraft().gameSettings.hideGUI = false;
		super.onGuiClosed();
	}

	@Override
	public void synProperty(JsonObject obj) {
		super.synProperty(obj);
		if (obj.has("pool")) {
			JsonArray array = obj.getAsJsonArray("pool");
			List<LotteryPrize> prizePool = new ArrayList<LotteryPrize>();
			for (JsonElement element : array) {
				JsonObject itemJson = element.getAsJsonObject();
				ItemStack item = JsonHelper.unpackJsonItem(itemJson);
				prizePool.add(new LotteryPrize(QualityType.getByNameOrCommon(itemJson.get("qualityType").getAsString()),
						item));
				if (!item.hasTagCompound()) {
					item.setTagCompound(new NBTTagCompound());
				}
				item.getTagCompound().setInteger("HideFlags", 2);
			}
			this.prizePool = prizePool;
		}
	}

}
