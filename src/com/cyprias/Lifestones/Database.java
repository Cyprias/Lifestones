package com.cyprias.Lifestones;

import java.sql.SQLException;

import org.bukkit.scheduler.BukkitScheduler;

import com.cyprias.Lifestones.Databases.MySQL;
import com.cyprias.Lifestones.Databases.SQLite;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;

public class Database {
	private static Lifestones plugin;
	//private SQLite sqlite;
	//private MySQL mysql;
	static BukkitScheduler scheduler;
	public Database(Lifestones plugin) {
		Database.plugin = plugin;
		Database.scheduler = plugin.getServer().getScheduler();

		new SQLite(this, plugin.getDataFolder());
		new MySQL(this);
	}
	

	public static void createTables() throws SQLException, ClassNotFoundException{
		if (Config.mysqlEnabled == true){
			MySQL.createTables();
		}else{
			SQLite.createTables();
		}
	}
	
	public void createTables(Boolean async) throws SQLException, ClassNotFoundException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						createTables();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			createTables();
		}
	}
	
	public static void loadDatabases() throws SQLException{
		loadLifestones();
		loadAttunments();
	}
		
	public static void loadDatabases(Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						loadDatabases();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			loadDatabases();
		}
	}
	
	public static void saveLifestone(String world, int X, int Y, int Z) throws SQLException{
		if (Config.mysqlEnabled == true){
			MySQL.saveLifestone(world, X, Y, Z);
		}else{
			SQLite.saveLifestone(world, X, Y, Z);
		}

	}
	public static void saveLifestone(final String world, final int X, final int Y, final int Z, Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						saveLifestone(world, X, Y, Z);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			saveLifestone(world, X, Y, Z);
		}
	}
	
	public static void removeLifestone(String world, int X, int Y, int Z) throws SQLException{
		if (Config.mysqlEnabled == true){
			MySQL.removeLifestone(world, X, Y, Z);	
		}else{
			SQLite.removeLifestone(world, X, Y, Z);
		}

	}
	
	public static void removeLifestone(final String world, final int X, final int Y, final int Z, Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						removeLifestone(world, X, Y, Z);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			removeLifestone(world, X, Y, Z);
		}
	}
	
	public static void saveAttunment(String player, String world, double x, double y, double z,  float yaw, float pitch) throws SQLException{
		if (Config.mysqlEnabled == true){
			MySQL.saveAttunment(player, world, x, y, z,yaw,pitch);
		}else{
			SQLite.saveAttunment(player, world, x, y, z,yaw,pitch);
		}

	}
	
	public static void saveAttunment(final String player, final String world, final double x, final double y, final double z, final float yaw, final float pitch, Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						saveAttunment(player, world, x, y, z,yaw,pitch);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			saveAttunment(player, world, x, y, z,yaw,pitch);
		}
	}
	
	public static void removeOtherWorldAttunments(String player, String world) throws SQLException{
		if (Config.mysqlEnabled == true){
			MySQL.removeOtherWorldAttunments(player, world);	
		}else{
			SQLite.removeOtherWorldAttunments(player, world);
		}

	}
	public static void removeOtherWorldAttunments(final String player, final String world, Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						removeOtherWorldAttunments(player, world);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			removeOtherWorldAttunments(player, world);
		}
	}
	
	public static void loadAttunments() throws SQLException {
		if (Config.mysqlEnabled == true){
			MySQL.loadAttunements();
		}else{
			SQLite.loadAttunements();
		}
	}
	
	public void loadAttunments(Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						loadAttunments();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}else{
			loadAttunments();
		}
	}
	
	public static void loadLifestones() throws SQLException {
		if (Config.mysqlEnabled == true){
			MySQL.loadLifestones();
		}else{
			SQLite.loadLifestones();
		}
	}
	
	public void loadLifestones(Boolean async) throws SQLException {
		if (async == true){
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					try {
						loadLifestones();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
