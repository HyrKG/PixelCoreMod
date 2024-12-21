package cn.hyrkg.fastforge_v2.pixelcore.fastgui.utils;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class JsonHelper {
	public static ItemStack unpackJsonItem(JsonObject ijson) {
		Item item = Item.getItemById(ijson.get("id").getAsInt());
		ItemStack itemstack = new ItemStack(item);
		itemstack.setItemDamage(ijson.get("damage").getAsInt());

		if (ijson.has("display"))
			itemstack.setStackDisplayName(ijson.get("display").getAsString());
		if (ijson.has("amount"))
			itemstack.setCount(ijson.get("amount").getAsInt());
		if (ijson.has("lore")) {
			NBTTagCompound tag = itemstack.getTagCompound();
			if (tag == null)
				tag = new NBTTagCompound();

			NBTTagCompound td;

			if (!tag.hasKey("display")) {
				tag.setTag("display", td = new NBTTagCompound());
			} else {
				td = tag.getCompoundTag("display");
			}

			JsonArray arr = ijson.get("lore").getAsJsonArray();
			Iterator<JsonElement> strs = arr.iterator();

			NBTTagList list = new NBTTagList();

			JsonPrimitive jp;
			while (strs.hasNext()) {
				jp = (JsonPrimitive) strs.next();
				list.appendTag(new NBTTagString(jp.getAsString()));
				// System.out.println(jp.getAsString());

			}
			td.setTag("Lore", list);
			tag.setTag("display", td);
			itemstack.setTagCompound(tag);
		}
		return itemstack;
	}

}
