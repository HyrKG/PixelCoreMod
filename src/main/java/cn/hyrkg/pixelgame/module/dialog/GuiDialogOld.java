package cn.hyrkg.pixelgame.module.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.hyrkg.fastforge_v2.pixelcore.fastgui.FastGuiHandler;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list.ComponentDragableScrollBar;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list.ComponentList;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.component.list.ComponentListButton;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.DrawHelper;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.ReadyTex;
import cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils.TickCounter;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.BaseFastForgeGui;
import cn.hyrkg.pixelgame.config.PixelGameConfig;
import cn.hyrkg.pixelgame.core.lib.LibSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class GuiDialogOld extends BaseFastForgeGui {

	protected ComponentList<DialogOption> list = new ComponentList<>(1337, PixelGameConfig.dialog_offset + 484 + 30,
			546, 55 * 5);
	protected ComponentDragableScrollBar scrollBar = new ComponentDragableScrollBar(1305,
			PixelGameConfig.dialog_offset + 488 + 30, 11, 55 * 5 - 4);

	protected TickCounter fadeInCounter = new TickCounter(5, false);

	/** Dialog */
	protected int dialogCutLength = 0;
	protected String dialogContent = "";

	/** Option */
	protected UUID optionCurrentUuid = UUID.randomUUID();
	protected long lastClicked = -1;

	/**
	 * Target Entity
	 */
	// 目标实体，用于渲染
	protected boolean targetEntityLoaded = false;
	protected EntityLivingBase targetEntity = null;

	/**
	 * 
	 * 变量调用
	 * 
	 */

	public EntityLivingBase getTargetEntity() {
		if (targetEntityLoaded) {
			return targetEntity;
		}
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().player == null
				|| Minecraft.getMinecraft().player.world == null) {
			return null;
		}
		this.targetEntityLoaded = true;
		for (Entity entity : Minecraft.getMinecraft().player.world.loadedEntityList) {
			if (!(entity instanceof EntityLivingBase)) {
				continue;
			}
			if (getProperty().targetName.has() && getProperty().targetName.get().equals(entity.getName())) {
				targetEntity = (EntityLivingBase) entity;
				break;
			} else if (getProperty().targetUid.has() && getProperty().targetUid.get().equals(entity.getUniqueID())) {
				targetEntity = (EntityLivingBase) entity;
				break;
			}

		}
		return targetEntity;
	}

	public PropertyDialog getProperty() {
		return this.getSharedProperty().getAsShader(PropertyDialog.class);
	}

	public void setDialog(String content) {
		this.dialogContent = content;
		dialogCutLength = content.length();
	}

	public String getCutDialog() {
		return dialogContent.substring(0, this.dialogContent.length() - dialogCutLength);
	}

	/**
	 * 
	 * 构造函数
	 * 
	 */
	public GuiDialogOld(UUID uuidIn) {
		super(uuidIn);
		fadeInCounter.reset();

		this.setDialog("......");
		List<DialogOption> optionList = new ArrayList<>();
		optionList.add(DialogOption.of(0, "..."));
		list.setCreateListFunction(this::createListButton);
		list.setupDirect(optionList);
	}

	public GuiDialogOld() {
		this(UUID.randomUUID());
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().wh(1920, 1080).fitScaledScreen(0.65f).translateToCenter(width, height);

		gui.clearComponents();

		list.enableAnimate = true;
		list.step(20, 15);
		gui.addComponent(list);

		if (list.getComponents().size() >= 4) {
//			scrollBar.setOnPageUpdate(this::onScrollUpdate).setAlwaysDrawHoverCover(true);
			gui.addComponent(scrollBar);
		}

	}

	public ComponentListButton<DialogOption> createListButton(int index, DialogOption option) {
		ComponentListButton<DialogOption> comp = new ComponentListButton<>(index, 545, 55, 5);
		comp.info(option);
		comp.whenClick(j -> this.onDialogOptionClick((ComponentListButton<DialogOption>) j));
		comp.setConsumerToDraw(this::drawDialogOption);
		comp.texHover(ReadyTex.of(ModuleDialog.TEX_DIALOG, 0, 0, 0, 0));
		return comp;
	}

	/**
	 * 
	 * 组件回调
	 * 
	 */
	public void onScrollUpdate(float progress) {
		list.setProgress(progress);
		list.init(handler);
	}

	public void onDialogOptionClick(ComponentListButton<DialogOption> button) {
		if (button.getInfo().id == -1) {
			this.close();
		} else {

			if (!(lastClicked == -1 || (System.currentTimeMillis() - lastClicked) >= 100)) {
				return;
			}
			this.msg().add("click_option", button.id).add("uid", optionCurrentUuid.toString()).sent();
		}
	}

	@Override
	public void tick() {
		super.tick();
		fadeInCounter.tick();

		if (dialogCutLength > 0) {
			dialogCutLength -= 1;
			if (dialogCutLength % 2 == 0) {
				LibSounds.play("type");
			}
		}

	}

	/**
	 * 
	 * 请求与请求处理
	 * 
	 */

	@Override
	public void onMessage(JsonObject jsonObject) {
		if (jsonObject.has("dialog")) {
			setDialog(jsonObject.get("dialog").getAsString());
		} else if (jsonObject.has("options")) {
			optionCurrentUuid = UUID.fromString(jsonObject.get("uid").getAsString());
			JsonObject optionJson = jsonObject.getAsJsonObject("options");
			List<DialogOption> optionList = new ArrayList<>();
			for (Map.Entry<String, JsonElement> entry : optionJson.entrySet()) {
				optionList.add(DialogOption.of(Integer.parseInt(entry.getKey()), entry.getValue().getAsString()));
				list.setupDirect(optionList);

			}
		} else if (jsonObject.has("clr_options")) {
			list.setupDirect(new ArrayList<>());
		} else if (jsonObject.has("finish")) {
			list.setupDirect(java.util.Arrays.asList(new DialogOption[] { DialogOption.of(-1, "(关闭)") }));
		}
	}

	/**
	 * 
	 * 绘制方法
	 * 
	 */
	public void drawDialogOption(ComponentListButton<DialogOption> button) {
		FastGuiHandler gui = this.handler;
		DialogOption option = button.getInfo();

		boolean isHover = button.isHover();

		gui.bind(ModuleDialog.TEX_DIALOG);
		gui.drawTex(1374, isHover ? 78 : 151, 546, 55);

		gui.push("content").translate(60, 17).scale2D(isHover ? 3 : 2.8);
		gui.drawString("" + option.content);
	}

	@Override
	public void draw(FastGuiHandler gui) {

		DrawHelper.drawRolllingShadow(gui, 0, 0, 5f, 5f);

		gui.bind(ModuleDialog.TEX_DIALOG);
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		gui.pushKeep("offset").translate(0,
				PixelGameConfig.dialog_offset + 800 + 15 * fadeInCounter.percentage(gui.getPartialTicks()), -100)
				.rgba(1f, 1f, 1f, 0.2f);
		gui.drawTex(0, 264, 1920, 816);
		gui.pop("offset");

		gui.bind(ModuleDialog.TEX_DIALOG);
		gui.pushKeep("offset").translate(0, PixelGameConfig.dialog_offset + 800).rgba(1f, 1f, 1f,
				fadeInCounter.percentage(gui.getPartialTicks()));

		gui.glStateInvokeStart();
		gui.drawTex(0, 264, 1920, 816);

		EntityLivingBase targetEntity = null;
		for (Entity entity : Minecraft.getMinecraft().player.world.loadedEntityList) {
			if (!(entity instanceof EntityLivingBase)) {
				continue;
			}
			if (entity.getName().equals(getProperty().title.get())
					|| entity.getUniqueID().equals(getProperty().targetUid.get())) {
				targetEntity = (EntityLivingBase) entity;
			}
		}
		if (targetEntity != null) {
			GuiInventory.drawEntityOnScreen(0, 0, 50, 0, 0, targetEntity);

		}

		// 绘制scrollbar
		if (list.getComponents().size() >= 4) {
			// 拖拽scroll放大
//			if (scrollBar.isScrollbarDragging()) {
//				gui.push("scroolbar").translate(1307, 490 + 30 + (230 * list.getProgress()) - 800, 15).rgba(0, 0, 0, 1)
//						.scale2D(1.3);
//			} else {
//				gui.push("scroolbar").translate(1308, 490 + 30 + (240 * list.getProgress()) - 800, 15);
//			}

			gui.drawTex(1335, 83, 5, 26);
		}

		// 绘制title
		gui.push("title").translate(960, 34).scale2D(3);
		gui.drawCenteredString("§e§l" + getProperty().title.get());

		gui.pushKeep("content").translate(960, 100).scale2D(3 * PixelGameConfig.dialog_words_scale);
		gui.glStateInvokeStart();
		String[] lines = getCutDialog().split("/n");
		for (int i = 0; i < lines.length; i++) {
			gui.push("line").translate(0, i * 15);
			String lineForDraw = lines[i];
			if (lineForDraw.endsWith("/")) {
				lineForDraw = lineForDraw.substring(0, lineForDraw.length() - 1);
			}
			gui.drawCenteredString(lineForDraw);
		}

		gui.pop("content");
		gui.pop("offset");
	}

}
