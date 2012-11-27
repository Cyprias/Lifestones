package com.cyprias.Lifestones;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


public class Config {
	private Lifestones plugin;
	private static Configuration config;
	

	public static Boolean mysqlEnabled, preferAsyncDBCalls, setUnregisteredLifestonesToAir, debugMessages;
	public static int protectLifestoneRadius;
	
	public Config(Lifestones plugin) {
		this.plugin = plugin;
		
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public void reloadOurConfig(){
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		loadConfigOpts();
	}
	private void loadConfigOpts(){
		mysqlEnabled = config.getBoolean("mysql.enabled");
		preferAsyncDBCalls = config.getBoolean("preferAsyncDBCalls");
		setUnregisteredLifestonesToAir = config.getBoolean("setUnregisteredLifestonesToAir");
		protectLifestoneRadius = config.getInt("protectLifestoneRadius");
		
		debugMessages = config.getBoolean("debugMessages");
		
		loadStrucutre();
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
	static List<lifestoneStructure> structureBlocks = new ArrayList<lifestoneStructure>();
	
	
	private void loadStrucutre(){
		FileConfiguration fc = YML.getYMLConfig("structure.yml", plugin.getResource("structure.yml"));
		ConfigurationSection dStructure = fc.getConfigurationSection("structure");
		
		Block block;
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
}
