package cn.hyrkg.pixelgame.client.component;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.LibColor;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import net.minecraft.client.renderer.GlStateManager;

public class ComponentMessageBox extends BaseComponent {
	private static final Tex TEX = Tex.of("component/messagebox");

	public final String msg;

	private List<NamebleRunnable> run = new ArrayList<>();

	public ComponentMessageBox(String msg) {
		this.setSkipDraw(true);
		this.msg = msg;
	}

	public ComponentMessageBox addCallback(String name, Runnable runnable) {
		run.add(new NamebleRunnable(name, runnable));
		return this;
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		this.transformSolution = TransformSolution.of(0, 0, 442, 188).fitScreen(0.75f)
				.translateToCenter(fastGuiHandler.getGuiWidth(), fastGuiHandler.getGuiHeight());

		this.setHoverPrecondition(null);
		this.setSuperComponent(null);

		this.clearComponents();

		if (run.size() > 0) {
			int size = run.size();
			int index = 0;

			for (NamebleRunnable nameRun : run) {
				int x = 145 + index * 200 - (size - 1) * 100;
				int y = 125;

				ComponentButton btn = new ComponentButton(index, x, y, 156, 38).whenClick(this::whenClick);
				this.addComponent(btn);

				index += 1;
			}
		}

	}

	public void whenClick(ComponentButton btn) {
		run.get(btn.id).runnable.run();
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		gui.bind(TEX);
		gui.drawTex(0, 0, 442, 188);
		List<String> strings = gui.mc().fontRenderer.listFormattedStringToWidth(msg, 125);
		int height = (int) (strings.size() * (gui.mc().fontRenderer.FONT_HEIGHT + 3) * 3.2);
		GL11.glPushMatrix();
		GL11.glTranslated(225, 70 - height / 2, 0);
		GL11.glScaled(3.2, 3.2, 1);
		int offsetY = 0;
		for (String s : strings) {
			gui.push().translate(0, offsetY, 0);
			gui.drawCenteredStringWithShadow(s);
			offsetY += gui.mc().fontRenderer.FONT_HEIGHT + 3;
		}
		GL11.glPopMatrix();

		if (run.size() > 0) {
			int size = run.size();

			int index = 0;

			for (NamebleRunnable nameRun : run) {
				GlStateManager.color(1, 1, 1);
				gui.bind(TEX);

				int x = 145 + index * 200 - (size - 1) * 100;
				int y = 125;

				gui.pushKeep("offset").translate(x, y);
				gui.glStateInvokeStart();

				gui.push().scale(1, 0.8, 1);
				gui.drawTex(458, 11, 156, 47);

				gui.push("word").translate(80, 8).scale2D(3d);
				gui.drawCenteredString("§l" + nameRun.name);

				gui.pop("offset");

				index += 1;
			}
		}

	}

	@Override
	public boolean isHover() {
		return true;
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		GL11.glPushMatrix();
		GlStateManager.enableDepth();
		GL11.glTranslated(0, 0, 300);
		gui.drawRect(0, 0, gui.getGuiWidth(), gui.getGuiHeight(), LibColor.black70);
		this.draw(gui);
		GL11.glPopMatrix();
	}

	class NamebleRunnable {
		public final String name;
		public final Runnable runnable;

		public NamebleRunnable(String name, Runnable run) {
			this.name = name;
			this.runnable = run;
		}
	}

}
