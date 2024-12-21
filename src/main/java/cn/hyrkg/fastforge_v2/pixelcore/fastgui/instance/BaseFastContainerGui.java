package cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Sets;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.TransformSolution;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.IComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseFastContainerGui extends BaseFastGuiScreen {
	/** The location of the inventory background texture */

	/** holds the slot currently hovered */
	protected Slot hoveredSlot;
	/** Used when touchscreen is enabled. */
	protected Slot clickedSlot;
	/** Used when touchscreen is enabled. */
	protected boolean isRightMouseClick;
	/** Used when touchscreen is enabled */
	protected ItemStack draggedStack = ItemStack.EMPTY;
	protected int touchUpX;
	protected int touchUpY;
	protected Slot returningStackDestSlot;
	protected long returningStackTime;
	/** Used when touchscreen is enabled */
	protected ItemStack returningStack = ItemStack.EMPTY;
	protected Slot currentDragTargetSlot;
	protected long dragItemDropDelay;
	protected final Set<Slot> dragSplittingSlots = Sets.<Slot>newHashSet();
	protected boolean dragSplitting;
	protected int dragSplittingLimit;
	protected int dragSplittingButton;
	protected boolean ignoreMouseUp;
	protected int dragSplittingRemnant;
	protected long lastClickTime;
	protected Slot lastClickSlot;
	protected int lastClickButton;
	protected boolean doubleClick;
	protected ItemStack shiftClickedSlot = ItemStack.EMPTY;

	public Container inventorySlots;

	public boolean disablePlayerInventoryShift = false;
	public boolean allowShortcutKey = false;
	public boolean allowShiftPick = false;

	public static CustomSlot EMPTY_SLOT = new CustomSlot(Minecraft.getMinecraft().player.inventoryContainer.getSlot(0),
			0, 0, 0, 16, 1, 1);
	protected HashMap<Slot, CustomSlot> slotMap = new HashMap<>();

	public BaseFastContainerGui(Container contaier) {

		this.inventorySlots = contaier;

		this.ignoreMouseUp = true;
		this.allowUserInput = false;
	}

	// 在此对所有坐标系进行偏操作
	public CustomSlot getCustomSlot(Slot slot) {
		if (slotMap.containsKey(slot))
			return slotMap.get(slot);
		return EMPTY_SLOT;
	}

	public abstract void setupCustomSlot();

	public void initGui() {
		TransformSolution trans = handler.getTransformSolution();

		this.mc.player.openContainer = this.inventorySlots;
		super.initGui();

		setupCustomSlot();

		slotMap.values().forEach(j -> j.offset(trans.getX(), trans.getY()));
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		this.hoveredSlot = null;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
			Slot slot = this.inventorySlots.inventorySlots.get(i1);
			CustomSlot customSlot = getCustomSlot(slot);

			if (customSlot.bindingSlot.isEnabled()) {
				this.drawSlot(slot);
			}

			// 绘制悬浮框
			if (this.isMouseOverSlot(customSlot, slot, mouseX, mouseY) && slot.isEnabled()) {
				this.hoveredSlot = slot;
				customSlot.drawRect(this, mouseX, mouseY);
			}
		}

		if (this.hoveredSlot != null) {
			CustomSlot customSlot = getCustomSlot(this.hoveredSlot);
			if (customSlot != null) {
				customSlot.drawCover(this, mouseX, mouseY);
			}
		}

		RenderHelper.disableStandardItemLighting();
//		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for (IComponent component : handler.getComponents()) {
			component.drawAfterAll(handler);
		}
		RenderHelper.enableGUIStandardItemLighting();
		InventoryPlayer inventoryplayer = this.mc.player.inventory;
		ItemStack itemstack = this.draggedStack.isEmpty() ? inventoryplayer.getItemStack() : this.draggedStack;

		// 绘制悬浮物品
		if (!itemstack.isEmpty()) {
			int j2 = 8;
			int k2 = this.draggedStack.isEmpty() ? 8 : 16;
			String s = null;

			if (!this.draggedStack.isEmpty() && this.isRightMouseClick) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2.0F));
			} else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(this.dragSplittingRemnant);

				if (itemstack.isEmpty()) {
					s = "" + TextFormatting.YELLOW + "0";
				}
			}

			this.drawItemStack(itemstack, mouseX - 8, mouseY - k2, s);
		}

		// 未知，无用代码
		if (!this.returningStack.isEmpty()) {
			float f = (float) (Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

			if (f >= 1.0F) {
				f = 1.0F;
				this.returningStack = ItemStack.EMPTY;
			}

			int l2 = this.returningStackDestSlot.xPos - this.touchUpX;
			int i3 = this.returningStackDestSlot.yPos - this.touchUpY;
			int l1 = this.touchUpX + (int) ((float) l2 * f);
			int i2 = this.touchUpY + (int) ((float) i3 * f);
			this.drawItemStack(this.returningStack, l1, i2, (String) null);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();

		this.renderHoveredToolTip(mouseX, mouseY);

	}

	protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
		if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null
				&& this.hoveredSlot.getHasStack()) {
			this.renderToolTip(this.hoveredSlot.getStack(), p_191948_1_, p_191948_2_);
		}
	}

	/**
	 * Draws an ItemStack.
	 * 
	 * The z index is increased by 32 (and not decreased afterwards), and the item
	 * is then rendered at z=200.
	 */
	public void drawItemStack(ItemStack stack, int x, int y, String altText) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
	}

	// 以下为原生代码

	/**
	 * 
	 * Origin Methods
	 * 
	 **/

	/**
	 * Draws the given slot: any item in it, the slot's background, the hovered
	 * highlight, etc.
	 */
	public void drawSlot(Slot slotIn) {

		CustomSlot xy = getCustomSlot(slotIn);
		ItemStack itemstack = slotIn.getStack();
		boolean placed = false;
		boolean isDragging = slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
		ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
		String s = null;

		if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick
				&& !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		} else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
			if (this.dragSplittingSlots.size() == 1) {
				return;
			}

			if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
				itemstack = itemstack1.copy();
				placed = true;
				Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack,
						slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
				int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));

				if (itemstack.getCount() > k) {
					s = TextFormatting.YELLOW.toString() + k;
					itemstack.setCount(k);
				}
			} else {
				this.dragSplittingSlots.remove(slotIn);
				this.updateDragSplitting();
			}
		}

		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;

		if (itemstack.isEmpty() && slotIn.isEnabled()) {
			TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

			if (textureatlassprite != null) {
				GlStateManager.disableLighting();
				this.mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
//				this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
				GlStateManager.enableLighting();
//				isDragging = true;
			}
		}

		if (!isDragging) {
			if (placed) {
//				drawRect(i, j, i + 16, j + 16, -2130706433);
				xy.drawHover(this);
			}
			xy.drawItem(this.mc, this.itemRender, itemstack, s);
		}

		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	public void updateDragSplitting() {
		ItemStack itemstack = this.mc.player.inventory.getItemStack();

		if (!itemstack.isEmpty() && this.dragSplitting) {
			if (this.dragSplittingLimit == 2) {
				this.dragSplittingRemnant = itemstack.getMaxStackSize();
			} else {
				this.dragSplittingRemnant = itemstack.getCount();

				for (Slot slot : this.dragSplittingSlots) {
					ItemStack itemstack1 = itemstack.copy();
					ItemStack itemstack2 = slot.getStack();
					int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
					Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
					int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

					if (itemstack1.getCount() > j) {
						itemstack1.setCount(j);
					}

					this.dragSplittingRemnant -= itemstack1.getCount() - i;
				}
			}
		}
	}

	/**
	 * Returns the slot at the given coordinates or null if there is none.
	 */
	public Slot getSlotAtPosition(int x, int y) {
		for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = this.inventorySlots.inventorySlots.get(i);

			CustomSlot pos = getCustomSlot(slot);

			if (this.isMouseOverSlot(pos, slot, x, y) && slot.isEnabled()) {
				return slot;
			}
		}

		return null;
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		boolean flag = this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100);
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);

		// 判断是否禁止点击
		if (getCustomSlot(slot) != null) {
			if (!getCustomSlot(slot).isAllow()) {
				return;
			}
		}

		long i = Minecraft.getSystemTime();
		this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L
				&& this.lastClickButton == mouseButton;
		this.ignoreMouseUp = false;

		if (mouseButton == 0 || mouseButton == 1 || flag) {
			// TODO fix it
			int j = 0;
			int k = 0;
			boolean flag1 = this.hasClickedOutside(mouseX, mouseY, j, k);
			if (slot != null)
				flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
			int l = -1;

			if (slot != null) {
				l = slot.slotNumber;
			}

			if (flag1) {
				l = -999;
			}

			if (this.mc.gameSettings.touchscreen && flag1 && this.mc.player.inventory.getItemStack().isEmpty()) {
				this.mc.displayGuiScreen((GuiScreen) null);
				return;
			}

			if (l != -1) {
				if (this.mc.gameSettings.touchscreen) {
					if (slot != null && slot.getHasStack()) {
						this.clickedSlot = slot;
						this.draggedStack = ItemStack.EMPTY;
						this.isRightMouseClick = mouseButton == 1;
					} else {
						this.clickedSlot = null;
					}
				} else if (!this.dragSplitting) {
					if (this.mc.player.inventory.getItemStack().isEmpty()) {
						if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
							this.handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
						} else {
							handleClickItem(l, slot, mouseButton);
						}

						this.ignoreMouseUp = true;
					} else {
						this.dragSplitting = true;
						this.dragSplittingButton = mouseButton;
						this.dragSplittingSlots.clear();

						if (mouseButton == 0) {
							this.dragSplittingLimit = 0;
						} else if (mouseButton == 1) {
							this.dragSplittingLimit = 1;
						} else if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
							this.dragSplittingLimit = 2;
						}
					}
				}
			}
		}

		this.lastClickSlot = slot;
		this.lastClickTime = i;
		this.lastClickButton = mouseButton;

	}

	public void handleClickItem(int l, Slot slot, int mouseButton) {
		boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
		ClickType clicktype = ClickType.PICKUP;

		if (flag2) {
			this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
			clicktype = ClickType.QUICK_MOVE;
		} else if (l == -999) {
			clicktype = ClickType.THROW;
		}

		this.handleMouseClick(slot, l, mouseButton, clicktype);
	}

	protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_) {
		return !handler.isHover();
	}

	/**
	 * Called when a mouse button is pressed and the mouse is moved around.
	 * Parameters are : mouseX, mouseY, lastButtonClicked & timeSinceMouseClick.
	 */
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//		if (true)
//			return;

		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = this.mc.player.inventory.getItemStack();

		if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
			if (clickedMouseButton == 0 || clickedMouseButton == 1) {
				if (this.draggedStack.isEmpty()) {
					if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty()) {
						this.draggedStack = this.clickedSlot.getStack().copy();
					}
				} else if (this.draggedStack.getCount() > 1 && slot != null
						&& Container.canAddItemToSlot(slot, this.draggedStack, false)) {
					long i = Minecraft.getSystemTime();

					if (this.currentDragTargetSlot == slot) {
						if (i - this.dragItemDropDelay > 500L) {
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
							this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
							this.dragItemDropDelay = i + 750L;
							this.draggedStack.shrink(1);
						}
					} else {
						this.currentDragTargetSlot = slot;
						this.dragItemDropDelay = i;
					}
				}
			}
		} else if (this.dragSplitting && slot != null && !itemstack.isEmpty()
				&& (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2)
				&& Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack)
				&& this.inventorySlots.canDragIntoSlot(slot)) {
			this.dragSplittingSlots.add(slot);
			this.updateDragSplitting();
		}
	}

	/**
	 * Called when a mouse button is released.
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state); // Forge, Call parent to release buttons
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		// TODO fix it
		int i = 0;
		int j = 0;
		boolean flag = this.hasClickedOutside(mouseX, mouseY, i, j);
		if (slot != null)
			flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		int slotNumber = -1;

		if (slot != null) {
			slotNumber = slot.slotNumber;
		}

		if (flag) {
			slotNumber = -999;
		}

		// 双击
		if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot(ItemStack.EMPTY, slot)) {
			if (isShiftKeyDown()) {
				if (!this.shiftClickedSlot.isEmpty()) {
					for (Slot slot2 : this.inventorySlots.inventorySlots) {
						if (slot2 != null && slot2.canTakeStack(this.mc.player) && slot2.getHasStack()
								&& slot2.isSameInventory(slot)
								&& Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
							this.handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
						}
					}
				}
			} else {
				this.handleMouseClick(slot, slotNumber, state, ClickType.PICKUP_ALL);
			}

			this.doubleClick = false;
			this.lastClickTime = 0L;
		} else {
			if (this.dragSplitting && this.dragSplittingButton != state) {
				this.dragSplitting = false;
				this.dragSplittingSlots.clear();
				this.ignoreMouseUp = true;
				return;
			}

			if (this.ignoreMouseUp) {
				this.ignoreMouseUp = false;
				return;
			}

			if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
				if (state == 0 || state == 1) {
					if (this.draggedStack.isEmpty() && slot != this.clickedSlot) {
						this.draggedStack = this.clickedSlot.getStack();
					}

					boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);

					if (slotNumber != -1 && !this.draggedStack.isEmpty() && flag2) {
						this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
						this.handleMouseClick(slot, slotNumber, 0, ClickType.PICKUP);

						if (this.mc.player.inventory.getItemStack().isEmpty()) {
							this.returningStack = ItemStack.EMPTY;
						} else {
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state,
									ClickType.PICKUP);
							this.touchUpX = mouseX - i;
							this.touchUpY = mouseY - j;
							this.returningStackDestSlot = this.clickedSlot;
							this.returningStack = this.draggedStack;
							this.returningStackTime = Minecraft.getSystemTime();
						}
					} else if (!this.draggedStack.isEmpty()) {
						this.touchUpX = mouseX - i;
						this.touchUpY = mouseY - j;
						this.returningStackDestSlot = this.clickedSlot;
						this.returningStack = this.draggedStack;
						this.returningStackTime = Minecraft.getSystemTime();
					}

					this.draggedStack = ItemStack.EMPTY;
					this.clickedSlot = null;
				}
			} else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
				this.handleMouseClick((Slot) null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit),
						ClickType.QUICK_CRAFT);

				for (Slot slot1 : this.dragSplittingSlots) {
					this.handleMouseClick(slot1, slot1.slotNumber,
							Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
				}

				this.handleMouseClick((Slot) null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit),
						ClickType.QUICK_CRAFT);
			} else if (!this.mc.player.inventory.getItemStack().isEmpty()) {
				if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(state - 100)) {
					this.handleMouseClick(slot, slotNumber, state, ClickType.CLONE);
				} else {
					boolean flag1 = slotNumber != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

					if (flag1) {
						this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy()
								: ItemStack.EMPTY;
					}

					this.handleMouseClick(slot, slotNumber, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
				}
			}
		}

		if (this.mc.player.inventory.getItemStack().isEmpty()) {
			this.lastClickTime = 0L;
		}

		this.dragSplitting = false;
	}

	/**
	 * Returns whether the mouse is over the given slot.
	 */
	public boolean isMouseOverSlot(CustomSlot customSlot, Slot slotIn, int mouseX, int mouseY) {
		return customSlot.isHover(mouseX, mouseY);
		// return this.isPointInRegion(pos[0], pos[1], 16, 16, mouseX, mouseY);
	}
//
//	/**
//	 * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX,
//	 * rectY, rectWidth, rectHeight, pointX, pointY
//	 */
//	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
//		int i = this.offsetW;
//		int j = this.offsetH;
//		pointX = pointX - i;
//		pointY = pointY - j;
//		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1
//				&& pointY < rectY + rectHeight + 1;
//	}

	/**
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		if (slotIn != null) {
			slotId = slotIn.slotNumber;
		}

		if (!allowShiftPick) {
			if (type == ClickType.QUICK_MOVE)
				return;
		}

		if (disablePlayerInventoryShift && type == ClickType.QUICK_MOVE && slotIn.inventory instanceof InventoryBasic) {
			return;
		}

//		new RuntimeException().printStackTrace();

		this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, mouseButton, type, this.mc.player);
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			this.mc.player.closeScreen();
		}

		super.keyTyped(typedChar, keyCode);
		this.checkHotbarKeys(keyCode);

		if (allowShortcutKey) {
			if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
				if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
					this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
				} else if (this.mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
					this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0,
							ClickType.THROW);
				}
			}
		}
	}

	/**
	 * Checks whether a hotbar key (to swap the hovered item with an item in the
	 * hotbar) has been pressed. If so, it swaps the given items. Returns true if a
	 * hotbar key was pressed.
	 */
	protected boolean checkHotbarKeys(int keyCode) {
		if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
			for (int i = 0; i < 9; ++i) {
				if (this.mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(keyCode)) {
					this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		if (this.mc.player != null) {
			this.inventorySlots.onContainerClosed(this.mc.player);
		}
		super.onGuiClosed();
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();

//		if (textLeft > 0) {
//			textLeft -= 1;
//			if (textLeft % 3 == 0)
//				Minecraft.getMinecraft().player.playSound(LibSounds.word_type, 1f, 1f);
//		}

		if (!this.mc.player.isEntityAlive() || this.mc.player.isDead) {
			this.mc.player.closeScreen();
		}
	}

	/*
	 * ======================================== FORGE START
	 * =====================================
	 */

	/**
	 * Returns the slot that is currently displayed under the mouse.
	 */
	@javax.annotation.Nullable
	public Slot getSlotUnderMouse() {
		return this.hoveredSlot;
	}
	/*
	 * ======================================== FORGE END
	 * =====================================
	 */
}
