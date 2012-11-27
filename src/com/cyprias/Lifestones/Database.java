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
	
	public void saveLifestone(String world, int X, int Y, int Z){
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.saveLifestone(world, X, Y, Z);
		//}

	}
	
	public void saveLifestone(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.saveLifestone(world, X, Y, Z);
				}
			});
			
		}else{
			sqlite.saveLifestone(world, X, Y, Z);
		}
	}
	
	public void removeLifestone(String world, int X, int Y, int Z){
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.removeLifestone(world, X, Y, Z);
		//}

	}
	
	public void removeLifestone(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.removeLifestone(world, X, Y, Z);
				}
			});
			
		}else{
			sqlite.removeLifestone(world, X, Y, Z);
		}
	}
	
	public void saveAttunment(String world, int X, int Y, int Z){
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.saveAttunment(world, X, Y, Z);
		//}

	}
	
	public void saveAttunment(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.saveAttunment(world, X, Y, Z);
				}
			});
			
		}else{
			sqlite.saveAttunment(world, X, Y, Z);
		}
	}
	
	
	
	public void loadLifestones() {
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.loadLifestones();
		//}
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
