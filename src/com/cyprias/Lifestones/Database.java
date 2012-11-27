package com.cyprias.Lifestones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;

import com.cyprias.Lifestones.Databases.SQLite;

public class Database {
	public Lifestones plugin;
	public SQLite sqlite;
	public Database(Lifestones plugin) {
		this.plugin = plugin;
		plugin.info("path: " + plugin.getDataFolder().getPath());
		
		sqlite = new SQLite(this, plugin.getDataFolder());
		
		
	}
	
	public void saveLifestone(Block block){
		
		if (Config.mysqlEnabled == true){
			
		}else{
			sqlite.saveLifestone(block);
		}

	}
	
	public void saveLifestone(final Block block, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.saveLifestone(block);
				}
			});
			
		}else{
			saveLifestone(block);
		}
	}
	
	public void loadLifestones() {
		if (Config.mysqlEnabled == true){
			
		}else{
			sqlite.loadLifestones();
		}
	}
	
	public void loadLifestones(Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.loadLifestones();
				}
			});
			
		}else{
			loadLifestones();
		}
	}
}
