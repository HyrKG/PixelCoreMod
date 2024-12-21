package cn.hyrkg.pixelgame.module.forge.blueprint;

import java.awt.Color;
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
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.IForgeGui;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SharedProperty;
import cn.hyrkg.pixelgame.client.component.ComponentMessageBox;
import cn.hyrkg.pixelgame.core.lib.LibSounds;
import cn.hyrkg.pixelgame.util.StackUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiBlueprint extends BaseFastContainerGui implements IForgeGui {
	public static final Tex TEX_BLUEPRINT = Tex.of("forge/blueprint");

	/////////////////////////////////////
	// 基础
	private final UUID uuid;
	protected SharedProperty sharedProperty;
	private IInventory upperChestInventory;
	private IInventory lowerChestInventory;

	/////////////////////////////////////
	// 界面功能
	private ComponentButton upgradeButton = null; // 锻造按钮

	////////////////////////////////////
	// 远端同步信息
	private List<String> lastNotify = new ArrayList<String>(); // 通知信息（title）
	private boolean isCraftable = false;// 是否可以制作
	private List<ItemStack> materials = new ArrayList<ItemStack>();

	// 额外材料
	private List<ItemStack> validExtraMaterials = new ArrayList<ItemStack>();
	private ItemStack selectedExtraMaterial = null;
	private HashMap<Integer, Integer> slotIdMap = new HashMap<Integer, Integer>();

	public GuiBlueprint(UUID uuid, EntityPlayer player, IInventory upperInv, IInventory lowerInv) {
		super(new ContainerChest(upperInv, lowerInv, player));
		this.uuid = uuid;
		sharedProperty = new SharedProperty();
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.clearComponents();
		gui.getTransformSolution().wh(323, 199).fitScaledScreen(2.6f).translateToCenter(width, height);

		// 帮助按钮
		String text = "§e§l锻造图纸介绍:\n§f- §7在背包内选择你的锻造图之后\n§f- §7会自动显示锻造所需要的相关材料\n§f- §c锻造会有概率失败，无论成功和失败都会减少图纸的使用次数\n§b§l魔晶介绍:\n§f- §7在锻造时如果选择了魔晶可以提升你的锻造概率\n§f- §7不同的魔晶可以提升不同的概率值\n§f- §7选用后无论锻造成功还是失败魔晶都会被消耗掉\n§c§l如何获取图纸？\n§f- §7通关组队副本后概率掉落§a锻造图纸宝箱§7开启后概率获得图纸\n§f- §7也可以通过使用§a普通矿物核心§7兑换§a图纸福袋§7开启后概率获得图纸\n§f- §7在黑市中也可以刷新出高级图纸\n§c§l如何获取魔晶？\n§f- §7商城中购买魔晶宝箱，开启后概率获得";
		gui.addComponent(new ComponentButtonTextable(0, 154, 8, 11, 11).setText(text));

		// 锻造按钮
		gui.addComponent(upgradeButton = new ComponentButton(1, 105, 74, 49, 12).whenClick(this::onClick)
				.texEnable(ReadyTex.of(TEX_BLUEPRINT, 24, 245, 49, 12))
				.texHover(ReadyTex.of(TEX_BLUEPRINT, 24, 231, 49, 12))
				.texSelected(ReadyTex.of(TEX_BLUEPRINT, 78, 245, 49, 12)));
		upgradeButton.setSelected(!isCraftable);

		//////////////////////////////////////////////////////////////
		// 加载保护石
		JsonObject completeJson = sharedProperty.getCompleteJson();
		int selecteExtraIndex = -1;
		if (completeJson.has("selectedExtraMaterial")) {
			selecteExtraIndex = completeJson.get("selectedExtraMaterial").getAsInt();
		}
		int index = 0;
		selectedExtraMaterial = null;
		List<ItemStack> newMaterials = new ArrayList<ItemStack>();
		if (completeJson.has("extraMaterials")) {
			JsonArray array = completeJson.getAsJsonArray("extraMaterials");
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

				if (selecteExtraIndex == element.getAsInt()) {
					selectedExtraMaterial = copy;
					btn.texHover(ReadyTex.of(TEX_BLUEPRINT, 0, 0, 0, 0));
				}

				index += 1;

			}
		}
		validExtraMaterials = newMaterials;

		gui.setHoverPrecondition(() -> !gui.hasComponent(ComponentMessageBox.class));
	}

	public void onClick(ComponentButton btn) {
		if (btn.id == 1 && isCraftable) {
			this.msg().add("craft", 1).sent();
		} else if (slotIdMap.containsKey(btn.id)) {
			Integer slot = slotIdMap.get(btn.id);
			this.msg().add("select", slot).sent();
		}
	}

	@Override
	public void onMessage(JsonObject jsonObject) {
		if (jsonObject.has("info")) {
			boolean finished = false;
			String text = jsonObject.get("info").getAsString();
			if (jsonObject.has("finished")) {
				finished = jsonObject.get("finished").getAsBoolean();
			}

			if (text == null || text.trim().isEmpty()) {
				handler.removeComponent(ComponentMessageBox.class);
			} else {
				displayInfoComp(jsonObject.get("info").getAsString(), finished);
			}
		}
	}

	@Override
	public void draw(FastGuiHandler gui) {
		gui.bind(TEX_BLUEPRINT);

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
		DrawHelper.drawCenterRolling(disX, disY, 0.4f);

		double bgOffsetX = 0;
		int bgOffsetY = 0;

		// 绘制背景板
		gui.push().translate(8.5 + bgOffsetX, 23 + bgOffsetY, 30).scale2D(1.02);
		gui.drawTex(295, 230 + 5, 164, 89);

		GL11.glPopMatrix();

		///////////////////////////////////////////////

		GL11.glPushMatrix();
		DrawHelper.drawCenterRolling(disX, disY, 0.5f);

		// 绘制界面
		gui.drawTex(19, 20, 323, 199);

		// 绘制槽位
		gui.push().translate(38, 35, 0);
		gui.drawTex(24, 290, 101, 19);

		// 绘制材料提示（如果没有材料）
//		if (!this.inventorySlots.inventorySlots.get(9).getHasStack()) {
//			gui.push().translate(25, 65, 0).scale2D(0.7);
//			gui.drawTex(139, 240, 9, 11);
//			
//			gui.push().translate(35, 65).scale2D(0.7);
//			gui.drawString("所需材料将会在选中图纸后显示");
//		}

		// 绘制提示文本

		if (lastNotify != null && !lastNotify.isEmpty()) {
			gui.pushKeep("lores").translate(128, 60 - (lastNotify.size() - 1) * 4).scale2D(0.70);
			gui.glStateInvokeStart();
			GlStateManager.enableAlpha();
			for (int index = 0; index < lastNotify.size(); index++) {
				String lore = lastNotify.get(index);
				int width = mc.fontRenderer.getStringWidth(lore);
				gui.drawRect(-width / 2 - 1, -2 + index * 13, (int) (width + 6), 12, new Color(0, 0, 0, 0.6f).getRGB());
				gui.push().translate(3, index * 13);
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
		for (ItemStack stack : validExtraMaterials) {
			int row = itemIndex % 6;
			int cow = itemIndex / 6;
			GlStateManager.enableDepth();
			gui.bind(TEX_BLUEPRINT);
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

			if (stack == selectedExtraMaterial) {
				gui.bind(TEX_BLUEPRINT);
				gui.push().translate(8, 5);
				gui.drawTex(350, 42, 10, 11);
			}
			gui.pop();
			itemIndex += 1;
		}
	}

	@Override
	public void setupCustomSlot() {
		slotMap.clear();
		TransformSolution trans = handler.getTransformSolution();
		double scale = trans.scaledX;
		int size = 12;

		int containerSize = this.lowerChestInventory.getSizeInventory();

		// 需求文本信息（从物品lore中获取）
		List<String> requireList = new ArrayList<String>();
		Slot requireSlot = this.inventorySlots.getSlot(1);
		if (requireSlot.getHasStack()) {
			requireList.addAll(StackUtils.getLore(requireSlot.getStack()));
		}

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
					customSlot = new CustomSlot(slot, slot.slotNumber, 42, 39, 13, scale);
				} else if (slot.slotNumber == 27) {
					customSlot = new CustomSlot(slot, slot.slotNumber, 124, 39, 13, scale);
				} else if (slot.slotNumber >= 9 && slot.slotNumber < 27 - 8) {
					// 材料槽
					int idx = slot.slotNumber - 9;
					int row = idx % 5;
					int col = idx / 5;

					String text = String.valueOf(slot.getStack().getCount());
					if (idx < requireList.size()) {
						text = requireList.get(idx);
					}
					customSlot = new SlotMaterial(this, text, slot, slot.slotNumber, 20 + row * 14, 62 + col * 13, 12,
							scale);

				} else {
					customSlot = new CustomSlot(slot, slot.slotNumber, -9999999, -9999999, size, scale);
				}
			}
			if (customSlot != null) {
				slotMap.put(slot, customSlot);
			}

		}
	}

	public boolean hasBlueprint() {
		return inventorySlots.getSlot(9).getHasStack();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void synProperty(JsonObject obj) {
		sharedProperty.synProperty(obj);

		// 同步通知信息
		if (sharedProperty.getCompleteJson().has("lastNotify")) {
			List<String> newNotify = new ArrayList<String>();
			sharedProperty.getCompleteJson().getAsJsonArray("lastNotify")
					.forEach(it -> newNotify.add(it.getAsString()));
			lastNotify = newNotify;
		}
		if (sharedProperty.getCompleteJson().has("craftable")) {
			isCraftable = sharedProperty.getCompleteJson().get("craftable").getAsBoolean();
		}

		initGui();
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	public ItemStack getStack(int slot) {
		if (upperChestInventory == null) {
			return null;
		}
		return upperChestInventory.getStackInSlot(slot);
	}

	public void displayInfoComp(String info, boolean finished) {
		this.handler.removeComponent(ComponentMessageBox.class);
		ComponentMessageBox comp = new ComponentMessageBox(info);
		if (finished) {
			comp.addCallback("确认", () -> handler.removeComponent(ComponentMessageBox.class));
		}
		this.handler.addComponent(comp);
	}

}
