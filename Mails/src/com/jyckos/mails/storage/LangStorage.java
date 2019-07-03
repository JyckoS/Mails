package com.jyckos.mails.storage;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.jyckos.mails.Mails;
import com.jyckos.mails.utils.Utility;

import lombok.Getter;

public class LangStorage {
	private Mails m;
	private HashMap<Message, List<String>> longmessages = new HashMap<Message, List<String>>();
	private HashMap<ShortMessage, String> shortmessages = new HashMap<ShortMessage, String>();
	public LangStorage(Mails m) {
		this.m = m;
		this.reload();
	}
	public enum Message {
		HELP_MESSAGE,
		ADMIN_HELP_MESSAGE,
		SHOW_MAIL,
		INSTRUCTIONS;
	}
	public enum ShortMessage {
		MAIL_SENT,
		MAIL_COOLDOWN,
		NO_PERMISSION,
		LAYOUT_UNEXIST,
		MAIL_RECEIVED,
		NO_MAILS,
		NOT_ENOUGH_FIELD,
		MAIL_CLEARED,
		NOT_ENOUGH_ARGS,
		TOO_SHORT,
		PLAYER_NEVER_EXISTS,
		UNREAD_MAILS;
	}
	public void reload() {
		m.reloadConfig();
		FileConfiguration config = m.getConfig();
		ConfigurationSection msg = config.getConfigurationSection("messages");
		for (String str : msg.getKeys(false)) {
			Message msga = null;
			try {
				msga = Message.valueOf(str.toUpperCase());
			} catch (IllegalArgumentException e) {
				Utility.sendConsole("[Mails] Unrecognized message " + str + ", ignoring..");
				continue;
			}
			this.longmessages.put(msga, msg.getStringList(str));
		}
		ConfigurationSection shortmsg = config.getConfigurationSection("short_messages");
		for (String str : shortmsg.getKeys(false)) {
			ShortMessage msga = null;
			try {
				msga = ShortMessage.valueOf(str.toUpperCase());
			} catch (IllegalArgumentException e) {
				Utility.sendConsole("[Mails] Unrecognized short message " + str + ", ignoring..");
				continue;
			}
			this.shortmessages.put(msga, shortmsg.getString(str));
		}

		
	}
	public List<String> getMessage(Message m) {
		return this.longmessages.get(m);
	}
	public String getMessage(ShortMessage m) {
		return this.shortmessages.get(m);
	}
}
