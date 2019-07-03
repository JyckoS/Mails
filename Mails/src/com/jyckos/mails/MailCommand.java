package com.jyckos.mails;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.jyckos.mails.manager.MailHolder;
import com.jyckos.mails.objects.Layout;
import com.jyckos.mails.objects.Mail;
import com.jyckos.mails.storage.LangStorage.Message;
import com.jyckos.mails.storage.LangStorage.ShortMessage;
import com.jyckos.mails.storage.OptionStorage.StringData;
import com.jyckos.mails.storage.OptionStorage.ValueData;
import com.jyckos.mails.utils.Utility;
import com.jyckos.mails.utils.XSound;
import com.jyckos.uuidapi.UUIDAPI;

public class MailCommand implements CommandExecutor {
	private Mails m;
	private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
	public MailCommand(Mails mails) {
		// TODO Auto-generated constructor stub
		this.m = mails;
	}
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (!arg0.hasPermission("mails.use")) return true;
		redo(arg0, arg3);
		return true;
	}
	private String getCurrentDate() {
		SimpleDateFormat formatter= new SimpleDateFormat(m.getOptionStorage().getData(StringData.DATE_FORMAT));  
		Date date = new Date(System.currentTimeMillis());  
		return formatter.format(date);
	//	System.out.println(formatter.format(date));  
		// System.out.println(dateFormat.format(cal)); 
	}
	private void redo(CommandSender snd, String[] args) {
		boolean isPlayer = snd instanceof Player;
		Player sender = null;
		if (isPlayer) {
			sender = (Player) snd;
		}
		if (args.length == 0) {
			Utility.sendMsg(snd, m.getLanguageStorage().getMessage(Message.HELP_MESSAGE));
			if (snd.hasPermission("mails.admin")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(Message.ADMIN_HELP_MESSAGE));

			}
			return;
		}
		switch (args[0].toLowerCase()) {
		default:
			Utility.sendMsg(snd, m.getLanguageStorage().getMessage(Message.HELP_MESSAGE));
			if (snd.hasPermission("mails.admin")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(Message.ADMIN_HELP_MESSAGE));

			}
			return;
		case "view":
		case "viewlayout":
		{
			if (!snd.hasPermission("mails.viewlayout")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			if (args.length < 2) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NOT_ENOUGH_ARGS).replaceAll("%a", "&7/mail viewlayout <Name>"));
				return;
			}
			Layout lay = this.m.getLayoutStorage().getLayout(args[1]);
			if (lay == null) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.LAYOUT_UNEXIST));
				return;			
			}
			for (String str : lay.getDefaultMsg()) {
				Utility.sendMsg(snd, str);
			}
			return;
		}
		case "time":
		{
			Utility.sendMsg(snd, "&7It's currently &f" + this.getCurrentDate());
			return;
		}
		case "layouts":
		{
			if (!snd.hasPermission("mails.layouts")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			Utility.sendMsg(snd, "&b&lLayouts");
			String laz = "";
			for (String str : this.m.getLayoutStorage().getLayouts().keySet()) {
				laz = laz + str + "&a, &r";
			}
			laz = laz.substring(0, laz.length() - 6);
			Utility.sendMsg(snd, laz);
			return;
		}
		case "compose":
		case "composelayout": // Fields starts from args[3]
		{
			if (!snd.hasPermission("mails.compose")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			if (!snd.hasPermission("mails.cooldown.bypass")) {
				if (isPlayer) {
				if (this.cooldown.containsKey(sender.getUniqueId())) {
					String msg = this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_COOLDOWN);
					int delay = this.m.getOptionStorage().getValue(ValueData.MAIL_COOLDOWN);
					long pcd = this.cooldown.get(sender.getUniqueId());
					long towait = delay * 1000; // Multiplied to miliseconds
					long seconds = (System.currentTimeMillis() - pcd);
					msg = msg.replaceAll("%a", Integer.toString((int) (towait - seconds)));
					Utility.sendMsg(sender, msg);
					return;
				}
				}
			}
			if (args.length < 3) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NOT_ENOUGH_ARGS).replaceAll("%a", "&7/mail composelayout <Target> <Layout> <Field 1> <Field 2>...."));
				return;
			}
			Layout lay = this.m.getLayoutStorage().getLayout(args[2]);
			if (lay == null) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.LAYOUT_UNEXIST));
				return;			
				}
			if (args.length < (3 + lay.getFields())) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NOT_ENOUGH_FIELD));
				return;			
			}
			if (!snd.hasPermission("mails.cooldown.bypass")) {
				if (isPlayer) {
					final Player own = sender;
				this.cooldown.put(sender.getUniqueId(), System.currentTimeMillis());
				new BukkitRunnable() {
					@Override
					public void run() {
						cooldown.remove(own.getUniqueId());
					}
				}.runTaskLaterAsynchronously(this.m, this.m.getOptionStorage().getValue(ValueData.MAIL_COOLDOWN) * 20);
				}
				
			}
			String[] fields = new String[] {"", "", "", "", "", "", "", "", "", "", "", "", ""};
			for (int i = 3; i < args.length; i++) {
				fields[i - 3] = args[i];
			}
			Player p = Bukkit.getPlayer(args[1]);
		/*If Online*/	if (p != null && p.isOnline()) {

				String senders = "";
				if (isPlayer) {
					senders = sender.getName();
					Utility.sendActionBar(sender, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_SENT).replaceAll("%a", p.getName()));

				}
				else {
					Utility.sendMsg(snd, "&cMail composed to &f" + args[1]);

					senders = Utility.TransColor(m.getOptionStorage().getData(StringData.NON_PLAYER_NAME));
				}
				
				List<String> msgz = lay.translate(fields);
				Mail m = Mail.getMail(senders, msgz, msgz, this.getCurrentDate());
				this.m.getMailManager().sendMail(m, p.getUniqueId());
				if (p.getOpenInventory().getTopInventory().getHolder() instanceof MailHolder) {
					MailHolder holder = (MailHolder) p.getOpenInventory().getTopInventory().getHolder();
					holder.addMailItem(m);
				}
				return;
			}
		/*
		 * if offline
		 */
			UUID uuid = UUIDAPI.getUUID(args[1]);
			if (uuid == null) {
				Utility.sendMsg(snd, this.m.getLanguageStorage().getMessage(ShortMessage.PLAYER_NEVER_EXISTS));
				return;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
			String senders = "";
			if (isPlayer) {
				senders = sender.getName();
				Utility.sendActionBar(sender, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_SENT).replaceAll("%a", target.getName()));

			}
			else {
				Utility.sendMsg(snd, "&cMail composed to &f" + args[1]);
				senders = Utility.TransColor(m.getOptionStorage().getData(StringData.NON_PLAYER_NAME));
			}
			List<String> msgz = lay.translate(fields);
			Mail m = Mail.getMail(senders, msgz, msgz, this.getCurrentDate());
			this.m.getMailManager().sendMail(m, uuid);
			return;
		
		}
		case "reload":
		{
			if (!snd.hasPermission("mails.admin")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			this.m.reloadAll();
			Utility.sendMsg(snd, "&cMails: &7Reloaded all!");
			return;
		}
		case "send":
		{
			if (!snd.hasPermission("mails.send")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			if (!snd.hasPermission("mails.cooldown.bypass")) {
				if (isPlayer) {
				if (this.cooldown.containsKey(sender.getUniqueId())) {
					String msg = this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_COOLDOWN);
					int delay = this.m.getOptionStorage().getValue(ValueData.MAIL_COOLDOWN);
					long pcd = this.cooldown.get(sender.getUniqueId());
					long towait = delay * 1000; // Multiplied to miliseconds
					long seconds = (System.currentTimeMillis() - pcd);
					msg = msg.replaceAll("%a", Integer.toString((int) (towait - seconds)));
					Utility.sendMsg(snd, msg);
					return;
				}
				}
			}
			if (args.length < 3) {
			Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NOT_ENOUGH_ARGS).replaceAll("%a", "&7/mail send <Player> <Messages>"));
			return;
			}
			if (args.length < 4) {
				Utility.sendMsg(snd, this.m.getLanguageStorage().getMessage(ShortMessage.TOO_SHORT));
				return;
			}
			if (!snd.hasPermission("mails.cooldown.bypass")) {
				if (isPlayer) {
					final Player own = sender;
				this.cooldown.put(sender.getUniqueId(), System.currentTimeMillis());
				new BukkitRunnable() {
					@Override
					public void run() {
						cooldown.remove(own.getUniqueId());
					}
				}.runTaskLaterAsynchronously(this.m, this.m.getOptionStorage().getValue(ValueData.MAIL_COOLDOWN) * 20);
				}
				
			}
			
			Player p = Bukkit.getPlayer(args[1]);
		/*If Online*/	if (p != null && p.isOnline()) {

				String senders = "";
				if (isPlayer) {
					senders = sender.getName();
					Utility.sendActionBar(sender, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_SENT).replaceAll("%a", p.getName()));

				}
				else {
					Utility.sendMsg(snd, "&cMail sent to &f" + args[1]);

					senders = Utility.TransColor(m.getOptionStorage().getData(StringData.NON_PLAYER_NAME));
				}
				ArrayList<String> msgz = new ArrayList<String>();
				ArrayList<StringBuilder> buildz = new ArrayList<StringBuilder>();
				int max = 0;
				StringBuilder build = new StringBuilder();
				for (int i = 2; i < args.length; i++) {
					build.append(args[i] + " ");
					max++;
					if (i == args.length - 1) {
						buildz.add(build);
						break;
					}
					if (max > 5) {
						max = 0;
						buildz.add(build);
						build = new StringBuilder();
					}
				}
				for (StringBuilder eb : buildz) {
					msgz.add(eb.toString());
				}
				Mail m = Mail.getMail(senders, msgz, msgz, this.getCurrentDate());
				this.m.getMailManager().sendMail(m, p.getUniqueId());
				if (p.getOpenInventory().getTopInventory().getHolder() instanceof MailHolder) {
					MailHolder holder = (MailHolder) p.getOpenInventory().getTopInventory().getHolder();
					holder.addMailItem(m);
				}
				return;
			}
		/*
		 * if offline
		 */
		
		UUID uuid = UUIDAPI.getUUID(args[1]);
		if (uuid == null) {
			Utility.sendMsg(snd, this.m.getLanguageStorage().getMessage(ShortMessage.PLAYER_NEVER_EXISTS));
			return;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
		String senders = "";
		if (isPlayer) {
			senders = sender.getName();
			Utility.sendActionBar(sender, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_SENT).replaceAll("%a", target.getName()));

		}
		else {
			Utility.sendMsg(snd, "&cMail sent to &f" + args[1]);

			senders = Utility.TransColor(m.getOptionStorage().getData(StringData.NON_PLAYER_NAME));
		}
		ArrayList<String> msgz = new ArrayList<String>();
		ArrayList<StringBuilder> buildz = new ArrayList<StringBuilder>();
		int max = 0;
		StringBuilder build = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			build.append(args[i] + " ");
			max++;
			if (i == args.length - 1) {
				buildz.add(build);
				break;
			}
			if (max > 5) {
				max = 0;
				buildz.add(build);
				build = new StringBuilder();
			}
		}
		for (StringBuilder eb : buildz) {
			msgz.add(eb.toString());
		}
		Mail m = Mail.getMail(senders, msgz, msgz, this.getCurrentDate());
		this.m.getMailManager().sendMail(m, uuid);
		return;
		}
		case "read":
		case "list":
		{
			if (!isPlayer) return;
			if (!snd.hasPermission("mails.read")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			if (!this.m.getMailStorage().hasMail(sender.getUniqueId())) {
				Utility.sendMsg(sender, this.m.getLanguageStorage().getMessage(ShortMessage.NO_MAILS));
				return;
			}
			final UUID uuid = sender.getUniqueId();
			final Player p = sender;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (p == null) return;
					Inventory inv = Mails.getInstance().getInventoryManager().getInventories(Mails.getInstance().getMailStorage().getEmails(uuid)).get(0);
					HashMap<Integer, ItemStack> itemz = new HashMap<Integer, ItemStack>();
					int limit = 0;
					for (int i = 0; i < 36; i++) {
						ItemStack item = inv.getItem(i);
						if (item == null) break;
						inv.setItem(i, null);

						itemz.put(i, item);
						limit = i;
					}
					p.openInventory(inv);
					Utility.PlaySound(p, XSound.NOTE_PLING.bukkitSound(), 0.6F, 1.3F);
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
							inv.setItem(target, itemz.get(target));
							target++;
						}
					}.runTaskTimerAsynchronously(m, 1L, 2L);
				}
			}.runTaskAsynchronously(this.m);
			break;
		}
		case "clear":
		{
			if (!isPlayer) return;
			if (!snd.hasPermission("mails.clear")) {
				Utility.sendMsg(snd, m.getLanguageStorage().getMessage(ShortMessage.NO_PERMISSION));
				return;
			}
			sender.closeInventory();
			this.m.getMailStorage().clearEmails(sender.getUniqueId());
			Utility.sendMsg(sender, this.m.getLanguageStorage().getMessage(ShortMessage.MAIL_CLEARED));
			break;
		}
	
		}
	}

}
