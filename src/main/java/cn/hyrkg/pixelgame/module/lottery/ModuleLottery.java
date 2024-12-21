package cn.hyrkg.pixelgame.module.lottery;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.ModForgeGuiHandler;
import cn.hyrkg.pixelgame.module.lottery.csgo.GuiLotteryCsgo;
import cn.hyrkg.pixelgame.module.lottery.flip_card.GuiLotteryFlipCard;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModuleLottery {

	public static void onPreInit() {
		ModForgeGuiHandler.registerStandardGui(GuiLotteryCsgo.class, "lottery_result");
		ModForgeGuiHandler.registerStandardGui(GuiLotteryFlipCard.class, "lottery_flip_card");

		MinecraftForge.EVENT_BUS.register(new ModuleLottery());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRender(RenderGameOverlayEvent.Pre event) {
		if (Minecraft.getMinecraft().gameSettings.hideGUI) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void hideHotbar(RenderGameOverlayEvent.Pre event) {

		if (event.getType() != ElementType.ALL) {
			return;
		}

		if (Minecraft.getMinecraft().currentScreen != null
				&& Minecraft.getMinecraft().currentScreen instanceof GuiLotteryFlipCard) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void hideHotbarAfter(RenderGameOverlayEvent.Pre event) {
		if (!event.isCancelable()) {
			return;
		}
		if (event.getType() != ElementType.ALL) {
			return;
		}
		if (Minecraft.getMinecraft().currentScreen != null
				&& Minecraft.getMinecraft().currentScreen instanceof GuiLotteryFlipCard) {
			event.setCanceled(false);
		}
	}
}
