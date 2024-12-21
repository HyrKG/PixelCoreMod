package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

import java.util.List;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class ComponentItemButton extends ComponentButton {

	private ItemStack stack;

	public ComponentItemButton(int id, ItemStack stack, int x, int y, int width, int height) {
		super(id, x, y, width, height);
		this.stack = stack;
	}

	@Override
	public void drawAfterAll(FastGuiHandler gui) {
		super.drawAfterAll(gui);
		if (stack != null && this.isHover()) {
			FontRenderer font = stack.getItem().getFontRenderer(stack);
			net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
			List<String> toolTip = gui.gui.getItemToolTip(stack);

			toolTip.add("");

			if (texHover == null) {
				toolTip.add("§9<点击> 选中该材料");
			} else {
				toolTip.add("§6(该材料已被选中)");
				toolTip.add("§9<点击> 取消选中");

			}

			gui.gui.drawHoveringText(toolTip, gui.getLastMouseX(), gui.getLastMouseY());
			net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
		}
	}

}
