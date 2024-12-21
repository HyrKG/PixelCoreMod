package cn.hyrkg.pixelgame.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public class StackUtils {
	public static List<String> getLore(ItemStack stack) {
		List<String> lore = new ArrayList<String>();
		if (stack == null) {
			return lore;
		}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			return lore;
		}
		if (!tag.hasKey("display")) {
			return lore;
		}
		NBTTagCompound displayTag = tag.getCompoundTag("display");
		if (!displayTag.hasKey("Lore")) {
			return lore;
		}
		for (NBTBase base : displayTag.getTagList("Lore", 8)) {
			lore.add(((NBTTagString) base).getString());
		}
		return lore;
	}
}
