package com.cyprias.Lifestones;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class Config {
	private static Lifestones plugin;
	private static Configuration config;
	

	public static Boolean fallbackToOtherWorldLifestone, mysqlEnabled, preferAsyncDBCalls, setUnregisteredLifestonesToAir, debugMessages, checkForNewVersion, lookAtNearestLS, callBlockPlaceEvent, allowPerWorldAttunement;
	public static String sqlUsername, sqlPassword, sqlURL, sqlPrefix, sqlDatabase, sqlHost, sqlPort, localeFile, respawnPriority;
	public static int protectLifestoneRadius, attuneDelay, recallDelay, protectPlayerAfterRecallDuration, randomTPRadius, rowsPerPage;
	public static Boolean useMetrics;
	
	public Config(Lifestones plugin) {
		Config.plugin = plugin;
		
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	public static void onEnable() throws FileNotFoundException, IOException, InvalidConfigurationException{
		reloadOurConfig();
	}
	public static void reloadOurConfig() throws FileNotFoundException, IOException, InvalidConfigurationException{
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		loadConfigOpts();
	}
	private static void loadConfigOpts() throws FileNotFoundException, IOException, InvalidConfigurationException{
		preferAsyncDBCalls = config.getBoolean("preferAsyncDBCalls");
		setUnregisteredLifestonesToAir = config.getBoolean("setUnregisteredLifestonesToAir");
		protectLifestoneRadius = config.getInt("protectLifestoneRadius");
		
		debugMessages = config.getBoolean("debugMessages");
		checkForNewVersion = config.getBoolean("checkForNewVersion");
		
		mysqlEnabled = config.getBoolean("mysql.enabled");
		sqlUsername = config.getString("mysql.username");
		sqlPassword = config.getString("mysql.password");
		sqlPrefix = config.getString("mysql.prefix"); 
		sqlDatabase = config.getString("mysql.database");
		sqlHost = config.getString("mysql.hostname");
		sqlPort = config.getString("mysql.port");
		sqlURL = "jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase;
		
		
		attuneDelay = config.getInt("attuneDelay");
		recallDelay = config.getInt("recallDelay");
		protectPlayerAfterRecallDuration = config.getInt("protectPlayerAfterRecallDuration");
		randomTPRadius = config.getInt("randomTPRadius");
		
		rowsPerPage = config.getInt("rowsPerPage");
		lookAtNearestLS = config.getBoolean("lookAtNearestLS");
		
		callBlockPlaceEvent = config.getBoolean("callBlockPlaceEvent");
		
		localeFile = config.getString("localeFile");
		
		allowPerWorldAttunement = config.getBoolean("allowPerWorldAttunement");
		
		fallbackToOtherWorldLifestone = config.getBoolean("fallbackToOtherWorldLifestone");
		useMetrics = config.getBoolean("use-metrics");
		
		respawnPriority = config.getString("respawn-listener-priority");
		
		
		loadStrucutre();
		loadDefaultAttunement();
	}
	
	public static void saveDefaultAttunement(Location loc){
		//String world, double x, double y, double z,  float yaw, float pitch
		config.set("defaultAttunement.world", loc.getWorld().getName());
		config.set("defaultAttunement.x", loc.getX());
		config.set("defaultAttunement.y", loc.getY());
		config.set("defaultAttunement.z", loc.getZ());
		config.set("defaultAttunement.yaw", loc.getYaw());
		config.set("defaultAttunement.pitch", loc.getPitch());
		plugin.saveConfig();
	}
	
	public static void loadDefaultAttunement(){
		if (config.contains("defaultAttunement")){
			String world = config.getString("defaultAttunement.world");
			double x = config.getDouble("defaultAttunement.x");
			double y = config.getDouble("defaultAttunement.y");
			double z = config.getDouble("defaultAttunement.z");
			float yaw = (float) config.getDouble("defaultAttunement.yaw");
			float pitch = (float) config.getDouble("defaultAttunement.pitch");
			Attunements.setDefaultAttunement(world, x, y, z, yaw,pitch);
		}
	}
	
	static public class lifestoneStructure {
		public int rX, rY, rZ;
		public int bID;
		public byte bData;
		
		
		public lifestoneStructure(int rX, int rY, int rZ, int bID, Byte bData){
			this.rX = rX;
			this.rY = rY;
			this.rZ = rZ;
			this.bID = bID;
			this.bData = bData;
		}
	}
	public static List<lifestoneStructure> structureBlocks = new ArrayList<lifestoneStructure>();
	
	
	private static void loadStrucutre() throws FileNotFoundException, IOException, InvalidConfigurationException{
		YML yml = new YML(plugin.getResource("structure.yml"),plugin.getDataFolder(), "structure.yml");

		ConfigurationSection dStructure = yml.getConfigurationSection("structure");

		structureBlocks.clear();
		for (String rCoords : dStructure.getKeys(false)) {
			String[] coords = rCoords.split(",");

			int X = Integer.parseInt(coords[0]);
			int Y = Integer.parseInt(coords[1]);
			int Z = Integer.parseInt(coords[2]);
			
			
			
			String[] blockData = dStructure.getString(rCoords).split(";");
			
			int id = Integer.parseInt(blockData[0]);
			byte data = Byte.parseByte(blockData[1]);
			
				
			structureBlocks.add(new lifestoneStructure(X,Y,Z,id,data));
			
			
		}
		
		
		
	}
	
	public static List<String> getStringList(String property) {
		return Lifestones.getInstance().getConfig().getStringList(property);
	}
	
	public static Double getDouble(String property) {
		return Lifestones.getInstance().getConfig().getDouble(property);
	}
	
}
