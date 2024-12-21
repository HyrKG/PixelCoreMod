package cn.hyrkg.pixelgame.module.monsterwave.ui.menu;

import java.util.UUID;

import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButtonTextable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import cn.hyrkg.pixelgame.module.monsterwave.ModuleMonsterWave;
import net.minecraft.client.Minecraft;

public class GuiMonsterWaveMainMenu extends BaseFastForgeGui {

	public static Tex tex = Tex.of("monsterwave/main_menu", 829, 514);

	public GuiMonsterWaveMainMenu(UUID uuidIn) {
		super(uuidIn);
	}

	public GuiMonsterWaveMainMenu() {
		this(UUID.randomUUID());
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().wh(476, 340).fitScaledScreen(1.7f).translateToCenter(width, height);
		gui.clearComponents();

		// 关闭按钮
		gui.addComponent(new ComponentButton(-1, 420, 81, 29, 29).texHover(ReadyTex.of(tex, 13, 369, 29, 29))
				.whenClick(this::onClickCommonBtn));
		// 问好
		gui.addComponent(new ComponentButtonTextable(0, 27, 81, 29, 29).setText("点此领取上一局奖励")
				.texHover(ReadyTex.of(tex, 50, 369, 29, 29)).whenClick(this::onClickCommonBtn));
		// 开始
		gui.addComponent(new ComponentButton(1, 148, 271, 179, 38).texHover(ReadyTex.of(tex, 238, 423, 179, 38))
				.whenClick(this::onClickCommonBtn));
		// 冠军排行
		gui.addComponent(new ComponentButton(2, 333, 123, 119, 46).texHover(ReadyTex.of(tex, 434, 369, 119, 46))
				.whenClick(this::onClickCommonBtn));
		// 分数排行
		gui.addComponent(new ComponentButton(3, 331, 172, 119, 46).texHover(ReadyTex.of(tex, 435, 423, 119, 46))
				.whenClick(this::onClickCommonBtn));
		// 排行奖励
		gui.addComponent(new ComponentButton(4, 332, 223, 119, 46).texHover(ReadyTex.of(tex, 575, 369, 119, 46))
				.whenClick(this::onClickCommonBtn));

		///////////////////////////////////////

		ComponentButton select1, select2, select3;
		// 不再提示
		gui.addComponent(select1 = new ComponentButtonTextable(0, 23, 123, 119, 46).setText("点击切换<不再提醒>")
				.whenClick(this::onClickSelectBtn).texHover(ReadyTex.of(tex, 0, 0, 0, 0))
				.texSelected(ReadyTex.of(tex, 103, 369, 119, 46)));
		// 关闭/开启提示
		gui.addComponent(select2 = new ComponentButtonTextable(1, 23, 171, 119, 46).setText("点击切换<提示>")
				.whenClick(this::onClickSelectBtn).texHover(ReadyTex.of(tex, 0, 0, 0, 0))
				.texSelected(ReadyTex.of(tex, 103, 423, 119, 46)));
		// 下次再提示我
		gui.addComponent(select3 = new ComponentButtonTextable(2, 23, 219, 119, 46).setText("点击切换<下次再提示>")
				.whenClick(this::onClickSelectBtn).texHover(ReadyTex.of(tex, 0, 0, 0, 0))
				.texSelected(ReadyTex.of(tex, 238, 369, 119, 46)));
		switch (getProperty().getState()) {
		case 0:
			select2.setSelected(true);
			break;
		case 1:
			select2.setSelected(true);
			select3.setSelected(true);
			break;
		case 2:
			select1.setSelected(true);
			break;
		}
	}

	public void onClickSelectBtn(ComponentButton btn) {
		if (!(btn instanceof ComponentButtonTextable))
			return;
		this.msg().add("select-notify", btn.id).sent();
	}

	public void onClickCommonBtn(ComponentButton btn) {
		switch (btn.id) {
		case -1:
			Minecraft.getMinecraft().displayGuiScreen(null);
			break;
		case 0:
			this.msg().add("instant-reward", 1).sent();
			break;
		case 1:
			this.msg().add("join", 1).sent();
			break;
		case 2:
			this.msg().add("win-rank", 1).sent();
			break;
		case 3:
			this.msg().add("score-rank", 1).sent();
			break;
		case 4:
			this.msg().add("rank-reward", 1).sent();
			break;
		default:
			return;
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {
		gui.bind(tex);
		gui.drawTex(0, 0, 476, 340);
	}

	@Override
	public void synProperty(JsonObject obj) {
		super.synProperty(obj);
		fastInitGui(handler);
	}

	public PropertyMwMainMenu getProperty() {
		return getSharedProperty().getAsShader(PropertyMwMainMenu.class);
	}
}
