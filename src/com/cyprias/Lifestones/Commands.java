package com.cyprias.Lifestones;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Config.lifestoneStructure;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;


public class Commands implements CommandExecutor {
	private Lifestones plugin;
	public Commands(Lifestones plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//final String message = getFinalArg(args, 0);
		//plugin.info(sender.getName() + ": /" + cmd.getName() + " " + message);
	
		if (cmd.getName().equals("lifestone")){
			if (!(Attunements.players.containsKey(sender.getName()))){
				plugin.sendMessage(sender, "You have not attuned to a lifestone yet.");
				return true;
			}
			
			Attunement attunement = Attunements.players.get(sender.getName());
			
			
			Location loc = new Location(plugin.getServer().getWorld(attunement.world), attunement.x, attunement.y, attunement.z, attunement.yaw, attunement.pitch);
			
			Player player = (Player) sender;
			player.teleport(loc);
			plugin.sendMessage(sender, "Recalled to lifestone");
			
			
		}else if (cmd.getName().equals("lifestones")){
		
		if (args.length > 0){
			if (args[0].equalsIgnoreCase("create")){
				
				Player player = (Player) sender;
				
				Block pBlock = player.getLocation().getBlock();
				if (plugin.isProtected(pBlock)){
					plugin.sendMessage(sender, "You're too close to another lifestone.");
					return true;
				}
				
				Block rBlock;
				
				lifestoneStructure lsBlock;
				for (int i=0; i<Config.structureBlocks.size();i++){
					lsBlock = Config.structureBlocks.get(i);
					rBlock = pBlock.getRelative(lsBlock.rX, lsBlock.rY, lsBlock.rZ);
					rBlock.setTypeId(lsBlock.bID);
					rBlock.setData(lsBlock.bData);
				}
				
				plugin.regsterLifestone(new lifestoneLoc(pBlock.getWorld().getName(), pBlock.getX(), pBlock.getY(), pBlock.getZ()));
				plugin.database.saveLifestone(pBlock.getWorld().getName(), pBlock.getX(), pBlock.getY(), pBlock.getZ(), Config.preferAsyncDBCalls);
				
				
				return true;
			}else if (args[0].equalsIgnoreCase("sql")){
				plugin.database.loadLifestones(Config.preferAsyncDBCalls);
				return true;
				//sqliteTest1
			}
		}
		}
		
		return false;
	}
	
	public static String getFinalArg(final String[] args, final int start) {
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}
	public boolean hasCommandPermission(CommandSender player, String permission) {
		if (plugin.hasPermission(player, permission)) {
			return true;
		}
		// sendMessage(player, F("stNoPermission", permission));
		plugin.sendMessage(player, ChatColor.GRAY+"You do not have permission: " + ChatColor.YELLOW + permission);
		return false;
	}

}
