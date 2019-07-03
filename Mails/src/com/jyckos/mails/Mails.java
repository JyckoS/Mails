package com.jyckos.mails;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.jyckos.mails.events.SimpleListener;
import com.jyckos.mails.manager.InventoryManager;
import com.jyckos.mails.manager.MailManager;
import com.jyckos.mails.storage.ItemStorage;
import com.jyckos.mails.storage.LangStorage;
import com.jyckos.mails.storage.LayoutStorage;
import com.jyckos.mails.storage.MailStorage;
import com.jyckos.mails.storage.OptionStorage;
import com.jyckos.mails.utils.ActionBarAPI;
import com.jyckos.mails.utils.Utility;

import lombok.Getter;

public class Mails extends JavaPlugin {
	public static Mails getInstance() {
		return instance;
	}
	private static Mails instance;
	@Override
	public void onEnable() {
		instance = this;
		if (!checkUUIDAPI()) {
			return;
		}
		new Metrics(this);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.getCommand("mail").setExecutor(new MailCommand(this));
		this.getServer().getPluginManager().registerEvents(new SimpleListener(this), this);
		Utility.sendConsole("[Mails] Registered events & commands.");
		this.optionStorage = new OptionStorage(this);
		Utility.sendConsole("[Mails] Options loaded.");

		this.languageStorage = new LangStorage(this);
		Utility.sendConsole("[Mails] Language loaded.");

		this.itemStorage = new ItemStorage(this);
		Utility.sendConsole("[Mails] Items loaded.");

		this.inventoryManager = new InventoryManager(this);
		Utility.sendConsole("[Mails] Inventory loaded.");
		this.mailManager = new MailManager(this);
		this.layoutStorage = new LayoutStorage(this);
		Utility.sendConsole("[Mails] Layouts loaded.");
		this.mailStorage = new MailStorage(this);
		Utility.sendConsole("[Mails] Complete.");
	}
	public boolean checkUUIDAPI() {
		boolean enabled = Bukkit.getServer().getPluginManager().isPluginEnabled("UUIDAPI");
		if (!enabled) {
			Utility.sendConsole("[Mails] &bUUIDAPI &cnot found! Please download at: &ebit.ly/UUIDAPI");
			this.setEnabled(false);
			
		}
		return enabled;
	}
	public void reloadAll() {
		this.optionStorage.reload();
		this.inventoryManager.reload();
		this.layoutStorage.reload();
		this.languageStorage.reload();
	}
	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.mailStorage.unload(p.getUniqueId());
		}
	}
	private @Getter ItemStorage itemStorage;
	private @Getter OptionStorage optionStorage;
	private ActionBarAPI aba = ActionBarAPI.getInstance();
	private @Getter InventoryManager inventoryManager;
	private @Getter MailManager mailManager;
	private @Getter LayoutStorage layoutStorage;
	private @Getter MailStorage mailStorage;
	private @Getter LangStorage languageStorage;
}
