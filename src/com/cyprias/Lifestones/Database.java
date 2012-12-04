package com.cyprias.Lifestones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitScheduler;

import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Databases.MySQL;
import com.cyprias.Lifestones.Databases.SQLite;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;

public class Database {
	private static Lifestones plugin;
	//private SQLite sqlite;
	//private MySQL mysql;
	static BukkitScheduler scheduler;
	public Database(Lifestones plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();

		new SQLite(this, plugin.getDataFolder());
		new MySQL(this);
	}
	

	public static void createTables(){
		if (Config.mysqlEnabled == true){
			MySQL.createTables();
		}else{
			SQLite.createTables();
		}
	}
	
	public void createTables(Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					createTables();
				}
			});
			
		}else{
			createTables();
		}
	}
	
	public static void loadDatabases(){
		loadLifestones();
		loadAttunments();
	}
		
	public static void loadDatabases(Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					loadDatabases();
				}
			});
			
		}else{
			loadDatabases();
		}
	}
	
	public static void saveLifestone(String world, int X, int Y, int Z){
		if (Config.mysqlEnabled == true){
			MySQL.saveLifestone(world, X, Y, Z);
		}else{
			SQLite.saveLifestone(world, X, Y, Z);
		}

	}
	public static void saveLifestone(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					saveLifestone(world, X, Y, Z);
				}
			});
			
		}else{
			saveLifestone(world, X, Y, Z);
		}
	}
	
	public static void removeLifestone(String world, int X, int Y, int Z){
		if (Config.mysqlEnabled == true){
			MySQL.removeLifestone(world, X, Y, Z);	
		}else{
			SQLite.removeLifestone(world, X, Y, Z);
		}

	}
	
	public static void removeLifestone(final String world, final int X, final int Y, final int Z, Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					removeLifestone(world, X, Y, Z);
				}
			});
			
		}else{
			removeLifestone(world, X, Y, Z);
		}
	}
	
	public static void saveAttunment(String player, String world, double x, double y, double z,  float yaw, float pitch){
		if (Config.mysqlEnabled == true){
			MySQL.saveAttunment(player, world, x, y, z,yaw,pitch);
		}else{
			SQLite.saveAttunment(player, world, x, y, z,yaw,pitch);
		}

	}
	
	public static void saveAttunment(final String player, final String world, final double x, final double y, final double z, final float yaw, final float pitch, Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					saveAttunment(player, world, x, y, z,yaw,pitch);
				}
			});
			
		}else{
			saveAttunment(player, world, x, y, z,yaw,pitch);
		}
	}
	
	public static void removeOtherWorldAttunments(String player, String world){
		if (Config.mysqlEnabled == true){
			MySQL.removeOtherWorldAttunments(player, world);	
		}else{
			SQLite.removeOtherWorldAttunments(player, world);
		}

	}
	public static void removeOtherWorldAttunments(final String player, final String world, Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					removeOtherWorldAttunments(player, world);
				}
			});
			
		}else{
			removeOtherWorldAttunments(player, world);
		}
	}
	
	public static void loadAttunments() {
		if (Config.mysqlEnabled == true){
			MySQL.loadAttunements();
		}else{
			SQLite.loadAttunements();
		}
	}
	
	public void loadAttunments(Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					loadAttunments();
				}
			});
			
		}else{
			loadAttunments();
		}
	}
	
	public static void loadLifestones() {
		if (Config.mysqlEnabled == true){
			MySQL.loadLifestones();
		}else{
			SQLite.loadLifestones();
		}
	}
	
	public void loadLifestones(Boolean async) {
		if (async == true){
			scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					loadLifestones();
				}
			});
			
		}else{
			loadLifestones();
		}
	}
	
	public void regsterLifestone(final lifestoneLoc lsLoc) {
		plugin.regsterLifestone(lsLoc);
	}
}
