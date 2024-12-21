package cn.hyrkg.pixelgame.dto.discount_market;

import java.util.ArrayList;
import java.util.List;

public class StorageGroup {
	// 组名
	public String name;
	// 物品内容
	public List<StorageItem> items = new ArrayList<>();
	// 过期时间（经过转义为本地时间），-1即为不展示（没有）
	public Long expireTimeLeft = -1L;

	// 本地缓存
	public Long cacheEndTime = -1L;
}
