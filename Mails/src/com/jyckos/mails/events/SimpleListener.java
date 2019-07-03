package com.jyckos.mails.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.jyckos.mails.Mails;
import com.jyckos.mails.manager.MailHolder;
import com.jyckos.mails.objects.Emails;
import com.jyckos.mails.objects.Mail;
import com.jyckos.mails.storage.LangStorage.Message;
import com.jyckos.mails.storage.LangStorage.ShortMessage;
import com.jyckos.mails.utils.Utility;
import com.jyckos.mails.utils.XSound;

public class SimpleListener implements Listener {
	private Mails m;
	public SimpleListener(Mails m) {
		this.m = m;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		this.m.getMailStorage().loadMails(p.getUniqueId());
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p =e.getPlayer();
		this.m.getMailStorage().unload(p.getUniqueId());
	}
	@EventHandler
	public void onMove(InventoryDragEvent e) {
		Inventory inv = e.getInventory();
		if (!(inv.getHolder() instanceof MailHolder)) {
			return;
		}
		e.setCancelled(true);
	}
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (!(inv.getHolder() instanceof MailHolder)) {
			return;
		}
		if (e.getCurrentItem() == null) return;
		Player p = (Player) e.getWhoClicked();
		ItemStack itemez = inv.getItem(e.getSlot());
		if (itemez == null) return;
		MailHolder holder = (MailHolder) inv.getHolder();
		e.setCancelled(true);
		if (e.getSlot() < 0 || e.getSlot() > 44) {
			return;
		}
		switch (e.getSlot()) {
		default: // When clicking on mails
		{
			if (e.getSlot() < 0 || e.getSlot() > 35) 
			{
				return;
			}
			Emails em = m.getMailStorage().getEmails(p.getUniqueId());
			int index = holder.getPage() * 36 + e.getSlot();
			Mail m =em.getMails().get(index);
			switch (e.getClick()) {
			default:
			{
				return;
			}
			case LEFT:
			{
				// send to chat
				for (String str : this.m.getLanguageStorage().getMessage(Message.SHOW_MAIL)) {
					if (str.contains("%a")) str = str.replaceAll("%a", m.getSender());
					if (str.contains("%b")) str = str.replaceAll("%b", m.getDate());
					if (str.contains("%c")) {
						Utility.sendMsg(p, m.getMessages());
						continue;
					}
					Utility.sendMsg(p, str);
				}
				Utility.PlaySound(p, XSound.DOOR_CLOSE.bukkitSound(), 0.6F, 1.7F);
				Utility.PlaySound(p, XSound.DOOR_CLOSE.bukkitSound(), 0.6F, 1.3F);
				return;
			}
			case RIGHT:
			{
				// mark as read
				m.setRead(true);
				ItemStack item = this.m.getItemStorage().getMailItem(m);
				e.getInventory().setItem(e.getSlot(), item);
				Utility.PlaySound(p, XSound.BAT_LOOP.bukkitSound(), 0.6F, 1.7F);

				return;
			}
			case SHIFT_RIGHT:
			{
				em.remove(index);
				if (em.getMails().isEmpty()) {
					// remove from data
					this.m.getMailStorage().clearEmails(p.getUniqueId());
					p.closeInventory();
				}
				ArrayList<ItemStack> toadd = new ArrayList<ItemStack>();
				e.getInventory().setItem(e.getSlot(), null);

				for (int i = e.getSlot() + 1; i < 36; i++) {
					ItemStack item = e.getInventory().getItem(i);
					if (item == null) break;
					toadd.add(item);
					e.getInventory().setItem(i, null);
				}
				for (ItemStack item : toadd) {
					e.getInventory().addItem(item);
				}
				Utility.PlaySound(p, XSound.ANVIL_BREAK.bukkitSound(), 0.6F, 1.3F);

				// delete mail
				return;
			}
			}
		}
		case 42: // Clear all mail
		{
			this.m.getMailStorage().clearEmails(p.getUniqueId());
			p.closeInventory();
			Utility.PlaySound(p, XSound.ARROW_HIT.bukkitSound(), 0.5F, 2.0F);
			Utility.PlaySound(p, XSound.FUSE.bukkitSound(), 0.5F, 2.0F);

			Utility.sendMsg(p, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_CLEARED));
			return;
		}
		case 36: // Prev page if exists
		{
			if (!holder.isHasPrevious()) return;
			Inventory prev = holder.getPreviousInventory();
			p.openInventory(prev);
			Utility.PlaySound(p, XSound.NOTE_PIANO.bukkitSound(), 0.6F, 1F);
			return;
			//break;
		}
		case 37:
		case 38:
		case 39:
		case 40:
		case 41:
		case 43:
		{
			return;
		}
		case 44: // Next page if exists
		{
			if (!holder.isHasNext()) return;
			Inventory prev = holder.getNextInventory();
			MailHolder hold = (MailHolder) prev.getHolder();
			if (!hold.isOpened()) {
				hold.setOpened(true);
				HashMap<Integer, ItemStack> itemz = new HashMap<Integer, ItemStack>();
				int limit = 0;
				for (int i = 0; i < 36; i++) {
					ItemStack item = prev.getItem(i);
					if (item == null) break;
					prev.setItem(i, null);

					itemz.put(i, item);
					limit = i;
				}
				p.openInventory(prev);

				final int limitz = limit;
				new BukkitRunnable() {
					int target = 0;
					@Override
					public void run() {
						if (target > limitz) {
							this.cancel();
							return;
						}
						Utility.PlaySound(p, XSound.CHICKEN_EGG_POP.bukkitSound(), 0.45F, 1.3F);
						prev.setItem(target, itemz.get(target));
						target++;
					}
				}.runTaskTimerAsynchronously(m, 1L, 2L);
			}
			else {
				p.openInventory(prev);

			}
			Utility.PlaySound(p, XSound.NOTE_PIANO.bukkitSound(), 0.6F, 1.2F);
			return;
		}
		}
	}
}
