package cn.hyrkg.pixelgame.module.forge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButtonTextable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentItemButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.BaseFastContainerGui;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.CustomSlot;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.DrawHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.IForgeGui;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SharedProperty;
import cn.hyrkg.pixelgame.core.lib.LibSounds;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class GuiUpgrade extends BaseFastContainerGui implements IForgeGui {

	private final UUID uuid;
	protected SharedProperty sharedProperty;

	private static Tex TEX_UPGRAGE = Tex.of("forge/upgrade");

	private IInventory upperChestInventory;
	private IInventory lowerChestInventory;

	// 渐入动画
	private TickCounter fadeInCounter = new TickCounter(15, false);

	// 进阶动画：渐进
	private TickCounter upgradeFadeInCounter = new TickCounter(10, false);
	// 进阶动画：结果
	private TickCounter upgradeResultCounter = new TickCounter(30, false);

	private ComponentButton upgradeButton = null;

	private List<ItemStack> validMaterials = new ArrayList<ItemStack>();
	private List<ItemStack> validProtections = new ArrayList<ItemStack>();

	private HashMap<Integer, Integer> slotIdMap = new HashMap<Integer, Integer>();

	private ItemStack selectedMaterial = null;
	private ItemStack selectedProtection = null;

	private boolean lastUpgradeSuccess = false;
	private String lastUpgradeMessage = "";

	private int tickLoop = 0;

	// 从物品中读取title和info来显示
	private String lastTitle = null;
	private List<String> lastInfo = null;

	public GuiUpgrade(UUID uuid, EntityPlayer player, IInventory upperInv, IInventory lowerInv) {
		super(new ContainerChest(upperInv, lowerInv, player));
		this.uuid = uuid;
		sharedProperty = new SharedProperty();
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;

		// 刚开始直接把动画填满，只有在触发动画的时候才播放动画
		upgradeFadeInCounter.tick = upgradeFadeInCounter.tickMax;
		upgradeResultCounter.tick = upgradeResultCounter.tickMax;
		handler.skipDrawAfterAll = true;
	}

	public ItemStack getStack(int slot) {
		if (upperChestInventory == null) {
			return null;
		}
		return upperChestInventory.getStackInSlot(slot);
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.clearComponents();
		gui.getTransformSolution().wh(323, 199).fitScaledScreen(2.6f).translateToCenter(width, height);

		String text = "§e§l进阶精石介绍：\n§f- §7在右侧选择你的进阶精石\n§f- §7点击进阶按钮即可进阶\n§f- §7进阶成功后你的装备属性将大幅度提升\n§f- §c若进阶失败可能会降级\n\n§b§l守护石介绍：\n§f- §7进阶时如果你有守护石\n§f- §7可以在右侧选择守护石再进阶\n§f- §7守护石能帮助你在进阶失败时\n§f- §7减少你的掉级掉级层数§a（更少降级）\n§f- §c守护石只有在进阶失败时才会被消耗\n\n§a进阶石、守护石可以通过万年以上灵兽掉落\n也可以通过 §6<斗魂挑战> §a获得！";
		gui.addComponent(new ComponentButtonTextable(0, 154, 8, 11, 11).setText(text));

		gui.addComponent(upgradeButton = new ComponentButton(1, 113, 75, 49, 12).whenClick(this::onClick)
				.texEnable(ReadyTex.of(TEX_UPGRAGE, 82, 325, 49, 12))
				.texHover(ReadyTex.of(TEX_UPGRAGE, 82, 311, 49, 12))
				.texSelected(ReadyTex.of(TEX_UPGRAGE, 142, 325, 49, 12)));
		upgradeButton.setSelected(true);

		JsonObject completeJson = sharedProperty.getCompleteJson();
		if (completeJson.has("upgradeable") && completeJson.get("upgradeable").getAsBoolean()) {
			upgradeButton.setSelected(false);

		}

		//////////////////////////////////////////////////////////////
		// 加载材料、保护石

		int selectedMaterialIndex = -1;
		int selecteProtectionIndex = -1;

		if (completeJson.has("selectedMaterial")) {
			selectedMaterialIndex = completeJson.get("selectedMaterial").getAsInt();
		}

		if (completeJson.has("selectedProtection")) {
			selecteProtectionIndex = completeJson.get("selectedProtection").getAsInt();
		}

		int index = 0;

		selectedMaterial = null;
		List<ItemStack> newMaterials = new ArrayList<ItemStack>();
		if (completeJson.has("materials")) {
			JsonArray array = completeJson.getAsJsonArray("materials");
			for (JsonElement element : array) {
				ItemStack stack = getStack(element.getAsInt());
				if (stack == null) {
					continue;
				}
				ItemStack copy = stack.copy();
				copy.setCount(1);
				newMaterials.add(copy);

				// 放置按钮
				int row = index % 6;
				int cow = index / 6;
				ComponentButton btn;
				handler.addComponent(
						btn = new ComponentItemButton(100 + index, copy, 192 + row * 19, 31 + cow * 19, 16, 15)
								.whenClick(this::onClick));
				slotIdMap.put(100 + index, element.getAsInt());

				if (selectedMaterialIndex == element.getAsInt()) {
					selectedMaterial = copy;
					btn.texHover(ReadyTex.of(TEX_UPGRAGE, 0, 0, 0, 0));
				}

				index += 1;

			}
		}
		validMaterials = newMaterials;

		selectedProtection = null;
		List<ItemStack> newProtections = new ArrayList<ItemStack>();
		if (completeJson.has("protections")) {
			JsonArray array = completeJson.getAsJsonArray("protections");
			for (JsonElement element : array) {
				ItemStack stack = getStack(element.getAsInt());
				if (stack == null) {
					continue;
				}
				ItemStack copy = stack.copy();
				copy.setCount(1);
				newProtections.add(copy);

				// 放置按钮
				int row = index % 6;
				int cow = index / 6;
				ComponentButton btn;

				handler.addComponent(
						btn = new ComponentItemButton(100 + index, copy, 192 + row * 19, 31 + cow * 19, 16, 15)
								.whenClick(this::onClick));
				slotIdMap.put(100 + index, element.getAsInt());

				if (selecteProtectionIndex == element.getAsInt()) {
					selectedProtection = copy;
					btn.texHover(ReadyTex.of(TEX_UPGRAGE, 0, 0, 0, 0));
				}
				index += 1;
			}
		}
		validProtections = newProtections;
		/////////////////////////////////////////////////////////////

		setupCustomSlot();
	}

	public void onClick(ComponentButton btn) {
		if (btn.id == 1 && upgradeFadeInCounter.isDone() && upgradeResultCounter.percentage() > 0.1) {
			upgradeButton.setSelected(true);
			this.msg().add("upgrade", 1).sent();
		} else if (slotIdMap.containsKey(btn.id)) {
			Integer slot = slotIdMap.get(btn.id);
			this.msg().add("select", slot).sent();
			LibSounds.play("upgrade_use");
		}
	}

	@Override
	public void onMessage(JsonObject jsonObject) {
		if (jsonObject.has("upgrade_result")) {
			JsonObject result = jsonObject.getAsJsonObject("upgrade_result");
			lastUpgradeMessage = result.get("message").getAsString();
			lastUpgradeSuccess = result.get("success").getAsBoolean();
			if (lastUpgradeSuccess) {
				LibSounds.play("upgrade_success");
			} else {
				LibSounds.play("upgrade_fail");
			}
			upgradeFadeInCounter.tick = 0;
			upgradeFadeInCounter.lastTick = 0;
		} else if (jsonObject.has("equip")) {
			LibSounds.play("upgrade_use");
		} else if (jsonObject.has("unequip")) {
			LibSounds.play("upgrade_use");
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {
		gui.bind(TEX_UPGRAGE);

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		GL11.glPushMatrix();
		float disX = DrawHelper.getDistanceXFromCenter(gui);
		float disY = DrawHelper.getDistanceYFromCenter(gui);
		DrawHelper.drawCenterRolling(disX, disY, 1.5f);

		double bgOffsetX = 0;
		int bgOffsetY = 0;

		if (!upgradeFadeInCounter.isDone()) {
			float p = upgradeFadeInCounter.percentage(gui.getPartialTicks());
			bgOffsetX = Math.sin(p * 30);
		}

		// 绘制锻造背景
		gui.push().translate(14 + bgOffsetX, 27 + bgOffsetY, 30);
		gui.drawTex(15, 241, 97, 60);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		DrawHelper.drawCenterRolling(disX, disY, 0.5f);

		// 绘制界面
		gui.drawTex(19, 20, 323, 199);

		// 绘制槽位
		gui.push().translate(39.5, 34, 0);
		gui.drawTex(17, 318, 48, 48);

		// 绘制开机效果
		if (!fadeInCounter.isDone()) {
			gui.push().rgba(0.0f, 0.0f, 0.0f, 1f - fadeInCounter.percentage(gui.getPartialTicks())).translate(16, 29);
			gui.drawTex(120, 244, 92, 57);
		} else if (!upgradeFadeInCounter.isDone()) {
			gui.push().rgba(0.99f, 0.61f, 0.25f, upgradeFadeInCounter.percentage(gui.getPartialTicks())).translate(16,
					29);
			gui.drawTex(120, 244, 92, 57);
		} else if (!upgradeResultCounter.isDone()) {
			float p = upgradeResultCounter.percentage(gui.getPartialTicks());
			gui.push().rgba(0.99f, 0.61f, 0.25f, 1 - p).translate(16, 29);
			gui.drawTex(120, 244, 92, 57);
			gui.push().translate(62, 74 - p * 4).scale2D(0.7);
			gui.drawCenteredString(lastUpgradeMessage);

		}

		// 绘制title
		if (lastTitle != null) {
			gui.push().translate(138, 32).scale2D(0.65);
			gui.drawCenteredString(lastTitle);
		}

		// 绘制lore
		if (lastInfo != null) {
			gui.pushKeep("lores").translate(138, 41).scale2D(0.65);
			gui.glStateInvokeStart();
			for (int index = 0; index < lastInfo.size(); index++) {
				String lore = lastInfo.get(index);
				gui.push().translate(0, index * 10);
				gui.drawCenteredString(lore);
			}
			gui.pop();
		}
		GL11.glPopMatrix();
		////////////////////////////////////////////////
		//
		// 绘制材料
		GlStateManager.color(1, 1, 1);
		int itemIndex = 0;
		for (ItemStack stack : validMaterials) {
			int row = itemIndex % 6;
			int cow = itemIndex / 6;
			GlStateManager.enableDepth();
			gui.bind(TEX_UPGRAGE);
			gui.pushKeep("g").translate(192 + row * 19, 31 + cow * 19);
			gui.glStateInvokeStart();
			gui.drawTex(371, 23, 16, 15);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glPushMatrix();
			GL11.glTranslated(3, 2.5, 0);
			double sc = 0.65;
			GL11.glScaled(sc, sc, 1);
			gui.gui.mc.getRenderItem().renderItemIntoGUI(stack, 0, 0);
			GL11.glPopMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableDepth();

			if (stack == selectedMaterial) {
				gui.bind(TEX_UPGRAGE);
				gui.push().translate(8, 5);
				gui.drawTex(350, 42, 10, 11);
			}
			gui.pop();
			itemIndex += 1;
		}
		for (ItemStack stack : validProtections) {
			int row = itemIndex % 6;
			int cow = itemIndex / 6;
			GlStateManager.enableDepth();
			gui.bind(TEX_UPGRAGE);
			gui.pushKeep("g").translate(192 + row * 19, 30 + cow * 19);
			gui.glStateInvokeStart();
			gui.drawTex(349, 22, 16, 17);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glPushMatrix();
			GL11.glTranslated(3, 3, 0);
			double sc = 0.65;
			GL11.glScaled(sc, sc, 1);
			gui.gui.mc.getRenderItem().renderItemIntoGUI(stack, 0, 0);
			GL11.glPopMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableDepth();

			if (stack == selectedProtection) {
				gui.bind(TEX_UPGRAGE);
				gui.push().translate(8, 5);
				gui.drawTex(350, 42, 10, 11);
			}
			gui.pop();
			itemIndex += 1;
		}
		// 同理，绘制保护石
		//////////////////////////////////////////////////////
	}

	@Override
	public void setupCustomSlot() {
		slotMap.clear();
		TransformSolution trans = handler.getTransformSolution();
		double scale = trans.scaledX;
		int size = 12;

		int containerSize = this.lowerChestInventory.getSizeInventory();

		for (Slot slot : this.inventorySlots.inventorySlots) {
			int id = slot.slotNumber;
			CustomSlot customSlot = null;
			if (id >= containerSize) {
				// 玩家背包
				int nid = id - containerSize;
				int row = nid % 9;
				int cow = nid / 9;

				int offsetX = 0;
				if (row == 1) {
					// 此处为了美观，通过代码调整位置。
					offsetX = 1;
				}

				if (nid <= 26) {
					customSlot = new CustomSlot(slot, slot.slotNumber, offsetX + 18 + (int) (row * 16.5),
							102 + (int) (cow * 17.5), size, scale);
				} else {
					customSlot = new CustomSlot(slot, slot.slotNumber, offsetX + 18 + (int) (row * 16.5),
							108 + (int) (cow * 17), size, scale);
				}
			} else {
				// 容器
				int rx = id % 9;
				int cx = id / 9;

				if (slot.slotNumber == 0) {
					customSlot = new CustomSlot(slot, slot.slotNumber, 56, 51, 15, scale);
				} else {
					customSlot = new CustomSlot(slot, slot.slotNumber, -9999999, -9999999, size, scale);
				}
			}
			if (customSlot != null) {
				slotMap.put(slot, customSlot);
			}

		}
	}

	@Override
	public void tick() {
		super.tick();
		fadeInCounter.tick();

		if (!upgradeFadeInCounter.isDone()) {
			upgradeFadeInCounter.tick();
			if (upgradeFadeInCounter.isDone()) {
				upgradeResultCounter.tick = 0;
				upgradeResultCounter.lastTick = 0;
				initGui();
			}
		}
		upgradeResultCounter.tick();

		// 从物品上加载信息
		if (tickLoop % 3 == 0 && upgradeFadeInCounter.isDone()) {
			// 每0.5秒更新一次物品信息
			updateTitleAndInfo();
		}

		// 更新tickloop
		if (tickLoop == 20) {
			tickLoop = 0;
		} else {
			tickLoop += 1;
		}
	}

	private void updateTitleAndInfo() {
		Slot slot = this.inventorySlots.getSlot(1);
		if (slot == null || slot.getStack() == null || !slot.getStack().hasTagCompound()) {
			return;
		}
		ItemStack stack = slot.getStack();
		NBTTagCompound displayTag = stack.getTagCompound().getCompoundTag("display");
		if (displayTag != null) {
			if (displayTag.hasKey("Name")) {
				lastTitle = displayTag.getString("Name");
			} else {
				lastTitle = null;
			}
			if (displayTag.hasKey("Lore")) {
				NBTTagList tagList = displayTag.getTagList("Lore", 8);
				List<String> loreList = new ArrayList<String>();
				for (NBTBase base : tagList) {
					loreList.add(((NBTTagString) base).getString());
				}
				lastInfo = loreList;
			} else {
				lastInfo = null;
			}
		}
	}

	@Override
	public void synProperty(JsonObject obj) {
		sharedProperty.synProperty(obj);
		if (upgradeFadeInCounter.isDone()) {
			initGui();
		}
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}
}
