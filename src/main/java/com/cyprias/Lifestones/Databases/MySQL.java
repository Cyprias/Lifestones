package com.cyprias.Lifestones.Databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cyprias.Lifestones.Attunements;
import com.cyprias.Lifestones.Config;
import com.cyprias.Lifestones.Database;
import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;

public class MySQL {

	private static Database database;
	public MySQL(Database database) {
		MySQL.database = database;
		// try {Class.forName("com.mysql.jdbc.Driver");} catch
		// (ClassNotFoundException e) {e.printStackTrace();}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(Config.sqlURL, Config.sqlUsername, Config.sqlPassword);
	}

	public static boolean tableExists(String tableName) throws SQLException {
		boolean exists = false;
		Connection con = getConnection();

		PreparedStatement statement = con.prepareStatement("show tables like '" + tableName + "'");
		ResultSet result = statement.executeQuery();

		result.last();
		if (result.getRow() != 0) {

			exists = true;
		}

		// //////
		result.close();
		statement.close();
		con.close();

		return exists;
	}

	public static void createTables() throws SQLException {
		String query;
		Connection con = getConnection();
		PreparedStatement statement = null;

		if (tableExists(Config.sqlPrefix + "Lifestones") == false) {
			System.out.println("Creating Lifestones.Lifestones table.");
			query = "CREATE TABLE `"
				+ Config.sqlPrefix
				+ "Lifestones` (`id` INT PRIMARY KEY AUTO_INCREMENT, `world` VARCHAR(32) NOT NULL, `x` INT NOT NULL, `y` INT NOT NULL, `z` INT NOT NULL) ENGINE = InnoDB";
			statement = con.prepareStatement(query);
			statement.executeUpdate();
		}

		if (tableExists(Config.sqlPrefix + "Attunements") == true) {// old
																	// table;
			System.out.println("Removing unique attribute from player in Attunements.");
			con.prepareStatement("ALTER TABLE " + Config.sqlPrefix + "Attunements DROP INDEX player").executeUpdate();
			// con.prepareStatement("ALTER TABLE "+Config.sqlPrefix +
			// "Attunements add unique index(player, world);").executeUpdate();
			con.prepareStatement("RENAME TABLE `" + Config.sqlPrefix + "Attunements` TO `" + Config.sqlPrefix + "Attunements_v2`").executeUpdate();
		} else if (tableExists(Config.sqlPrefix + "Attunements_v2") == false) {
			System.out.println("Creating Lifestones.Attunements table.");
			query = "CREATE TABLE `"
				+ Config.sqlPrefix
				+ "Attunements_v2` (`id` INT PRIMARY KEY AUTO_INCREMENT, `player` VARCHAR(32) NOT NULL, `world` VARCHAR(32) NOT NULL, `x` DOUBLE NOT NULL, `y` DOUBLE NOT NULL, `z` DOUBLE NOT NULL, `yaw` FLOAT NOT NULL, `pitch` FLOAT NOT NULL) ENGINE = InnoDB";
			statement = con.prepareStatement(query);
			statement.executeUpdate();
		}

		// statement.close();
		con.close();
	}

	public static void saveLifestone(String bWorld, int bX, int bY, int bZ) throws SQLException {// Block
																									// block
		String table = Config.sqlPrefix + "Lifestones";

		Connection con = getConnection();
		PreparedStatement statement = null;
		int success = 0;

		String query = "select * from " + table + " where `world` LIKE ? AND `x` LIKE ?  AND `y` LIKE ?  AND `z` LIKE ? ";

		statement = con.prepareStatement(query);
		statement.setString(1, bWorld);
		statement.setInt(2, bX);
		statement.setInt(3, bY);
		statement.setInt(4, bZ);

		ResultSet result = statement.executeQuery();
		while (result.next()) {
			success = 1;
			break;
		}

		statement.close();

		if (success > 0) {
			// System.out.println("mysql Lifestone already in DB!");
			return;
		}

		if (success == 0) {
			query = "INSERT INTO `" + table + "` (`world`, `x`, `y`, `z`) VALUES (?, ?, ?, ?);";
			statement = con.prepareStatement(query);
			statement.setString(1, bWorld);
			statement.setInt(2, bX);
			statement.setInt(3, bY);
			statement.setInt(4, bZ);

			success = statement.executeUpdate();
			statement.close();
			con.close();
		}

		con.close();
	}

	public static void loadLifestones() throws SQLException {
		String table = Config.sqlPrefix + "Lifestones";

		Connection con = getConnection();
		PreparedStatement statement = null;
		String query = "select * from " + table;

		statement = con.prepareStatement(query);

		ResultSet result = statement.executeQuery();
		while (result.next()) {
			database.regsterLifestone(new lifestoneLoc(result.getString("world"), result.getInt("x"), result.getInt("y"), result.getInt("z")));
		}

		statement.close();
		con.close();
	}

	public static void removeLifestone(String bWorld, int bX, int bY, int bZ) throws SQLException {
		String table = Config.sqlPrefix + "Lifestones";
		Connection con = getConnection();
		PreparedStatement statement = null;
		String query = "DELETE from `" + table + "` where `world` LIKE ? AND `x` LIKE ? AND `y` LIKE ? AND `z` LIKE ?";
		statement = con.prepareStatement(query);
		statement.setString(1, bWorld);
		statement.setInt(2, bX);
		statement.setInt(3, bY);
		statement.setInt(4, bZ);

		statement.executeUpdate();

		statement.close();
		con.close();

		// System.out.println("removeLifestone: " +success );

	}

	public static void removeOtherWorldAttunments(String player, String world) throws SQLException {
		String table = Config.sqlPrefix + "Attunements_v2";
		Connection con = getConnection();
		PreparedStatement statement = null;
		String query = "DELETE from `" + table + "` where `player` LIKE ? AND `world` NOT LIKE ?";
		statement = con.prepareStatement(query);
		statement.setString(1, player);
		statement.setString(2, world);
		// statement.setDouble(3, x);
		// statement.setDouble(4, y);
		// statement.setDouble(5, z);

		statement.executeUpdate();

		statement.close();
		con.close();

	}

	public static void saveAttunment(String player, String bWorld, double x, double y, double z, float yaw, float pitch) throws SQLException {
		String table = Config.sqlPrefix + "Attunements_v2";

		Connection con = getConnection();
		PreparedStatement statement = null;
		int success = 0;

		String query = "UPDATE `" + table + "` SET `x` = ?, `y` = ?, `z` = ?, `yaw` = ?, `pitch` = ? WHERE `player` = ? AND `world` = ?";

		statement = con.prepareStatement(query);
		statement.setDouble(1, x);
		statement.setDouble(2, y);
		statement.setDouble(3, z);
		statement.setFloat(4, yaw);
		statement.setFloat(5, pitch);
		statement.setString(6, player);
		statement.setString(7, bWorld);

		success = statement.executeUpdate();

		statement.close();

		if (success > 0) {
			return;
		}

		if (success == 0) {
			query = "insert into `" + table + "` (player, world, x,y,z,yaw, pitch) values (?, ?, ?, ?,?,?,?)";
			statement = con.prepareStatement(query);
			statement.setString(1, player);
			statement.setString(2, bWorld);

			statement.setDouble(3, x);
			statement.setDouble(4, y);
			statement.setDouble(5, z);
			statement.setFloat(6, yaw);
			statement.setFloat(7, pitch);

			success = statement.executeUpdate();
			statement.close();
			con.close();
		}

		con.close();
	}

	public static void loadAttunements() throws SQLException {
		String table = Config.sqlPrefix + "Attunements_v2";

		Connection con = getConnection();
		PreparedStatement statement = null;
		String query = "select * from " + table;

		statement = con.prepareStatement(query);

		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			Attunements.put(
				rs.getString("player"),
				new Attunement(rs.getString("player"), rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs
					.getFloat("pitch")));
		}
		statement.close();
		con.close();
	}

	public static int executeUpdate(String query, Object... args) throws SQLException {
		Connection con = getConnection();
		int sucessful = 0;

		PreparedStatement statement = con.prepareStatement(query);
		int i = 0;
		for (Object a : args) {
			i += 1;
			statement.setObject(i, a);
		}
		sucessful = statement.executeUpdate();
		con.close();
		return sucessful;
	}
	
	public static int removeAttunment(String player, String world) throws SQLException {
		// TODO Auto-generated method stub
		//query = "insert into `" + table + "` (player, world, x,y,z,yaw, pitch) values (?, ?, ?, ?,?,?,?)";
		//String query = "UPDATE `" + table + "` SET `x` = ?, `y` = ?, `z` = ?, `yaw` = ?, `pitch` = ? WHERE `player` = ? AND `world` = ?";
		String table = Config.sqlPrefix + "Attunements_v2";
		
		return executeUpdate("DELETE FROM " + table + " WHERE `player` LIKE ? AND `world` LIKE ?;", player, world);

		
		//con.prepareStatement("DELETE FROM " + Config.sqlPrefix + "Mailbox WHERE `amount` <= 0").executeUpdate();
		
	}

}
