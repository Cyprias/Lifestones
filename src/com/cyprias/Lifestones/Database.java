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
		sqlite = new SQLite(this, plugin.getDataFolder());
	}
	
	public void saveLifestone(String world, int X, int Y, int Z){
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.saveLifestone(world, X, Y, Z);
		//}

	}
	
	public void loadDatabases(){
		
		loadLifestones(Config.preferAsyncDBCalls);
		loadAttunments(Config.preferAsyncDBCalls);
		
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
	
	public void saveAttunment(String player, String world, double x, double y, double z,  float yaw, float pitch){
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.saveAttunment(player, world, x, y, z,yaw,pitch);
		//}

	}
	
	public void saveAttunment(final String player, final String world, final double x, final double y, final double z, final float yaw, final float pitch, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.saveAttunment(player, world, x, y, z,yaw,pitch);
				}
			});
			
		}else{
			sqlite.saveAttunment(player, world, x, y, z,yaw,pitch);
		}
	}
	
	public void loadAttunements() {
		//if (Config.mysqlEnabled == true){
			
		//}else{
			sqlite.loadAttunements();
		//}
	}
	
	public void loadAttunments(Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sqlite.loadAttunements();
				}
			});
			
		}else{
			loadAttunements();
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
