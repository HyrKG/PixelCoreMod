package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author HyrKG
 *
 */
public abstract class BaseComponent implements IComponent {

	protected FastGuiHandler fastGuiHandler = null;

	protected boolean isDragging = false;

	protected boolean debugMode = false;
	protected boolean infoMode = false;
	protected boolean buildMode = false;

	protected boolean skipDraw = false;
	protected boolean skipDrawComponents = false;

	protected IComponent superComponent = null;
	protected TransformSolution transformSolution = TransformSolution.of(0, 0, 0, 0);
	protected List<IComponent> components = new CopyOnWriteArrayList<>();
	protected IShortcutComponent shortComponent = null;

	protected Supplier<Boolean> hoverPrecondition = null;
	protected Supplier<Boolean> drawPrecondition = null;

	public void init(FastGuiHandler fastGuiHandler) {
	}

	@Override
	public void setGuiHandler(FastGuiHandler guiHandler) {
		this.fastGuiHandler = guiHandler;
		init(guiHandler);
	}

	@Override
	public FastGuiHandler getGuiHandler() {
		if (superComponent != null)
			return superComponent.getGuiHandler();
		return fastGuiHandler;
	}

	public boolean isSkipDraw() {
		return skipDraw;
	}

	public boolean isDragging() {
		return isDragging;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public boolean isBuildMode() {
		return buildMode;
	}

	public void setSkipDraw(boolean skipDraw) {
		this.skipDraw = skipDraw;
	}

	public BaseComponent setDrawPrecondition(Supplier<Boolean> drawPrecondition) {
		this.drawPrecondition = drawPrecondition;
		return this;
	}

	public BaseComponent debug(boolean flag, boolean infoMode) {
		debugMode = flag;
		this.infoMode = infoMode;
		return this;
	}

	public BaseComponent build(boolean flag) {
		buildMode = flag;
		return this;
	}

	@Override
	public IComponent getSuperComponent() {
//		ItemMap

		return superComponent;
	}

	@Override
	public void setSuperComponent(IComponent superComponent) {
		this.superComponent = superComponent;
	}

	@Override
	public List<IComponent> getComponents() {
		return components;
	}

	@Override
	public void addComponent(IComponent component) {
		component.setSuperComponent(this);

		component.setGuiHandler(fastGuiHandler);
		component.setHoverPrecondition(this::isHover);
		components.add(component);

		if (component instanceof IShortcutComponent) {
			((IShortcutComponent) component).setWhenLeaveConsumer(this::onShortcutLeave);
			shortComponent = (IShortcutComponent) component;
		}

		component.getTransformSolution().translateAdd(0, 0, 1);
	}

	public void removeComponent(IComponent component) {
		while (components.contains(component))
			components.remove(component);
	}

	public void removeComponent(Class<? extends IComponent> clazz) {

		for (IComponent component : components) {
			if (component.getClass().equals(clazz)) {
				components.remove(component);
			}
		}
	}

	public boolean hasComponent(Class<? extends IComponent> clazz) {
		for (IComponent component : components) {
			if (component.getClass().equals(clazz))
				return true;
		}
		return false;
	}

	public <T> T getComponent(Class<? extends T> clazz) {
		for (IComponent component : components) {
			if (component.getClass().equals(clazz))
				return (T) component;
		}
		return null;
	}

	@Override
	public double[] getTransformFinalOffset() {
		if (getSuperComponent() == null || !(getSuperComponent() instanceof IComponent))
			return new double[] { getTransformSolution().getX() - 1, getTransformSolution().getY() - 1,
					getTransformSolution().scaledX, getTransformSolution().scaledY };
		else {
			double[] sources = ((IComponent) getSuperComponent()).getTransformFinalOffset();
			sources[0] += getTransformSolution().getX() * sources[2];
			sources[1] += getTransformSolution().getY() * sources[3];
			sources[2] *= getTransformSolution().scaledX;
			sources[3] *= getTransformSolution().scaledY;
			return sources;
		}
	}

	public double[] getRelatedOffsetFromMouse(int offsetX, int offsetY) {
		double[] result = new double[2];

		double[] offset = this.getTransformFinalOffset();
		result[0] = (fastGuiHandler.getLastMouseX() + offsetX - offset[0]) * 1 / (offset[2] * 1);
		result[1] = (fastGuiHandler.getLastMouseY() + offsetY - offset[1]) * 1 / (offset[3] * 1);
		return result;
	}

	protected FastGuiHandler lastGui = null;

	@Override
	public void draw(FastGuiHandler gui) {
		if (drawPrecondition != null && !drawPrecondition.get())
			return;

		if (isBuildMode()) {
			lastGui = gui;
			if (isDragging) {
				offsetToMouse(gui.getLastMouseX(), gui.getLastMouseY());
			}
		}

		GL11.glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);

		TransformSolution trans = getTransformSolution();

		GL11.glTranslated(trans.getX(), trans.getY(), trans.getZ());
		GL11.glScaled(trans.scaledX, trans.scaledY, trans.scaledZ);

		drawBeforeCompoents(gui);

		drawDebug(trans, gui);

		if (!skipDrawComponents) {
			components.forEach(j -> {
				if (!j.isSkipDraw())
					j.draw(gui);
			});
		}
		GL11.glPopMatrix();
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		components.forEach(j -> j.drawAfterAll(gui));
	}

	public void drawDebug(TransformSolution trans, FastGuiHandler gui) {
		if (debugMode) {

			gui.drawRect(trans.width, trans.height, 2063612928);

			if (infoMode) {
				List<String> displays = new ArrayList<>();
				displays.add("isHover:" + isHover(gui.getLastMouseX(), gui.getLastMouseY()) + "\n" + "isBuild:"
						+ isBuildMode() + "\n" + "isDrag:" + isDragging);

				displays.add("transform:" + trans);

				double[] transformOffset = getTransformFinalOffset();
				double translatedX = new BigDecimal(transformOffset[0]).setScale(1, BigDecimal.ROUND_DOWN)
						.doubleValue();
				double translatedY = new BigDecimal(transformOffset[1]).setScale(1, BigDecimal.ROUND_DOWN)
						.doubleValue();

				double scaledWidth = trans.width * transformOffset[2];
				double scaledHeight = trans.height * transformOffset[3];

				displays.add("tpX:" + translatedX + ">" + (translatedX + scaledWidth) + "\ntpY:" + translatedY + ">"
						+ (translatedY + scaledHeight));

				String combine = "";
				for (String s : displays) {
					combine += "§6" + s + "\n\n";
				}

				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, 200);
				GL11.glScaled(3, 3, 1);
				Minecraft.getMinecraft().fontRenderer.drawSplitString(combine, 0, 0, (int) (trans.width), -1);
				GL11.glPopMatrix();
			}
		}
	}

	public abstract void drawBeforeCompoents(FastGuiHandler gui);

	@Override
	public TransformSolution getTransformSolution() {
		return transformSolution;
	}

	@Override
	public boolean isHover(int mouseX, int mouseY) {
		if (transformSolution == null || transformSolution == TransformSolution.EMPTY)
			return false;

		TransformSolution trans = getTransformSolution();

		double[] transformOffset = getTransformFinalOffset();

		double translatedX = transformOffset[0];
		double translatedY = transformOffset[1];
		double scaledWidth = trans.width * transformOffset[2];
		double scaledHeight = trans.height * transformOffset[3];

		if (!(mouseX >= translatedX && mouseX <= (translatedX + scaledWidth))) {
			return false;
		}
		if (!(mouseY >= translatedY && mouseY <= (translatedY + scaledHeight)))
			return false;
		return true;
	}

	public boolean isHover() {
		if (hoverPrecondition != null && !hoverPrecondition.get()) {
			return false;
		}
		return isHover(getGuiHandler().getLastMouseX(), getGuiHandler().getLastMouseY());
	}

	public IComponent setHoverPrecondition(Supplier<Boolean> hoverPrecondition) {
		this.hoverPrecondition = hoverPrecondition;
		return this;
	}

	public void offsetToMouse(int mouseX, int mouseY) {

		double[] transformOffset = null;
		if (getSuperComponent() != null && getSuperComponent() instanceof IComponent) {
			transformOffset = ((IComponent) getSuperComponent()).getTransformFinalOffset();
			this.getTransformSolution().translate(
					-((transformOffset[0] - mouseX) * 1 / transformOffset[2]) * 1 / transformSolution.scaledX,
					-((transformOffset[1] - mouseY) * 1 / transformOffset[3]) * 1 / transformSolution.scaledY, 0);
		} else {
			this.getTransformSolution().translate(mouseX * 1 / getTransformSolution().scaledX,
					mouseY * 1 / getTransformSolution().scaledY, 0);
		}

	}

	@Override
	public boolean isActive(FastGuiHandler gui) {
		return true;
	}

	@Override
	public void onMouseInput() {
		if (lastGui != null && isBuildMode()) {
			int k = Mouse.getEventButton();
			if (k == 0) {
				if (Mouse.getEventButtonState()) {
					if (isHover(lastGui.getLastMouseX(), lastGui.getLastMouseY())) {
						isDragging = true;
					}
				} else {
					isDragging = false;
				}

			}
		}
		components.forEach(j -> j.onMouseInput());
	}

	@Override
	public void onWheelInput(int value) {
		if (debugMode && isDragging) {
			getTransformSolution().scale2D(new BigDecimal(getTransformSolution().scaledX + 0.05 * -value)
					.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		components.forEach(j -> j.onWheelInput(value));

	}

	@Override
	public void onMouseClick(int mouseButton) {
		components.forEach(j -> j.onMouseClick(mouseButton));
	}

	@Override
	public void onTick() {
		components.forEach(j -> j.onTick());
	}

	@Override
	public void onKeyInput(char typedChar, int keyCode) {
		components.forEach(j -> j.onKeyInput(typedChar, keyCode));
	}

	@Override
	public void onClose() {
		components.forEach(j -> j.onClose());
	}

	public void onShortcutLeave(BaseComponent component) {
		while (components.contains(component)) {
			component.onRemove();
			components.remove(component);
			shortComponent = null;
		}
	}

	public boolean hasShortcutComponent() {
		return shortComponent != null;
	}

	public boolean hasNotShortcutComponent() {
		return shortComponent == null;
	}

	public void clearComponents() {
		getComponents().clear();
		shortComponent = null;
	}

	public void tryDismiss(IComponent component) {
		getComponents().remove(component);
	}

	public void dismiss() {
		if (this.superComponent != null) {
			this.superComponent.tryDismiss(this);
		}
	}
}
