package com.jyckos.mails.manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jyckos.mails.Mails;
import com.jyckos.mails.objects.Emails;
import com.jyckos.mails.objects.Mail;
import com.jyckos.mails.storage.ItemStorage.ItemData;
import com.jyckos.mails.utils.Utility;
import com.jyckos.mails.utils.XMaterial;

public class InventoryManager {
	private Mails m;
	public InventoryManager(Mails m) {
		this.m = m;
		this.reload();
	}
	public void reload() {
	}
	// 1 * 43
	// 0 1 2 3 4 5 6 7 9 43 44 45 46 47 48 49
	// Inventory size = 5 rows = 45 & 2 will be left empty so 43
	
	
	/*
	 * Rows
	 * 4 rows = 36 slots = 0 - 35
	 * 5 rows = 45 slots = 0 - 44
	 */
	
	/*
	 * Slots and triggers
	 * 0 until 35 = Mail Stuff
	 * 
	 * 36 = Previous Page
	 * 44 = Next Page
	 * 
	 */
	public ArrayList<Inventory> getInventories(Emails mail) {
		// Pages starts from 1 since its ALWAYS rounded up
		int pages = (int) Math.ceil(((double)mail.getMails().size()) / 36);
		//Utility.broadcast("Pages: " + pages + " Size: " + mail.getMails().size() + " Debug: " + (((double) mail.getMails().size()) / 36));
		ArrayList<Inventory> inventories = new ArrayList<Inventory>();
		for (int i = 0; i < pages; i++) { // 0 index means page 1
			MailHolder holder = new MailHolder(mail, i, inventories);
	
			Inventory inv = Bukkit.createInventory(holder, 45, Utility.TransColor("&1&lMail Box &0Page &l" + (i + 1) + "&0/&l" + pages));
			int beginindex = i * 36;
			int lastindex = beginindex + 36;
			for (int a = beginindex; a < lastindex; a++) {
				if (a > mail.getMails().size() - 1) {
					break;
				}
				Mail m = mail.getMails().get(a);
				inv.addItem(this.getItemFromMail(m));
			}
			for (int a = 36; a< 45; a++) {
				inv.setItem(a, this.m.getItemStorage().getItem(ItemData.FILLER));
			}
			if (i > 0) { inv.setItem(36, this.m.getItemStorage().getItem(ItemData.PREV_PAGE));
			holder.setHasPrevious(true);
			}
			
			if (pages > 1 && i < pages - 1) {
				holder.setHasNext(true);
				inv.setItem(44, this.m.getItemStorage().getItem(ItemData.NEXT_PAGE));
			}
			inv.setItem(42, this.m.getItemStorage().getItem(ItemData.CLEAR)); 
			inv.setItem(40, this.m.getItemStorage().getItem(ItemData.INSTRUCTIONS));
			inventories.add(inv);

		}
		return inventories;
	}
	public ItemStack getItemFromMail(Mail m) {
		return this.m.getItemStorage().getMailItem(m);
	}
}
