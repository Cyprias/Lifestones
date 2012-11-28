package com.cyprias.Lifestones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

import com.cyprias.Lifestones.Databases.MySQL;
import com.cyprias.Lifestones.Databases.SQLite;

public class Database {
	public Lifestones plugin;
	public SQLite sqlite;
	public MySQL mysql;
	public Database(Lifestones plugin) {
		this.plugin = plugin;
		this.sqlite = new SQLite(this, plugin.getDataFolder());
		this.mysql = new MySQL(this, plugin.getDataFolder());
	}
	

	public void createTables(){
		if (Config.mysqlEnabled == true){
			mysql.createTables();
		}else{
			sqlite.createTables();
		}
	}
	
	public void createTables(Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					createTables();
				}
			});
			
		}else{
			createTables();
		}
	}
	
	public void loadDatabases(){
		loadLifestones();
		loadAttunments();
	}
		
	public void loadDatabases(Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					loadDatabases();
				}
			});
			
		}else{
			loadDatabases();
		}
	}
	
	public void saveLifestone(String world, int X, int Y, int Z){
		if (Config.mysqlEnabled == true){
			mysql.saveLifestone(world, X, Y, Z);
		}else{
			sqlite.saveLifestone(world, X, Y, Z);
		}

	}
	public void saveLifestone(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					saveLifestone(world, X, Y, Z);
				}
			});
			
		}else{
			saveLifestone(world, X, Y, Z);
		}
	}
	
	public void removeLifestone(String world, int X, int Y, int Z){
		if (Config.mysqlEnabled == true){
			mysql.removeLifestone(world, X, Y, Z);	
		}else{
			sqlite.removeLifestone(world, X, Y, Z);
		}

	}
	
	public void removeLifestone(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					removeLifestone(world, X, Y, Z);
				}
			});
			
		}else{
			removeLifestone(world, X, Y, Z);
		}
	}
	
	public void saveAttunment(String player, String world, double x, double y, double z,  float yaw, float pitch){
		if (Config.mysqlEnabled == true){
			mysql.saveAttunment(player, world, x, y, z,yaw,pitch);
		}else{
			sqlite.saveAttunment(player, world, x, y, z,yaw,pitch);
		}

	}
	
	public void saveAttunment(final String player, final String world, final double x, final double y, final double z, final float yaw, final float pitch, Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					saveAttunment(player, world, x, y, z,yaw,pitch);
				}
			});
			
		}else{
			saveAttunment(player, world, x, y, z,yaw,pitch);
		}
	}
	
	public void loadAttunments() {
		if (Config.mysqlEnabled == true){
			mysql.loadAttunements();
		}else{
			sqlite.loadAttunements();
		}
	}
	
	public void loadAttunments(Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					loadAttunments();
				}
			});
			
		}else{
			loadAttunments();
		}
	}
	
	public void loadLifestones() {
		if (Config.mysqlEnabled == true){
			mysql.loadLifestones();
		}else{
			sqlite.loadLifestones();
		}
	}
	
	public void loadLifestones(Boolean async) {
		if (async == true){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					loadLifestones();
				}
			});
			
		}else{
			loadLifestones();
		}
	}
	
}
