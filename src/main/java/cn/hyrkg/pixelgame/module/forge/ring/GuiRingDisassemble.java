package cn.hyrkg.pixelgame.module.forge.ring;

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
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.GlState;
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

public class GuiRingDisassemble extends BaseFastContainerGui implements IForgeGui, BlueprintProvider {
	public static final Tex TEX_BLUEPRINT = Tex.of("forge/ring_disassemble");
	public static final Tex TEX_SKILLBAR = Tex.of("forge/skillbar");

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
	private int selectedRingSlot = -1;// 选中的魂环槽位

	// 渐入动画
	private TickCounter fadeInCounter = new TickCounter(15, false);
	// 进阶动画：渐进
	private TickCounter upgradeFadeInCounter = new TickCounter(10, false);
	// 进阶动画：结果
	private TickCounter upgradeResultCounter = new TickCounter(30, false);
	private boolean lastSuccess = false;

	public GuiRingDisassemble(UUID uuid, EntityPlayer player, IInventory upperInv, IInventory lowerInv) {
		super(new ContainerChest(upperInv, lowerInv, player));
		this.uuid = uuid;
		sharedProperty = new SharedProperty();
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;

		upgradeFadeInCounter.tick = upgradeFadeInCounter.tickMax;
		upgradeResultCounter.tick = upgradeResultCounter.tickMax;
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.clearComponents();
		gui.getTransformSolution().wh(245, 210).fitScaledScreen(2.6f).translateToCenter(width, height);

		// 帮助按钮
		String text = "-";
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
				int row = index % 3;
				int cow = index / 3;
				ComponentButton btn;
				handler.addComponent(
						btn = new ComponentItemButton(100 + index, copy, 192 + 2 + row * 19, 31 + cow * 19, 16, 15)
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

		if (completeJson.has("selectedRingSlot")) {
			selectedRingSlot = completeJson.get("selectedRingSlot").getAsInt();
		} else {
			selectedRingSlot = -1;
		}

		for (int i = 0; i < 9; i++) {
			ComponentButton btn;
			handler.addComponent(btn = new ComponentButton(i, -19, 8 + (int) (i * 18.3), 15, 15)
					.whenClick(this::onClickRingSlot).texHover(ReadyTex.of(TEX_BLUEPRINT, 0, 0, 0, 0)));
			btn.setHoverPrecondition(null);
		}

		gui.setHoverPrecondition(() -> !gui.hasComponent(ComponentMessageBox.class));

	}

	public void onClickRingSlot(ComponentButton btn) {
		this.msg().add("select-ring-slot", btn.id).sent();

	}

	public void onClick(ComponentButton btn) {
		if (btn.id == 1 && isCraftable) {
			this.msg().add("craft", 1).sent();
			LibSounds.play("upgrade_use");
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
		} else if (jsonObject.has("start-craft")) {
			upgradeFadeInCounter.tick = 0;
			upgradeFadeInCounter.lastTick = 0;
			LibSounds.play("upgrade_wstart");

		} else if (jsonObject.has("result")) {
			boolean success = jsonObject.get("result").getAsBoolean();
			lastSuccess = success;
			upgradeResultCounter.tick = 0;
			upgradeResultCounter.lastTick = 0;
			upgradeFadeInCounter.tick = upgradeFadeInCounter.tickMax;

			if (lastSuccess) {
				LibSounds.play("upgrade_wsuccess");
			} else {
				LibSounds.play("upgrade_wfail");
			}
		} else if (jsonObject.has("equip")) {
			LibSounds.play("upgrade_use");
		} else if (jsonObject.has("unequip")) {
			LibSounds.play("upgrade_use");
		} else if (jsonObject.has("update-fade")) {
			fadeInCounter.reset();
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

		if (!upgradeFadeInCounter.isDone()) {
			float p = upgradeFadeInCounter.percentage(gui.getPartialTicks());
			bgOffsetX = Math.sin(p * 30);
		}

		// 绘制背景板
		if (!upgradeFadeInCounter.isDone()) {
			float progress = 1f - upgradeFadeInCounter.percentage(gui.getPartialTicks());
			gui.push().translate(8.5 + bgOffsetX, 23 + bgOffsetY, 30).scale2D(1.02).rgba(progress, progress, progress,
					1f);
			gui.drawTex(387, 279, 160, 89);
		} else if (!upgradeResultCounter.isDone()) {
			float progress = upgradeResultCounter.percentage(gui.getPartialTicks());
			GlState state = gui.push().translate(8.5 + bgOffsetX, 23 + bgOffsetY, 30).scale2D(1.02);
			float color = 0.4f + 0.6f * progress;
			if (lastSuccess) {
				state.rgba(1f, 1f, color, 1f);
			} else {
				state.rgba(1f, color, color, 1f);
			}
			gui.drawTex(387, 279, 160, 89);
		} else {
			float progressFade = fadeInCounter.percentage(gui.getPartialTicks());
			gui.push().translate(8.5 + bgOffsetX, 23 + bgOffsetY, 30).scale2D(1.02).rgba(progressFade, progressFade,
					progressFade, 1f);
			gui.drawTex(387, 279, 160, 89);
		}

		GL11.glPopMatrix();

		///////////////////////////////////////////////

		GL11.glPushMatrix();
		DrawHelper.drawCenterRolling(disX, disY, 0.5f);

		// 绘制界面
		gui.drawTex(19, 20, 323, 199);

		// 绘制槽位
		gui.push().translate(43, 35, 0);
		gui.drawTex(24, 290, 32, 19);

		// 绘制提示文本

		if (lastNotify != null && !lastNotify.isEmpty()) {
			gui.pushKeep("lores").translate(128, 47 - (lastNotify.size() - 1) * 4).scale2D(0.70);
			gui.glStateInvokeStart();
			GlStateManager.enableAlpha();
			for (int index = 0; index < lastNotify.size(); index++) {
				String lore = lastNotify.get(index);
				if (lore.isEmpty()) {
					continue;
				}
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
			int row = itemIndex % 3;
			int cow = itemIndex / 3;
			GlStateManager.enableDepth();
			gui.bind(TEX_BLUEPRINT);
			gui.pushKeep("g").translate(192 + 2 + row * 19, 31 + cow * 19);
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

		////////////////////////////////////////////////
		//
		// 绘制侧边技能栏
		GlStateManager.color(1, 1, 1);
		gui.bind(TEX_SKILLBAR);
		gui.push().translate(-30, 1).scale2D(0.131);
		gui.drawFullTex();

		if (selectedRingSlot != -1) {
			gui.bind(TEX_BLUEPRINT);
			GlStateManager.enableDepth();
			GlStateManager.enableAlpha();
			gui.push().translate(-13, 13 + 18.3 * selectedRingSlot, 500);
			gui.drawTex(350, 42, 10, 11);
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
					customSlot = new CustomSlot(slot, slot.slotNumber, 47, 39, 13, scale);
				} else if (slot.slotNumber >= 9 && slot.slotNumber < 27 - 8) {
					// 材料槽
					int idx = slot.slotNumber - 9;
					int row = idx % 5;
					int col = idx / 5;

					String text = String.valueOf(slot.getStack().getCount());
					if (idx < requireList.size()) {
						text = requireList.get(idx);
					}
					customSlot = new RingSlotMaterial(this, text, slot, slot.slotNumber, 20 + row * 14, 62 + col * 13,
							12, scale);
				} else if (slot.slotNumber >= 27 && slot.slotNumber <= 27 + 8) {
					// 材料槽
					int idx = slot.slotNumber - 27;
					String text = String.valueOf(slot.getStack().getCount());
					if (idx < requireList.size()) {
						text = requireList.get(idx);
					}
					customSlot = new CustomSlot(slot, slot.slotNumber, -18, 11 + (int) (idx * 18.3), size, scale)
							.setAllow(false);

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
		fadeInCounter.tick();

		if (!upgradeFadeInCounter.isDone() && upgradeFadeInCounter.tickMax - upgradeFadeInCounter.tick > 1) {
			upgradeFadeInCounter.tick();
			if (upgradeFadeInCounter.tickMax - upgradeFadeInCounter.tick == 2) {
				this.msg().add("craft-confirm", 1).sent();
			}
		}
		upgradeResultCounter.tick();

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

	@Override
	protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
		if (!fadeInCounter.isDone()) {
			return;
		}
		super.renderHoveredToolTip(p_191948_1_, p_191948_2_);
	}

	@Override
	protected void renderToolTip(ItemStack stack, int x, int y) {
		// TODO Auto-generated method stub
		super.renderToolTip(stack, x, y);
	}

}
