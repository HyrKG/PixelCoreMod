package cn.hyrkg.pixelgame.module.discount_market;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.ComponentButtonTextable;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.BaseFastContainerGui;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.instance.CustomSlot;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.Tex;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.IForgeGui;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SharedProperty;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SimpleMsg;
import cn.hyrkg.pixelgame.client.component.ComponentMessageBox;
import cn.hyrkg.pixelgame.dto.discount_market.StorageGroup;
import cn.hyrkg.pixelgame.dto.discount_market.StorageItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class GuiDiscountMarket extends BaseFastContainerGui implements IForgeGui {
	private final static Gson gson = new Gson();

	public static final Tex TEX_UI = Tex.of("discount_market/ui");

	private final UUID uuid;
	protected SharedProperty sharedProperty;

	public final IInventory upperChestInventory;
	public final IInventory lowerChestInventory;

	public GuiDiscountMarket(UUID uuid, EntityPlayer player, IInventory upperInv, IInventory lowerInv) {
		super(new ContainerChest(upperInv, lowerInv, player));
		CompStorageContainer.offsetY = 0;
		this.uuid = uuid;
		sharedProperty = new SharedProperty();
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;

		handler.skipDrawAfterAll = true;
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.clearComponents();
		gui.getTransformSolution().wh(305, 179).fitScaledScreen(3.5f).translateToCenter(width, height);

		gui.addComponent(new ComponentButton(0, 289, 3, 11, 11).whenClick(this::onClick));
		gui.addComponent(
				new ComponentButtonTextable(1, 135, 161, 56, 10).setText("充值后点击此处确认收货").whenClick(this::onClick));
		gui.addComponent(new ComponentButtonTextable(2, 121, 161, 10, 10).setText("更新黑市状态").whenClick(this::onClick));
		setupCustomSlot();

		// 提示信息
		gui.addComponent(new ComponentButtonTextable(-1, 197, 160, 40, 11).setText("§e金魂币")
				.texHover(ReadyTex.of(TEX_UI, 0, 0, 0, 0)));
		gui.addComponent(new ComponentButtonTextable(-1, 241, 160, 40, 11).setText("§b晶钻")
				.texHover(ReadyTex.of(TEX_UI, 0, 0, 0, 0)));

		// 库存主容器
		gui.addComponent(new CompStorageContainer(this, 19, 37, 267, 115));

		gui.setHoverPrecondition(() -> !gui.hasComponent(ComponentMessageBox.class));

	}

	public void onClick(ComponentButton btn) {
		if (btn.id == 0) {
			close();
		} else if (btn.id == 1) {
			Minecraft.getMinecraft().player.sendChatMessage("/nedeals");
			this.msg().add("nedeals", 1).sent();// 通知更新晶钻
			displayInfoComp("正在收货中\n大概需要1秒钟...", false);
		} else if (btn.id == 2) {
			displayInfoComp("正在请求...", false);
			this.msg().add("refresh", 1).sent();// 通知更新黑市
		}
	}

	public void onPurchase(StorageItem item) {
		SimpleMsg msg = this.msg().add("purchase", item.slot);
		if (item.shortname != null) {
			msg.add("shortname", item.shortname);
		}
		msg.sent();
		displayInfoComp("请求中...", false);
	}

	@Override
	public void draw(FastGuiHandler gui) {
		gui.bind(TEX_UI);

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		gui.drawTex(0, 0, 305, 179);

		gui.push().translate(46, 24).scale2D(0.8);
		gui.drawCenteredString("§l全部", -593450);

		gui.push().translate(163, 163).scale2D(0.8);
		gui.drawCenteredString("§l收 货", -593450);

		gui.push().translate(208, 162).scale2D(0.85);
		gui.drawString("§e" + sharedProperty.getAsString("coins"));

		gui.push().translate(254, 162).scale2D(0.85);
		gui.drawString("§b" + sharedProperty.getAsString("points"));

		if (lastTitle != null && lastTitle.size() > 0) {
			for (int i = 0; i < lastTitle.size(); i++) {
				gui.push().translate(24, 163 - (lastTitle.size() - 1) * 3 + i * 6).scale2D(0.6);
				gui.drawString(lastTitle.get(i));
			}
		}

	}

	/* 删除所有槽位，用组件来代替渲染 */
	@Override
	public void setupCustomSlot() {
		slotMap.clear();
		for (Slot slot : this.inventorySlots.inventorySlots) {
			slotMap.put(slot, new CustomSlot(slot, slot.slotNumber, -9999999, -9999999, 1, 1));
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	private List<StorageGroup> groupCache = null;
	private List<String> lastTitle;

	public List<StorageGroup> getStorageGroups() {
		if (groupCache != null) {
			return groupCache;
		}
		List<StorageGroup> newGroup = new ArrayList<StorageGroup>();
		if (sharedProperty.getCompleteJson().has("groups")) {
			JsonArray array = sharedProperty.getCompleteJson().getAsJsonArray("groups");
			for (JsonElement element : array) {
				StorageGroup group = gson.fromJson(element.getAsString(), StorageGroup.class);
				if (group == null) {
					continue;
				}
				if (group.expireTimeLeft != -1) {
					group.cacheEndTime = System.currentTimeMillis() + group.expireTimeLeft;
				}
				newGroup.add(group);
			}
		}
		groupCache = newGroup;
		return groupCache;
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

	public void displayInfoComp(String info, boolean finished) {
		this.handler.removeComponent(ComponentMessageBox.class);
		ComponentMessageBox comp = new ComponentMessageBox(info);
		if (finished) {
			comp.addCallback("确认", () -> handler.removeComponent(ComponentMessageBox.class));
		}
		this.handler.addComponent(comp);
	}

	@Override
	public void synProperty(JsonObject obj) {
		sharedProperty.synProperty(obj);
		if (obj.has("groups")) {
			groupCache = null;
		}
		initGui();

		if (sharedProperty.getCompleteJson().has("title")) {
			String text = sharedProperty.getCompleteJson().get("title").getAsString();
			lastTitle = mc.fontRenderer.listFormattedStringToWidth(text, 999);
		} else {
			lastTitle = null;
		}
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}
}
