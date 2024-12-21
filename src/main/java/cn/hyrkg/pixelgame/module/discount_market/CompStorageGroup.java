package cn.hyrkg.pixelgame.module.discount_market;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.pixelgame.dto.discount_market.StorageGroup;
import cn.hyrkg.pixelgame.dto.discount_market.StorageItem;
import cn.hyrkg.pixelgame.util.TimeUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;

public class CompStorageGroup extends BaseComponent {

	private StorageGroup group;
	private final GuiDiscountMarket gui;
	private int height = 0;

	private String timeLeft = "";

	public CompStorageGroup(GuiDiscountMarket gui, StorageGroup group, int x, int y) {
		this.gui = gui;
		this.group = group;
		this.transformSolution = TransformSolution.of(x, y, 999, 999);

		int line = (group.items.size() / 3) + (group.items.size() % 3 == 0 ? 0 : 1);
		height = (int) (14 + line * 27);
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		clearComponents();

		// 增加库存物品
		int validSize = 0;
		for (StorageItem item : group.items) {
			if (gui.inventorySlots.inventorySlots.size() <= item.slot) {
				continue;
			}
			Slot slot = gui.inventorySlots.inventorySlots.get(item.slot);
			// 跳过空物品
			if (!slot.getHasStack()) {
				continue;
			}
			int id = validSize;
			int rx = id % 3, cx = id / 3;
			addComponent(new CompStorageItem(item, slot, id, 5 + rx * 85, 16 + cx * 27).onPurchase(gui::onPurchase));
			validSize += 1;
		}
		updateTime();
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		gui.bind(GuiDiscountMarket.TEX_UI);
		gui.push().translate(4, 1);
		gui.drawTex(152, 186, 252, 12);

		gui.push().translate(9, 4).scale2D(0.81);
		gui.drawString(group.name + timeLeft);
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void onTick() {
		super.onTick();
		updateTime();
	}

	public void updateTime() {
		if (group.cacheEndTime != -1) {
			if (System.currentTimeMillis() >= group.cacheEndTime) {
				timeLeft = "§c(已过期，请更新黑市状态)";
			} else {
				timeLeft = "§e(将于"
						+ TimeUtil.getTimeLeftAsChinese((group.cacheEndTime - System.currentTimeMillis()) / 1000)
						+ "§e后刷新)";
			}
		}
	}

}
