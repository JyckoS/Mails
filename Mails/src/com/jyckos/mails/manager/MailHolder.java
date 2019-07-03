package com.jyckos.mails.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.jyckos.mails.Mails;
import com.jyckos.mails.objects.Emails;
import com.jyckos.mails.objects.Mail;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MailHolder implements InventoryHolder {
	private @Getter int page = 0; // Starts from 0
	private ArrayList<Inventory> inventories = null;
	private @Getter @Setter boolean opened = false;
	private @Getter Emails emails;
	private @Getter @Setter boolean hasPrevious = false;
	private @Getter @Setter boolean hasNext = false;
	private @Getter @Setter Player owner;
	public MailHolder(Emails em, int page, ArrayList<Inventory> invs) {
		this.emails = em;
		this.page = page;
		this.inventories = invs;
	}
	public Inventory getPreviousInventory() {
		if (page - 1 < 0) return null;
		return inventories.get(page - 1);
	}
	public void addMailItem(Mail m) {
		int lastpage = inventories.size() - 1;
		Inventory inv = inventories.get(lastpage);
		int exist = 0;
		for (int i = 0; i < 35; i++) {
			if (inv.getItem(i) == null) break;
			exist++;
		}
		if ((exist + 1) > 35) { // Create new
			this.inventories = Mails.getInstance().getInventoryManager().getInventories(emails);
			if (page > inventories.size() - 1) {
				owner.openInventory(this.inventories.get(0));
				return;
			}
			else {
				owner.openInventory(this.inventories.get(page));
				return;
			}
			// Page 0 here = 1 in size

		}
		inv.addItem(Mails.getInstance().getInventoryManager().getItemFromMail(m));
	}
	public Inventory getNextInventory() {
		if (page + 1 > inventories.size() - 1) return null;
		return inventories.get(page + 1);
	}
	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
