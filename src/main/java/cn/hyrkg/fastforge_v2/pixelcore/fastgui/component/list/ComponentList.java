package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentScissorPanel;
import net.minecraft.client.renderer.GlStateManager;

public class ComponentList<T> extends ComponentScissorPanel {

	public class Animate {

		protected float aniSpeed = 10;
		protected float lastOffsetY = 0;
		protected int scroolStayTick = 0;

		public void onTick() {
			if (scroolStayTick > 0)
				scroolStayTick -= 1;
			if (lastOffsetY < offsetY) {
				float diff = (offsetY - lastOffsetY) / stepSize;
				if (diff < 1)
					diff = 1;

				scroolStayTick = 10;
				lastOffsetY += aniSpeed * diff;
				if (lastOffsetY > offsetY) {
					lastOffsetY = offsetY;
				}
			} else if (lastOffsetY > offsetY) {
				float diff = (lastOffsetY - offsetY) / stepSize;
				if (diff < 1)
					diff = 1;
				scroolStayTick = 10;
				lastOffsetY -= aniSpeed * diff;
				if (lastOffsetY <= offsetY) {
					lastOffsetY = offsetY;
				}
			}
		}

		public float getSmoothOffsetY(float partialTicks) {
			return lastOffsetY + ((float) offsetY - lastOffsetY) * partialTicks;
		}

		public boolean isAnimating() {
			if (!enableAnimate)
				return false;
			return lastOffsetY != offsetY;
		}

		public boolean isScrollStay() {
			return scroolStayTick > 0;
		}

		public float getProgress(float partialTicks) {
			return (float) getSmoothOffsetY(partialTicks) / -((float) getPageItemMax() * (float) interval);
		}

	}

	protected BiFunction<Integer, T, ComponentListButton<T>> createListFunction = null;

	protected List<ComponentListButton<T>> buttons = new ArrayList<>();
	protected int interval = 0;
	protected int pageSize = 1;

	protected Animate animate = new Animate();

	protected int offsetY = 0;
	protected int stepSize = 80;

	public boolean enableAnimate = false;
	public boolean footerInfo = false;

	public ComponentList(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public ComponentList setCreateListFunction(BiFunction<Integer, T, ComponentListButton<T>> createListFunction) {
		this.createListFunction = createListFunction;
		return this;
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		this.clearComponents();

		int index = 0;

		for (ComponentListButton<?> btn : buttons) {
			btn.getTransformSolution().translate(0, index * btn.getInterval() + offsetY, 1);
			this.addComponent(btn);

			index += 1;

			if (enableAnimate) {
				// 由该控件接管渲染，以实现平滑移动效果
				btn.setHoverPrecondition(() -> !animate.isAnimating());
				btn.setSkipDraw(true);
			}
		}
	}

	public ComponentList setup(List<ComponentListButton<T>> list) {
		this.buttons = list;
		if (list.isEmpty()) {
			interval = 1;
		} else {
			interval = list.get(0).getInterval();
		}
		pageSize = (int) (this.getTransformSolution().getHeight() / interval);

		this.init(fastGuiHandler);
		return this;
	}

	public ComponentList setupDirect(List<T> list) {
		if (createListFunction == null) {
			return this;
		}

		int index = 0;
		List<ComponentListButton<T>> resultList = new ArrayList<>();
		for (T t : list) {
			resultList.add(createListFunction.apply(index, t));
			index += 1;
		}

		this.setup(resultList);
		setProgress(0);
		return this;
	}

	public ComponentList<T> step(int stepSize, float aniSpeed) {
		this.stepSize = stepSize;
		this.animate.aniSpeed = aniSpeed;
		return this;
	}

	public Animate animate() {
		return this.animate;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		int index = 0;
		if (enableAnimate) {
			for (ComponentListButton<?> listBtn : buttons) {
				GlStateManager.enableBlend();
				GlStateManager.color(1, 1, 1);
				gui.pushKeep("offsety").translate(0,
						(double) (index * interval) + (double) (animate.getSmoothOffsetY(gui.getPartialTicks())));
				gui.glStateInvokeStart();
				GlStateManager.color(1, 1, 1);
				listBtn.drawBeforeCompoents(gui);
				gui.glStateInvokeEnd();
				gui.pop("offsety");
				index += 1;
			}
		}

		if (footerInfo) {
			gui.push("debug-hint").translate(15, offsetY + buttons.size() * interval).scale2D(3.5);
			gui.drawString(getFotterInfo());
		}
		// 在末尾绘制调试信息
		super.drawBeforeCompoents(gui);
	}

	public String getFotterInfo() {
		return "§7..." + buttons.size();
	}

	@Override
	public void onWheelInput(int value) {
		if (this.isHover()) {
			addPage(value);
		}
	}

	public void addPage(int value) {
		offsetY -= value * stepSize;
		checkAndCorrectY();

	}

	public void indexItem(int id) {
		offsetY = -id * interval;
		checkAndCorrectY();
	}

	public void checkAndCorrectY() {
		if (offsetY > 0)
			offsetY = 0;
		else if (offsetY < -getPageItemMax() * interval) {
			offsetY = -getPageItemMax() * interval;
		}
		init(fastGuiHandler);
	}

	public int getPageItemMax() {
		if (buttons.size() > pageSize)
			return buttons.size() - pageSize;
		return 0;
	}

	public float getProgress() {
		return (float) offsetY / -((float) getPageItemMax() * (float) interval);
	}

	public void setProgress(float progress) {
		this.offsetY = (int) (-((float) getPageItemMax() * (float) interval) * progress);
	}

	@Override
	public void onTick() {
		animate.onTick();
	}

}
