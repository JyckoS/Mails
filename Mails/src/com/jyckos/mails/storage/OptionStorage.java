package com.jyckos.mails.storage;

import java.util.HashMap;
import java.util.IllegalFormatException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.jyckos.mails.Mails;
import com.jyckos.mails.utils.Utility;

public class OptionStorage {
	private Mails m;
	private HashMap<StringData, String> stringdata = new HashMap<StringData, String>(); // Strings
	private HashMap<ValueData, Integer> valuedata = new HashMap<ValueData, Integer>(); // Integers
	public OptionStorage(Mails m) {
		this.m = m;
		this.reload();
	}
	public void reload() {
		m.reloadConfig();
		FileConfiguration config = m.getConfig();
		ConfigurationSection options = config.getConfigurationSection("options");
		{ConfigurationSection data = options.getConfigurationSection("data"); // Strings
		for (String str : data.getKeys(false)) {
			StringData dat = null;
			try {
				dat = StringData.valueOf(str.toUpperCase());
			} catch (IllegalArgumentException e) {
				Utility.sendConsole("[Mails] Unknown Data " + str + ", ignoring..");
				continue;
			}
			stringdata.put(dat, data.getString(str));
		}
		}
		{
			ConfigurationSection value = options.getConfigurationSection("value"); // Strings
			for (String str : value.getKeys(false)) {
				ValueData dat = null;
				try {
					dat = ValueData.valueOf(str.toUpperCase());
				} catch (IllegalArgumentException e) {
					Utility.sendConsole("[Mails] Unknown Value " + str + ", ignoring..");
					continue;
				}
				valuedata.put(dat, value.getInt(str));
			}
		}
	}
	public int getValue(ValueData data) {
		return this.valuedata.get(data);
	}
	public String getData(StringData data) {
		return this.stringdata.get(data);
	}
	public static enum ValueData {
		MAIL_REMINDER_DELAY,
		MAIL_SAVE_DELAY,
		MAIL_COOLDOWN;
	}
	public static enum StringData {
		DATE_FORMAT,
		NON_PLAYER_NAME,
		MAIL_ITEMNAME;
	}
}
