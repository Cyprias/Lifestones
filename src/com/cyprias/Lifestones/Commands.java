package com.cyprias.Lifestones;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.Lifestones.Config.lifestoneStructure;


public class Commands implements CommandExecutor {
	private Lifestones plugin;
	public Commands(Lifestones plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//final String message = getFinalArg(args, 0);
		//plugin.info(sender.getName() + ": /" + cmd.getName() + " " + message);
	
		if (args.length > 0){
			if (args[0].equalsIgnoreCase("create")){
				
				Player player = (Player) sender;
				
				Block pBlock = player.getLocation().getBlock();
				Block rBlock;
				
				lifestoneStructure lsBlock;
				for (int i=0; i<Config.structureBlocks.size();i++){
					lsBlock = Config.structureBlocks.get(i);
					rBlock = pBlock.getRelative(lsBlock.rX, lsBlock.rY, lsBlock.rZ);
					rBlock.setTypeId(lsBlock.bID);
					rBlock.setData(lsBlock.bData);
				}
				plugin.database.saveLifestone(pBlock, Config.preferAsyncDBCalls);
				//plugin.regsterLifestone(new lifestoneLoc(pBlock.getWorld().getName(), pBlock.getX(), pBlock.getY(), pBlock.getZ()));
				return true;
			}else if (args[0].equalsIgnoreCase("sql")){
				plugin.database.loadLifestones(Config.preferAsyncDBCalls);
				return true;
				//sqliteTest1
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
