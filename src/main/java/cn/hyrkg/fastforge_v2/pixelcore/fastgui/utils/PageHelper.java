package cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils;

import java.util.ArrayList;
import java.util.List;

public class PageHelper<T> {

	private List<T> list = new ArrayList<>();
	private int pageMax = 1, pageNow = 1;
	private int pageAmount = 1;

	public PageHelper(int amountPerPage) {
		pageAmount = amountPerPage;
	}

	public PageHelper<T> setContext(List<T> contexts) {
		list = contexts;
		updatePageInfo();
		return this;
	}

	public PageHelper<T> updatePageInfo() {
		pageMax = list.size() / pageAmount + (list.size() % pageAmount == 0 ? 0 : 1);
		if (pageMax == 0)
			pageMax = 1;

		if (pageNow > pageMax)
			pageNow = pageMax;
		if (pageNow < 1)
			pageNow = 1;
		return this;
	}

	public PageHelper<T> setPage(int amount) {
		pageNow = amount;
		if (pageNow > pageMax)
			pageNow = 1;
		if (pageNow < 1)
			pageNow = pageMax;
		return updatePageInfo();
	}

	public PageHelper<T> addPage(int amount) {
		pageNow += amount;
		if (pageNow > pageMax)
			pageNow = 1;
		if (pageNow < 1)
			pageNow = pageMax;
		return updatePageInfo();
	}

	public int getPageMax() {
		return pageMax;
	}

	public int getPageNow() {
		return pageNow;
	}

	public String getPageInfo() {
		return pageNow + "/" + pageMax;
	}

	public int getPageAmount() {
		return pageNow;
	}

	public int getAmountNowPage() {
		if (list.isEmpty())
			return 0;
		if (pageNow == pageMax && list.size() % pageAmount == 0)
			return pageAmount;
		return (pageNow == pageMax ? (list.size() % pageAmount) : pageAmount);
	}

	public T getItem(int index) {
		return list.get(getTrueIndex(index));
	}

	public int getTrueIndex(int index) {
		return (pageNow - 1) * pageAmount + index;
	}

	public float getProgress() {
		return (float) (pageNow - 1) / (float) (pageMax - 1);
	}

	public List<T> getContent() {
		return list;
	}

}
