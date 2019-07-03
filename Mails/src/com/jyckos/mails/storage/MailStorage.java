package com.jyckos.mails.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jyckos.mails.objects.Emails;
import com.jyckos.mails.objects.Mail;
import com.jyckos.mails.utils.Utility;

public class MailStorage {
	private HashMap<UUID, Emails> mails = new HashMap<UUID, Emails>();
	private com.jyckos.mails.Mails m;
	public MailStorage(com.jyckos.mails.Mails m) {
		this.m = m;
		File f = new File(m.getDataFolder(), "mails");
		if (!f.exists()) {
			f.mkdir();
		}
		this.loadAll();
	}
	public void loadAll() {
		ArrayList<Player> p = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		new BukkitRunnable() {
			@Override
			public void run() {
				this.remove();
			}
			private void remove() {
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
				loadMails(pl.getUniqueId());
				
				
			}
		}.runTaskTimerAsynchronously(m, 1L, 1L);
	}
	public void save(UUID uuid) { // Save = Save & nothing is removed
		Emails emails = null;
		if (mails.containsKey(uuid)) {
			emails = this.mails.get(uuid);
		}
		else {
			return;
		}
		StringBuilder build = new StringBuilder();
		build.append("mails");
		build.append(File.separator);
		build.append(uuid.toString());
		build.append(".yml");
		String path = build.toString();
		File f = new File(m.getDataFolder(), path);
		if (f.exists()) {
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		f = new File(m.getDataFolder(), path);
		YamlConfiguration yml = new YamlConfiguration();
		ConfigurationSection mail = yml.createSection("mails");
		ArrayList<Mail> ms = new ArrayList<Mail>(emails.getMails());
		Collections.reverse(ms);
		for (Mail ma : ms) {
			ConfigurationSection sect = mail.createSection(ma.getUniqueID().toString());
			sect.set("sender", ma.getSender());
			sect.set("date", ma.getDate());
			sect.set("read", ma.isRead());
			sect.set("message", ma.getOriginalmessage());
		}
		try {
			yml.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void unload(UUID uuid) { // Unload = save & remove
		Emails emails = null;
		if (mails.containsKey(uuid)) {
			emails = this.mails.get(uuid);
			mails.remove(uuid);
		}
		else {
			return;
		}
		StringBuilder build = new StringBuilder();
		build.append("mails");
		build.append(File.separator);
		build.append(uuid.toString());
		build.append(".yml");
		String path = build.toString();
		File f = new File(m.getDataFolder(), path);
		if (f.exists()) {
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		f = new File(m.getDataFolder(), path);
		YamlConfiguration yml = new YamlConfiguration();
		ConfigurationSection mail = yml.createSection("mails");
		ArrayList<Mail> ms = new ArrayList<Mail>(emails.getMails());
		Collections.reverse(ms);
		for (Mail ma : ms) {
			ConfigurationSection sect = mail.createSection(ma.getUniqueID().toString());
			sect.set("sender", ma.getSender());
			sect.set("date", ma.getDate());
			sect.set("read", ma.isRead());
			sect.set("message", ma.getOriginalmessage());
		}
		try {
			yml.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean hasMail(UUID uuid) {
		return mails.containsKey(uuid);
	}
	public Emails getEmails(UUID uuid) {
		return this.mails.get(uuid);
	}
	public void clearEmails(UUID uuid) {
		mails.remove(uuid);
	}
	public void sendOfflineMail(UUID uuid, Mail email) {
		new BukkitRunnable() {
			@Override
			public void run() {
				StringBuilder build = new StringBuilder();
				build.append("mails");
				build.append(File.separator);
				build.append(uuid.toString());
				build.append(".yml");
				String path = build.toString();
				File f = new File(m.getDataFolder(), path);
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					f = new File(m.getDataFolder(), path);
				}
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
				ConfigurationSection mail = yml.getConfigurationSection("mails");
				ConfigurationSection mymail = mail.createSection(email.getUniqueID().toString());
				mymail.set("sender", email.getSender());
				mymail.set("date", email.getDate());
				mymail.set("read", email.isRead());
				mymail.set("message", email.getOriginalmessage());
				try {
					yml.save(f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(this.m);
		
	}
	public void sendMail(UUID uuid, Mail m) {
		if (mails.containsKey(uuid)) {
		mails.get(uuid).add(m);
		}
		else {
			Emails ma = new Emails();
			ma.add(m);
			mails.put(uuid, ma);
		}
	}
	/*
	 * mails:
	 *   rr2421-24120:
	 *      read: false
	 *      sender: "sender"
	 *      date: "date"
	 *      message:
	 *      - "messages"
	 * 
	 * 
	 * 
	 */
	public void loadMails(UUID uuid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				StringBuilder build = new StringBuilder();
				build.append("mails");
				build.append(File.separator);
				build.append(uuid.toString());
				build.append(".yml");
				String path = build.toString();
				File f = new File(m.getDataFolder(), path);
				if (!f.exists()) return;
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
			
				ConfigurationSection mail = yml.getConfigurationSection("mails");
				Emails em = new Emails();
				ArrayList<String> keys = new ArrayList<String>(mail.getKeys(false));
				Collections.reverse(keys);
				for (String str : keys) {
					ConfigurationSection mai = mail.getConfigurationSection(str);
					String sender = mai.getString("sender");
					String date = mai.getString("date");
					List<String> messages =mai.getStringList("message");
					Mail mae = Mail.getMail(sender, Utility.TransColor(messages) /*Colored*/, messages /*Un colored*/, date);
					boolean read = mai.getBoolean("read");
					mae.setRead(read);
					
					em.add(mae);
				}
				Collections.reverse(em.getMails());
				mails.put(uuid, em);
			}
		}.runTaskAsynchronously(this.m);
	}
}
