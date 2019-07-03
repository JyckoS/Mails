package com.jyckos.mails.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.jyckos.mails.Mails;
import com.jyckos.mails.objects.Layout;
import com.jyckos.mails.utils.Utility;

import lombok.Getter;

public class LayoutStorage {
	private Mails m;
	private @Getter HashMap<String, Layout> layouts = new HashMap<String, Layout>();
	public LayoutStorage(Mails m) {
		this.m = m;
		this.reload();
	}
	public void reload() {
		m.reloadConfig();
		this.layouts.clear();
		FileConfiguration config = m.getConfig();
		ConfigurationSection layout = config.getConfigurationSection("layouts");
		for (String key : layout.getKeys(false)) {
			int fields = 0;
			List<String> msg = layout.getStringList(key);
			ArrayList<Integer> fieldvalue = new ArrayList<Integer>();
			for (String str : msg) {
				for (int i = 1; i < 20; i++) {
					if (fieldvalue.contains(i)) {
						continue;
					}
					if (str.contains("%f" + i)) {
						fieldvalue.add(i);
					}
				}
			}
			fields = fieldvalue.size();
			this.layouts.put(key, new Layout(msg, fields));
		}
	}
	public Layout getLayout(String key) {
		return layouts.get(key);
	}
}
