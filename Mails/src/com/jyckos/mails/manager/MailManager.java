package com.jyckos.mails.manager;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jyckos.mails.Mails;
import com.jyckos.mails.objects.Emails;
import com.jyckos.mails.objects.Mail;
import com.jyckos.mails.storage.LangStorage.ShortMessage;
import com.jyckos.mails.storage.OptionStorage.ValueData;
import com.jyckos.mails.utils.Utility;
import com.jyckos.mails.utils.XSound;

public class MailManager {
	private Mails m;
	public MailManager(Mails m) {
		this.m = m;
		new BukkitRunnable() {
			int current_save_delay = 0;
			int current_reminder_delay = 0;
			@Override
			public void run() {
				current_save_delay++;
				current_reminder_delay++;
				if (this.current_save_delay > m.getOptionStorage().getValue(ValueData.MAIL_SAVE_DELAY)) {
					// Save
					this.current_save_delay = 0;
					save();
				}
				if (this.current_reminder_delay > m.getOptionStorage().getValue(ValueData.MAIL_REMINDER_DELAY)) {
					// Remind
					this.current_reminder_delay = 0;
					remind();
				}
			}
			private void save() {
				ArrayList<Player> p = new ArrayList<Player>(Bukkit.getOnlinePlayers());
				new BukkitRunnable() {
					@Override
					public void run() {
						this.remove();
					}
					public void remove() {

						if (p.isEmpty()) {
							this.cancel();
							return;
						}
						Player pl = p.get(0);
						p.remove(0);
						if (pl == null || !pl.isOnline()) {
						remove();	
						return;
						}
						m.getMailStorage().save(pl.getUniqueId());
					}
				}.runTaskTimerAsynchronously(m, 1L, 1L);
			}
			private void remind() {
				ArrayList<Player> p = new ArrayList<Player>(Bukkit.getOnlinePlayers());
				new BukkitRunnable() {
					@Override
					public void run() {
						this.remove();
					}
					public void remove() {

						if (p.isEmpty()) {
							this.cancel();
							return;
						}
						Player pl = p.get(0);
						p.remove(0);
						if (pl == null || !pl.isOnline()) {
						remove();	
						return;
						}
						if (!m.getMailStorage().hasMail(pl.getUniqueId())) {
							remove();
							return;
						}
						Emails em = m.getMailStorage().getEmails(pl.getUniqueId());
						int unreads = em.getUnreads();
						if (unreads <= 0) {
							remove();
							return;
						}
						String str = m.getLanguageStorage().getMessage(ShortMessage.UNREAD_MAILS);
						str = str.replaceAll("%a", Integer.toString(unreads));
						Utility.sendMsg(pl, str);
						Utility.PlaySound(pl, XSound.ORB_PICKUP.bukkitSound(), 0.4F, 1.6F);
					}
				}.runTaskTimerAsynchronously(m, 1L, 1L);
			}
			}.runTaskTimerAsynchronously(this.m, 20L, 20L);
	}
	public void sendMail(Mail m, UUID uuid) {
		Player p = Bukkit.getPlayer(uuid);
		if (p != null && p.isOnline()) {
			this.m.getMailStorage().sendMail(uuid, m);
			Utility.sendActionBar(p, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_RECEIVED).replaceAll("%a", m.getSender()));
			return;	
		}
		this.m.getMailStorage().sendOfflineMail(uuid, m);
		 // Not online
		
	}
}
