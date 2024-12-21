package cn.hyrkg.pixelgame.module.monsterwave.ui.menu.misc;

import java.util.UUID;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import net.minecraft.client.renderer.GlStateManager;

public class GuiStartFail extends BaseFastForgeGui {
	public static Tex tex = Tex.of("monsterwave/widgets", 1051, 663);

	public GuiStartFail(UUID uuidIn) {
		super(uuidIn);
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().wh(453, 288).fitScreen(1.3f).translateToCenter(width, height);
	}

	@Override
	public void draw(FastGuiHandler gui) {
		GlStateManager.enableBlend();
		gui.bind(tex);
		gui.drawTex(7, 94, 453, 288);
	}

}
