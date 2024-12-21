package cn.hyrkg.pixelgame.dto.discount_market;

import net.minecraft.inventory.Slot;

public class StorageItem {
	public String shortname = null;
	// 绑定槽位
	public int slot;
	// 副标题
	public String subtitle;
	// 条幅标题(可以为null)
	public String bannerTitle = null;
	// 价格（点券、金币）
	public String points = null;
	public String coins = null;
	// 是否售罄
	public boolean soldOut = false;
}
