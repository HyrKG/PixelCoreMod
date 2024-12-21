package cn.hyrkg.pixelgame.module.dialog;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

public class GuiDialog extends BaseFastForgeGui {

	protected ComponentList<DialogOption> list = new ComponentList<>(200, 600, 900, 60 * 5);

	protected TickCounter fadeInCounter = new TickCounter(5, false);

	/** Dialog */
	protected double dialogCutLength = 0;
	protected String dialogContent = "";
	protected int dialogCutPause = 0;

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
		return dialogContent.substring(0, this.dialogContent.length() - (int) dialogCutLength);
	}

	@Override
	public void onWheelInput(int value) {
		list.setProgress(Math.max(0, Math.min(list.getProgress() + value * 0.5f, 1)));
		list.init(handler);
	}

	/**
	 * 
	 * 构造函数
	 * 
	 */
	public GuiDialog(UUID uuidIn) {
		super(uuidIn);
		fadeInCounter.reset();

		this.setDialog("......");
		List<DialogOption> optionList = new ArrayList<>();
		optionList.add(DialogOption.of(0, "..."));
		list.setCreateListFunction(this::createListButton);
		list.setupDirect(optionList);
	}

	public GuiDialog() {
		this(UUID.randomUUID());
	}

	@Override
	public void fastInitGui(FastGuiHandler gui) {
		gui.getTransformSolution().fitScaledScreen(0.9f);
		gui.clearComponents();
		list.enableAnimate = true;
		list.step(30, 15);
		gui.addComponent(list);
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
	 * 绘制方法
	 * 
	 */
	public void drawDialogOption(ComponentListButton<DialogOption> button) {
		if (dialogCutLength > 0) {
			return;
		}

		FastGuiHandler gui = this.handler;
		DialogOption option = button.getInfo();

		boolean isHover = button.isHover();

		StringBuilder prefixBuilder = new StringBuilder();
		if (list.getComponents().size() > 5) {
			prefixBuilder.append("§7" + (button.id + 1) + "/" + list.getComponents().size() + " ");
		}

		if (isHover) {
			prefixBuilder.append("§e▶ ");

		} else {
			prefixBuilder.append("§6▷ ");
		}

		gui.push("content").translate(60, 17).scale2D(isHover ? 3.3 : 3);
		gui.drawString(prefixBuilder.toString() + option.content);

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
		if (dialogCutLength > 5) {
			return;
		}

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

		if (dialogCutPause > 0) {
			dialogCutPause -= 1;
		} else if (dialogCutLength > 0) {
			dialogCutLength -= 1;
			if (dialogCutLength < 0) {
				dialogCutLength = 0;
			}

			String nowDialog = getCutDialog();

			if (nowDialog.endsWith(".")) {
				dialogCutPause = 3;
			} else if (nowDialog.endsWith("？")) {
				dialogCutPause = 6;
			} else if (nowDialog.endsWith("！") || nowDialog.endsWith("!")) {
				dialogCutPause = 4;
			} else if (nowDialog.endsWith("。")) {
				dialogCutPause = 6;
			} else if (nowDialog.endsWith(",") || nowDialog.endsWith("，")) {
				dialogCutPause = 4;
			}
			if ((int) dialogCutLength % 2 == 0 || dialogCutPause > 0) {
				LibSounds.play("type");
			}
			if (dialogCutLength % 3 == 0) {
				tick();
			}
		}

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		dialogCutLength -= 15;
		if (dialogCutLength <= 0) {
			dialogCutLength = 0;
		}
		dialogCutPause = 0;
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

	@Override
	public void draw(FastGuiHandler gui) {

//		DrawHelper.drawRolllingShadow(gui, 0, 0, 5f, 5f);
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);

		// 绘制标题
		gui.push("title").translate(250, 160).scale2D(7);
		gui.drawString("§f§l" + getProperty().title.get());

		gui.pushKeep("content").translate(250, 300).scale2D(3 * PixelGameConfig.dialog_words_scale);
		gui.glStateInvokeStart();
		String[] lines = getCutDialog().split("/n");
		for (int i = 0; i < lines.length; i++) {
			gui.push("line").translate(0, i * 15);
			String lineForDraw = lines[i];
			if (lineForDraw.endsWith("/")) {
				lineForDraw = lineForDraw.substring(0, lineForDraw.length() - 1);
			} else if (lineForDraw.endsWith("§")) {
				lineForDraw = lineForDraw.substring(0, lineForDraw.length() - 1);
			}
			gui.drawString(lineForDraw);
		}

		gui.pop("content");

		if (dialogCutLength <= 0 && list.getComponents().size() > 5) {
			gui.push("hint").translate(300, 915).scale2D(2.5);
			gui.drawString("§7[ 使用滑轮 §f▼ §7查看更多 ]");
		}

		if (getTargetEntity() != null) {
			double scale = gui.getTransformSolution().scaledX;
			GlStateManager.color(1, 1, 1);
			GuiInventory.drawEntityOnScreen(1370, 980, 390, 15 + (int) (-gui.getLastMouseX() * scale * 0.03),
					6 + (int) (-gui.getLastMouseY() * scale * 0.03), getTargetEntity());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
		GlStateManager.enableTexture2D();
		GlStateManager.disableDepth();

		int right = width, top = 0, left = 0, bottom = height;
		int startColor = new Color(0, 0, 0, (int) (fadeInCounter.percentage(partialTicks) * 245)).getRGB();
		int endColor = new Color(0, 0, 0, (int) (fadeInCounter.percentage(partialTicks) * 180)).getRGB();

		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) left, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos((double) right, (double) top, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();

		handler.drawScreen(mouseX, mouseY, partialTicks);
	}

}
