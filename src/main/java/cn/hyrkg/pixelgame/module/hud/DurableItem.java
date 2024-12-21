package cn.hyrkg.pixelgame.module.hud;

public class DurableItem<I> {

	public final I item;
	public final long totalDurability;
	public long durability;

	private DurableItem(I item, long durability) {
		this.item = item;
		this.totalDurability = durability;
		this.durability = durability;
	}

	public static <I> DurableItem<I> of(I item, long durability) {
		return new DurableItem<I>(item, durability);
	}

	public static <I> DurableItem<I> of(I item) {
		return new DurableItem<I>(item, -1);
	}
}
