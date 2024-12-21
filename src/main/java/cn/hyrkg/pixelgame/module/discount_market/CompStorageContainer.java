package cn.hyrkg.pixelgame.module.discount_market;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentScissorPanel;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list.ComponentDragableScrollBar;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.CustomSlot;
import cn.hyrkg.pixelgame.dto.discount_market.StorageGroup;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;

/**
 * 库存容器，用于展示库存内容
 */
public class CompStorageContainer extends ComponentScissorPanel {

	public static int offsetY = 0;
	private int maxOffsetY = 0;
	private final GuiDiscountMarket gui;

	public CompStorageContainer(GuiDiscountMarket gui, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.gui = gui;

	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		ComponentDragableScrollBar scrollbar = this.getComponent(ComponentDragableScrollBar.class);
		this.clearComponents();
		if (scrollbar != null) {
			this.addComponent(scrollbar);
		} else {
			this.addComponent(new ComponentDragableScrollBar(258, 2, 5, 110).setOnPageUpdate(this::onScrollUpdate));
		}

		int currentOffset = 0;
		int lastHeight = 0;
		for (StorageGroup group : gui.getStorageGroups()) {
			CompStorageGroup compGroup = new CompStorageGroup(gui, group, 0, currentOffset - offsetY);
			currentOffset += compGroup.getHeight();
			lastHeight = compGroup.getHeight();
			this.addComponent(compGroup);
		}
		maxOffsetY = Math.max(0, currentOffset - 100);
	}

	@Override
	public void drawPanel(FastGuiHandler gui) {
		gui.bind(GuiDiscountMarket.TEX_UI);
		gui.push().translate(257, 2 + ((double) offsetY / (double) maxOffsetY) * 101d);
		gui.drawTex(310, 1, 7, 9);
	}

	@Override
	public void onTick() {
		super.onTick();
	}

	@Override
	public void onWheelInput(int value) {
		super.onWheelInput(value);

		offsetY += value * 20;
		checkOffset();
		init(fastGuiHandler);
	}

	public void checkOffset() {
		if (offsetY < 0) {
			offsetY = 0;
		}
		if (offsetY > maxOffsetY) {
			offsetY = maxOffsetY;
		}
	}

	public void onScrollUpdate(float progress) {
		this.offsetY = (int) (maxOffsetY * progress);
		init(fastGuiHandler);
	}

}
