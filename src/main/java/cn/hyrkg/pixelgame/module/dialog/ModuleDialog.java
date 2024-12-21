package cn.hyrkg.pixelgame.module.dialog;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.ModForgeGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModuleDialog {
	public static Tex TEX_DIALOG = Tex.of("dialog/dialog", 1920, 1080);

	public static void onPreInit() {
		// 注册ForgeUI
		ModForgeGuiHandler.registerStandardGui(GuiDialog.class, "dialog");
		MinecraftForge.EVENT_BUS.register(new ModuleDialog());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onRenderName(RenderLivingEvent.Specials.Pre event) {
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().currentScreen == null) {
			return;
		}
		if (Minecraft.getMinecraft().currentScreen instanceof GuiDialog) {
			event.setCanceled(true);
		}
	}
}
