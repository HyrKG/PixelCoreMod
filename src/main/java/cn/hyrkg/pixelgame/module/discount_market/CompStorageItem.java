package cn.hyrkg.pixelgame.module.discount_market;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.BaseComponent;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButtonTextable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentPanel;
import cn.hyrkg.pixelgame.dto.discount_market.StorageItem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * 库存物品
 */
public class CompStorageItem extends BaseComponent {

	private final StorageItem item;
	private final Slot slot;
	private final int id;

	private Consumer<StorageItem> purchaseConsumer = null;
	private ComponentPanel compItemArea = null;

	public CompStorageItem(StorageItem item, Slot slot, int id, int x, int y) {
		this.item = item;
		this.slot = slot;
		this.id = id;
		this.transformSolution = TransformSolution.of(x, y, 79, 24);
	}

	@Override
	public void init(FastGuiHandler fastGuiHandler) {
		clearComponents();
		if (!item.soldOut) {
			addComponent(new ComponentButtonTextable(0, 64, 12, 9, 7).setText("点击购买此物品").whenClick(this::onClick));
		}
		this.addComponent(compItemArea = new ComponentPanel(0, 0, 23, 24));
	}

	public void onClick(ComponentButton btn) {
		if (this.purchaseConsumer != null) {
			this.purchaseConsumer.accept(item);
		}
	}

	public CompStorageItem onPurchase(Consumer<StorageItem> purchaseConsumer) {
		this.purchaseConsumer = purchaseConsumer;
		return this;
	}

	@Override
	public void drawBeforeCompoents(FastGuiHandler gui) {
		gui.bind(GuiDiscountMarket.TEX_UI);
		gui.drawTex(310, 76, 79, 24);

		if (item.soldOut) {
			gui.push().translate(64, 12).rgba(0.5f, 0.5f, 0.5f, id);
		} else {
			gui.push().translate(64, 12);
		}
		gui.drawTex(310, 113, 9, 7);

		// 绘制金币符号
		String amount = "";
		gui.push().translate(25, 9.5).scale2D(0.6);
		if (item.points != null) {
			amount = "§b" + item.points;
			gui.drawTex(310, 104, 9, 7);
		} else if (item.coins != null) {
			amount = "§e" + item.coins;
			gui.drawTex(320, 104, 9, 7);
		}
		gui.push().translate(31, 8.4).scale2D(0.7);
		gui.drawString(amount);

		GlStateManager.color(1, 1, 1);
		gui.bind(GuiDiscountMarket.TEX_UI);
		if (item.bannerTitle != null && !item.bannerTitle.isEmpty()) {
			gui.push().translate(0, 0).scale2D(0.3);
			gui.drawTex(98, 195, 44, 44);

			gui.push().translate(3, 3).rotate(0, 0, -45).scale2D(0.55);
			gui.drawCenteredString("§e" + item.bannerTitle);
		}

		gui.push().translate(25, 14.9).scale2D(0.56);
		gui.drawString("§6" + item.subtitle);

		ItemStack stack = slot.getStack();
		if (stack != null) {
			GL11.glPushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableDepth();
			GL11.glTranslated(6, 5, 15);
			double sc = 0.8;
			GL11.glScaled(sc, sc, 1);
			gui.gui.mc.getRenderItem().renderItemIntoGUI(stack, 0, 0);
			RenderHelper.disableStandardItemLighting();
			GL11.glPopMatrix();

			gui.push().translate(25, 2.3).scale2D(0.7);
			gui.drawString(stack.getDisplayName());
		}

		if (item.soldOut) {
			gui.bind(GuiDiscountMarket.TEX_UI);
			GlStateManager.color(1, 1, 1);
			gui.push().translate(54, 5).scale2D(0.3);
			gui.drawTex(1, 185, 87, 62);
		}

	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		super.drawAfterAll(gui);
		if (compItemArea != null && slot.getHasStack() && compItemArea.isHover()) {
			ItemStack hoverStack = slot.getStack();
			FontRenderer font = hoverStack.getItem().getFontRenderer(hoverStack);
			net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(hoverStack);
			gui.gui.drawHoveringText(gui.gui.getItemToolTip(hoverStack), gui.getLastMouseX(), gui.getLastMouseY());
			net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
		}
	}

}
