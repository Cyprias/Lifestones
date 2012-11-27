package com.cyprias.Lifestones;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.cyprias.Lifestones.Config.lifestoneStructure;


public class Lifestones extends JavaPlugin {
	private String stPluginEnabled = "§f%s §7v§f%s §7is enabled.";
	public static String chatPrefix = "§f[§aLs§f] ";
	static String pluginName;
	public Commands commands;
	public Config config;
	public YML yml;
	public Events events;
	public Database database;
	public void onLoad() {
		pluginName = getDescription().getName();
		
		this.yml = new YML(this);
		this.config = new Config(this);
		this.commands = new Commands(this);
		this.events = new Events(this);
		this.database = new Database(this);
	}
	
	public void onEnable() {
		config.reloadOurConfig();
		getCommand("lifestones").setExecutor(this.commands);
		getServer().getPluginManager().registerEvents(this.events, this);
		
		database.loadLifestones(Config.preferAsyncDBCalls);
		
		info(String.format(stPluginEnabled, pluginName, this.getDescription().getVersion()));
	}
	public void onDisable() {
	
		getCommand("lifestones").setExecutor(null);
	}
	
	private Logger log = Logger.getLogger("Minecraft");

	public void info(String msg) {
		getServer().getConsoleSender().sendMessage(chatPrefix + msg);
	}

	
	public boolean hasPermission(CommandSender sender, String node) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (player.isPermissionSet(node)) // in case admin purposely set the
											// node to false.
			return player.hasPermission(node);

		if (player.isPermissionSet(pluginName.toLowerCase() + ".*"))
			return player.hasPermission(pluginName.toLowerCase() + ".*");

		String[] temp = node.split("\\.");
		String wildNode = temp[0];
		for (int i = 1; i < (temp.length); i++) {
			wildNode = wildNode + "." + temp[i];

			if (player.isPermissionSet(wildNode + ".*"))
				// plugin.info("wildNode1 " + wildNode+".*");
				return player.hasPermission(wildNode + ".*");
		}

		return player.hasPermission(node);
	}
	
	public void sendMessage(CommandSender sender, String message) {
		info("§e" + sender.getName() + "->§f" + message);
	}
	
	/**/
	static public class  lifestoneLoc {
		String world;
		int X, Y, Z;
		public lifestoneLoc(String world, int X, int Y, int Z){
			this.world = world;
			this.X = X;
			this.Y = Y;
			this.Z = Z;
		}
	}
	
	public ArrayList<lifestoneLoc> lifestoneLocations = new ArrayList<lifestoneLoc>();
	public void regsterLifestone(lifestoneLoc lsLoc){
		for (int i=0;i<lifestoneLocations.size();i++){
			if (lifestoneLocations.get(i).world.equals(lsLoc.world)){
				if (lifestoneLocations.get(i).X == lsLoc.X && lifestoneLocations.get(i).Y == lsLoc.Y && lifestoneLocations.get(i).Z == lsLoc.Z){
			//		info("LS already in aray...");
					return;
				}
			}
		}
		lifestoneLocations.add(lsLoc);
		info("Added LS at " + lsLoc.world + ", " + lsLoc.X + ", " + lsLoc.Y + ", " + lsLoc.Z);
	}
	
	public Boolean isLifestone(Block block){
		String bWorld = block.getWorld().getName();
		int bX = block.getX();
		int bY = block.getY();
		int bZ = block.getZ();
		
		lifestoneStructure lsBlock;
		lifestoneLoc loc;
		for (int l=0;l<lifestoneLocations.size();l++){
			loc = lifestoneLocations.get(l);

			if (!(loc.world.equalsIgnoreCase(bWorld)))
				continue;

			if (loc.X == bX && loc.Y == bY && loc.Z == bZ){
				return true;
			}
			for (int b=0; b<Config.structureBlocks.size();b++){
				lsBlock = Config.structureBlocks.get(b);
				if ((loc.X+lsBlock.rX) == bX && (loc.Y+lsBlock.rY) == bY && (loc.Z+lsBlock.rZ) == bZ){
					return true;
				}
				
				
			}
			
		}
		
		return false;
	}
	
}
