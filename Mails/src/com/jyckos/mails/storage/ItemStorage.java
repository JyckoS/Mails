package com.jyckos.mails.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jyckos.mails.Mails;
import com.jyckos.mails.nbt.NBTItem;
import com.jyckos.mails.objects.Mail;
import com.jyckos.mails.utils.Utility;
import com.jyckos.mails.utils.XMaterial;

public class ItemStorage {
	private Mails m;
	public ItemStorage(Mails m) {
		this.m = m;
		this.defaultmaterial.put(ItemData.CLEAR, XMaterial.REDSTONE_BLOCK.parseMaterial());
		this.defaultmaterial.put(ItemData.MAIL, XMaterial.PAPER.parseMaterial());
		this.defaultmaterial.put(ItemData.READ_MAIL, XMaterial.PAPER.parseMaterial());
		this.defaultmaterial.put(ItemData.INSTRUCTIONS, XMaterial.GLOWSTONE.parseMaterial());
		this.defaultmaterial.put(ItemData.NEXT_PAGE, XMaterial.GREEN_STAINED_GLASS_PANE.parseMaterial());
		this.defaultmaterial.put(ItemData.PREV_PAGE, XMaterial.RED_STAINED_GLASS_PANE.parseMaterial());
		this.defaultmaterial.put(ItemData.FILLER, XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial());
		this.reload();


	}
	private HashMap<ItemData, Material> defaultmaterial = new HashMap<ItemData, Material>();
/*
 * Unread = Enchanted paper
 * Read = Common Paper
 * 
 */
	public void reload() {
		m.reloadConfig();
		FileConfiguration config = m.getConfig();
		ConfigurationSection items = config.getConfigurationSection("items");
		for (String str : items.getKeys(false)) {
			ItemData dat = null;
			try {
				dat = ItemData.valueOf(str.toUpperCase());
			} catch (IllegalArgumentException e) {
				Utility.sendConsole("[Mails] Item Key " + str + " is unknown, ignoring..");
				continue;
			}
			ConfigurationSection item = items.getConfigurationSection(str);
			String name = Utility.TransColor(item.getString("name"));
			List<String> lores = Utility.TransColor(item.getStringList("lore"));
			Material mat = null;
			XMaterial xmat = null;
			String material = item.getString("material");
			Integer durability = item.getInt("durability");
			byte durabil = durability.byteValue();
			boolean integer = true;
			int material_id = 0;
			try {
				material_id = Integer.parseInt(material);
			} catch (NumberFormatException e) {
				integer = false;
			}
			if (!integer) {
			xmat = XMaterial.matchXMaterial(material, durabil);
			}else {
				xmat = XMaterial.matchXMaterial(material_id, durabil);
			}
			if (xmat == null) {
				Utility.sendConsole("[Mails] Item " + dat.toString() + " has no valid Material, using default instead.");
				mat = this.defaultmaterial.get(dat);
			}
			else {
				mat = xmat.parseMaterial();
			}
			ItemStack itemz = new ItemStack(mat);
			itemz.setDurability(durabil);
			ItemMeta meta = itemz.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(lores);
			itemz.setItemMeta(meta);
			this.items.put(dat, itemz);
		}
	}
	public ItemStack getItem(ItemData dat) {
		return items.get(dat).clone();
	}
	private HashMap<ItemData, ItemStack> items = new HashMap<ItemData, ItemStack>();
	public ItemStack getMailItem(Mail m) {
		ItemStack item = null;
		if (m.isRead()) {
			item = items.get(ItemData.READ_MAIL).clone();
		}
		else {
			item = items.get(ItemData.MAIL).clone();
			item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName();
		name = name.replace("%a", m.getSender());
		name = name.replace("%b", m.getDate());
		meta.setDisplayName(Utility.TransColor(name));
		ArrayList<String> msgs = new ArrayList<String>();
		for (String str : m.getMessages()) {
			msgs.add("&f" + str);
		}
		meta.setLore(Utility.TransColor(msgs));
		item.setItemMeta(meta);
		NBTItem nbt = new NBTItem(item);
		nbt.setString("c2e", m.getUniqueID().toString());
		return nbt.getItem();
	}
	public static enum ItemData {
		CLEAR,
		MAIL,
		READ_MAIL,
		INSTRUCTIONS,
		NEXT_PAGE,
		PREV_PAGE,
		FILLER;
	}
}
