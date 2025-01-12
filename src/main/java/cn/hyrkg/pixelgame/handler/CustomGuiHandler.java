package cn.hyrkg.pixelgame.handler;

import java.lang.reflect.Field;
import java.util.UUID;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.ModForgeGuiHandler;
import cn.hyrkg.pixelgame.module.discount_market.GuiDiscountMarket;
import cn.hyrkg.pixelgame.module.forge.GuiUpgrade;
import cn.hyrkg.pixelgame.module.forge.blueprint.GuiBlueprint;
import cn.hyrkg.pixelgame.module.forge.ring.GuiRingBlueprint;
import cn.hyrkg.pixelgame.module.forge.ring.GuiRingDisassemble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomGuiHandler {
	private static Field field_upperChest = null, field_lowerChest = null;

	public static void onInit() {
		MinecraftForge.EVENT_BUS.register(new CustomGuiHandler());
	}

	public void checkFileReflection() {
		if (field_upperChest != null && field_lowerChest != null) {
			return;
		}
		for (Field fs : GuiChest.class.getDeclaredFields()) {
			if (fs.getType().equals(IInventory.class)) {
				if (field_upperChest == null) {
					field_upperChest = fs;
					field_upperChest.setAccessible(true);
				} else {
					field_lowerChest = fs;
					field_lowerChest.setAccessible(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event) {
//		if (true) {
//			return;
//		}
		if (event.getGui() instanceof GuiChest) {
			checkFileReflection();
			try {
				IInventory upp = (IInventory) field_upperChest.get(event.getGui());
				IInventory lpp = (IInventory) field_lowerChest.get(event.getGui());

				String containerName = lpp.getName();

				if (containerName.startsWith("pixel_forge#upgrade")) {
					String uuid = containerName.split("\\~")[1];
					GuiUpgrade gui = new GuiUpgrade(UUID.fromString(uuid), Minecraft.getMinecraft().player, upp, lpp);
					Minecraft.getMinecraft().displayGuiScreen(gui);
					ModForgeGuiHandler.bindForgeGui(gui);
					event.setCanceled(true);
				} else if (containerName.startsWith("pixel_forge#bp")) {
					String uuid = containerName.split("\\~")[1];
					GuiBlueprint gui = new GuiBlueprint(UUID.fromString(uuid), Minecraft.getMinecraft().player, upp,
							lpp);
					Minecraft.getMinecraft().displayGuiScreen(gui);
					ModForgeGuiHandler.bindForgeGui(gui);
					event.setCanceled(true);
				} else if (containerName.startsWith("pixel_forge#rab")) {
					String uuid = containerName.split("\\~")[1];
					GuiRingBlueprint gui = new GuiRingBlueprint(UUID.fromString(uuid), Minecraft.getMinecraft().player,
							upp, lpp);
					Minecraft.getMinecraft().displayGuiScreen(gui);
					ModForgeGuiHandler.bindForgeGui(gui);
					event.setCanceled(true);
				} else if (containerName.startsWith("pixel_forge#rda")) {
					String uuid = containerName.split("\\~")[1];
					GuiRingDisassemble gui = new GuiRingDisassemble(UUID.fromString(uuid),
							Minecraft.getMinecraft().player, upp, lpp);
					Minecraft.getMinecraft().displayGuiScreen(gui);
					ModForgeGuiHandler.bindForgeGui(gui);
					event.setCanceled(true);
				} else if (containerName.startsWith("pdm#ui")) {
					String uuid = containerName.split("\\~")[1];
					GuiDiscountMarket gui = new GuiDiscountMarket(UUID.fromString(uuid),
							Minecraft.getMinecraft().player, upp, lpp);
					Minecraft.getMinecraft().displayGuiScreen(gui);
					ModForgeGuiHandler.bindForgeGui(gui);
					event.setCanceled(true);
				}
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
	}
}
