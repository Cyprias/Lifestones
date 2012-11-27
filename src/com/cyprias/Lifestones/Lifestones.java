package com.cyprias.Lifestones;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public void sendMessage(CommandSender sender, String message, Boolean showConsole, Boolean sendPrefix) {
		if (sender instanceof Player && showConsole == true) {
			info("§e" + sender.getName() + "->§f" + message);
		}
		if (sendPrefix == true) {
			sender.sendMessage(chatPrefix + message);
		} else {
			sender.sendMessage(message);
		}
	}

	public void sendMessage(CommandSender sender, String message, Boolean showConsole) {
		sendMessage(sender, message, showConsole, true);
	}

	public void sendMessage(CommandSender sender, String message) {
		sendMessage(sender, message, true);
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
	
	public static double getUnixTime() {
		return (System.currentTimeMillis() / 1000D);
	}
	
	public ArrayList<lifestoneLoc> lifestoneLocations = new ArrayList<lifestoneLoc>();
	public void regsterLifestone(final lifestoneLoc lsLoc){
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
		//isLifestoneCache.clear();
		
		//our loadLifestones function may be called asyncly, caching blocks grabs blocks so should be run in the main thread. 
		info("Preping cache + " + getUnixTime());
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				cacheSurroundBlocks(lsLoc);
			}
		});
	}

	public static HashMap<Block, Block> isLifestoneCache = new HashMap<Block, Block>();
	public static HashMap<Block, Block> isProtectedCache = new HashMap<Block, Block>();
	
	private void cacheSurroundBlocks(lifestoneLoc loc){
		info("Starting cache + " + getUnixTime());
		Block cBlock = getServer().getWorld(loc.world).getBlockAt(loc.X , loc.Y, loc.Z);
		lifestoneStructure lsBlock;
		for (int b=0; b<Config.structureBlocks.size();b++){
			lsBlock = Config.structureBlocks.get(b);
			isLifestoneCache.put(getServer().getWorld(loc.world).getBlockAt(loc.X+lsBlock.rX , loc.Y+lsBlock.rY, loc.Z+lsBlock.rZ), cBlock);
			//info("caching " + (loc.X+lsBlock.rX) +", " + (loc.Y+lsBlock.rY) + ", " + (loc.Z+lsBlock.rZ));
		}
		
		
		
	    for (int y_iter = cBlock.getX()+Config.protectLifestoneRadius; y_iter > cBlock.getX()-Config.protectLifestoneRadius; y_iter--) {
	        for (int x_iter = cBlock.getY()+Config.protectLifestoneRadius; x_iter > cBlock.getY()-Config.protectLifestoneRadius; x_iter--) {
	          for (int z_iter = cBlock.getZ()+Config.protectLifestoneRadius; z_iter > cBlock.getZ()-Config.protectLifestoneRadius; z_iter--) {
	        	  
	        	  isProtectedCache.put(getServer().getWorld(loc.world).getBlockAt(y_iter , x_iter, z_iter), cBlock);
	        //	  info("protecting " + y_iter +", " +x_iter + ", " + z_iter);
	        	  
	          }
	        }
	    }
	    info("ending cache + " + getUnixTime());
	}
	
	private void removeCachedSurroundBlocks(lifestoneLoc loc){
		Block cBlock = getServer().getWorld(loc.world).getBlockAt(loc.X , loc.Y, loc.Z);
		lifestoneStructure lsBlock;
		Block rBlock;
		//for (int b=0; b<Config.structureBlocks.size();b++){
		for (int b=Config.structureBlocks.size()-1; b>= 0;b--){//Backwards so our buttons are removed first, not fall on the ground.
			lsBlock = Config.structureBlocks.get(b);
			//isLifestoneCache.put(cBlock, getServer().getWorld(loc.world).getBlockAt(loc.X+lsBlock.rX , loc.Y+lsBlock.rY, loc.Z+lsBlock.rZ));
			rBlock = getServer().getWorld(loc.world).getBlockAt(loc.X+lsBlock.rX , loc.Y+lsBlock.rY, loc.Z+lsBlock.rZ);
			isLifestoneCache.remove(rBlock);
			if (Config.setUnregisteredLifestonesToAir == true){
				rBlock.setTypeId(0);
			}
		}
		
	    for (int y_iter = cBlock.getX()+Config.protectLifestoneRadius; y_iter > cBlock.getX()-Config.protectLifestoneRadius; y_iter--) {
	        for (int x_iter = cBlock.getY()+Config.protectLifestoneRadius; x_iter > cBlock.getY()-Config.protectLifestoneRadius; x_iter--) {
	          for (int z_iter = cBlock.getZ()+Config.protectLifestoneRadius; z_iter > cBlock.getZ()-Config.protectLifestoneRadius; z_iter--) {
	        	  
	        	  isProtectedCache.remove(getServer().getWorld(loc.world).getBlockAt(y_iter , x_iter, z_iter)); 
	        //	  info("protecting " + y_iter +", " +x_iter + ", " + z_iter);
	        	  
	          }
	        }
	    }
	}
	
	
	public void unregsterLifestone(final lifestoneLoc lsLoc){
		for (int i=0;i<lifestoneLocations.size();i++){
			if (lifestoneLocations.get(i).world.equals(lsLoc.world)){
				if (lifestoneLocations.get(i).X == lsLoc.X && lifestoneLocations.get(i).Y == lsLoc.Y && lifestoneLocations.get(i).Z == lsLoc.Z){
					lifestoneLocations.remove(i);

					removeCachedSurroundBlocks(lsLoc);
					
					return;
				}
			}
		}
		
	}
	
	public Boolean isProtected(Block block){
		if (isProtectedCache.containsKey(block))
			return true;

		return false;
	}
	public Boolean isLifestone(Block block){
		if (isLifestoneCache.containsKey(block))
			return true;

		return false;
	}
	
	public Block getLifestoneCenterBlock(Block block){
		if (isLifestone(block)){
			return isLifestoneCache.get(block);
		}
		return null;
	}
	
}
