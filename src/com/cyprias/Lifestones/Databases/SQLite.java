package com.cyprias.Lifestones.Databases;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.block.Block;

import com.cyprias.Lifestones.Database;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;

public class SQLite {
	private Database database;
	private String pluginPath;

	private String sqlDB;

	public SQLite(Database databases, File file) {
		this.database = databases;
		this.pluginPath = file.getPath() + file.separator;

		sqlDB = "jdbc:sqlite:" + pluginPath + "database.sqlite";
		createTables();
	}

	public boolean tableExists(String tableName) {
		boolean exists = false;
		String query;

		try {
			Connection con = DriverManager.getConnection(sqlDB);
			Statement stat = con.createStatement();
			ResultSet result = stat.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';");

			while (result.next()) {
				exists = true;
				break;
			}

			result.close();
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return exists;
	}

	public void createTables() {
		try {
			Connection con = DriverManager.getConnection(sqlDB);
			Statement stat = con.createStatement();

			if (tableExists("Lifestones") == false) {
				database.plugin.info("Creating lifestones table.");
				stat.executeUpdate("CREATE TABLE `Lifestones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `world` VARCHAR(32) NOT NULL, `x` INT NOT NULL, `y` INT NOT NULL, `z` INT NOT NULL)");
			}

			stat.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sqliteTest1() {
		try {

			Connection con = DriverManager.getConnection("jdbc:sqlite:" + pluginPath + "test.db");

			Statement stat = con.createStatement();

			stat.executeUpdate("drop table if exists people;");
			stat.executeUpdate("create table people (name, occupation);");
			PreparedStatement prep = con.prepareStatement("insert into people values (?, ?);");

			prep.setString(1, "Gandhi");
			prep.setString(2, "politics");
			prep.addBatch();
			prep.setString(1, "Turing");
			prep.setString(2, "computers");
			prep.addBatch();
			prep.setString(1, "Wittgenstein");
			prep.setString(2, "smartypants");
			prep.addBatch();

			con.setAutoCommit(false);
			prep.executeBatch();
			con.setAutoCommit(true);

			ResultSet rs = stat.executeQuery("select * from people;");
			while (rs.next()) {
				System.out.println("name = " + rs.getString("name"));
				System.out.println("job = " + rs.getString("occupation"));
			}
			rs.close();
			con.close();

			// Statement stat = conn.createStatement();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadLifestones() {
		
		try {
			Connection con = DriverManager.getConnection(sqlDB);
			Statement stat = con.createStatement();
			ResultSet rs = stat.executeQuery("select * from Lifestones;");
			while (rs.next()) {
				
				
			
				
				database.plugin.regsterLifestone(new lifestoneLoc(rs.getString("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")));
				
				
			}
			rs.close();
			con.close();
			
			
			
		} catch (SQLException e) {e.printStackTrace();}
		
	}
	
	public void saveLifestone(String bWorld, int bX, int bY, int bZ) {//Block block
		//String bWorld = block.getWorld().getName();
		//int bX, bY, bZ;
		//bX = block.getX();
		//bY = block.getY();
		//bZ = block.getZ();
			
		try {
			Connection con = DriverManager.getConnection(sqlDB);

			Statement stat = con.createStatement();
			ResultSet rs = stat.executeQuery("select * from Lifestones;");
			while (rs.next()) {
				if (rs.getString("world").equalsIgnoreCase(bWorld)){
					
					if (rs.getInt("x") == bX && rs.getInt("y") == bY && rs.getInt("z") == bZ){
						System.out.println("Lifestone already in DB!");
						rs.close();
						stat.close();
						con.close();
						return;
					}
					
				}
			}
			
			PreparedStatement prep = con.prepareStatement("insert into Lifestones (world, x, y, z) values (?, ?, ?, ?)");
			prep.setString(1, bWorld);
			prep.setInt(2, bX);
			prep.setInt(3, bY);
			prep.setInt(4, bZ);

			prep.execute();

			
			rs.close();
			stat.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAttunment(String bWorld, int bX, int bY, int bZ) {
		
		
	}
	
	
	public void removeLifestone(String bWorld, int bX, int bY, int bZ) {
		
		try {
			Connection con = DriverManager.getConnection(sqlDB);

			Statement stat = con.createStatement();
			ResultSet rs = stat.executeQuery("select * from Lifestones;");

			
			PreparedStatement prep = con.prepareStatement("DELETE from Lifestones where `world` LIKE ? AND `x` LIKE ? AND `y` LIKE ? AND `z` LIKE ?");
			prep.setString(1, bWorld);
			prep.setInt(2, bX);
			prep.setInt(3, bY);
			prep.setInt(4, bZ);

			prep.execute();

			
			rs.close();
			stat.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

}
