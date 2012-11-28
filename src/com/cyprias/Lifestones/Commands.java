package com.cyprias.Lifestones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Config.lifestoneStructure;
import com.cyprias.Lifestones.Events.attuneTask;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;

public class Commands implements CommandExecutor {
	private Lifestones plugin;

	public Commands(Lifestones plugin) {
		this.plugin = plugin;
	}

	public class recallTask implements Runnable {
		Player player;

		int pX, pY, pZ;

		public recallTask(Player player) {
			this.player = player;

			pX = player.getLocation().getBlockX();
			pY = player.getLocation().getBlockY();
			pZ = player.getLocation().getBlockZ();

			plugin.sendMessage(player, "Recalling to lifestone in " + Config.recallDelay + " seconds, move to cancel.");
		}

		public void run() {
			if (player.getLocation().getBlockX() != pX || player.getLocation().getBlockY() != pY || player.getLocation().getBlockZ() != pZ) {
				plugin.sendMessage(player, "You moved too far, attunement failed.");
				return;
			}

			Attunement attunement = Attunements.players.get(player.getName());

			Location loc = new Location(plugin.getServer().getWorld(attunement.world), attunement.x, attunement.y, attunement.z, attunement.yaw,
				attunement.pitch);

			player.teleport(loc);
			plugin.sendMessage(player, "Recalled to lifestone");
			
			
			plugin.playerProtections.put(player.getName(), plugin.getUnixTime() + Config.protectPlayerAfterRecallDuration);
			//protectPlayerAfterRecallDuration
		}
	}

	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// final String message = getFinalArg(args, 0);
		// plugin.info(sender.getName() + ": /" + cmd.getName() + " " +
		// message);

		if (commandLabel.equals("lifestone")) {
			if (args.length > 1) {
				return onCommand(sender, cmd, "lifestones", args);
			}
			if (!hasCommandPermission(sender, "lifestones.recall")) {
				return true;
			}
			
			
			
			if (!(Attunements.players.containsKey(sender.getName()))) {
				plugin.sendMessage(sender, "You have not attuned to a lifestone yet.");
				return true;
			}
			Player player = (Player) sender;

			recallTask task = new recallTask(player);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, Config.recallDelay*20L);
			return true;

		} else if (commandLabel.equals("lifestones")) {
			
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("create")) {
					if (!hasCommandPermission(sender, "lifestones.create")) {
						return true;
					}
					
					Player player = (Player) sender;

					Block pBlock = player.getLocation().getBlock();
					if (plugin.isProtected(pBlock)) {
						plugin.sendMessage(sender, "You're too close to another lifestone.");
						return true;
					}

					Block rBlock;

					lifestoneStructure lsBlock;
					for (int i = 0; i < Config.structureBlocks.size(); i++) {
						lsBlock = Config.structureBlocks.get(i);
						rBlock = pBlock.getRelative(lsBlock.rX, lsBlock.rY, lsBlock.rZ);
						rBlock.setTypeId(lsBlock.bID);
						rBlock.setData(lsBlock.bData);
					}
					
					//TP player above the device
					for (int y=1; y < (256-pBlock.getY()); y++){
						rBlock = pBlock.getRelative(0, y, 0);
						if (rBlock.getTypeId() == 0){
							player.teleport(new Location(player.getWorld(), rBlock.getX() + .5, rBlock.getY() + 1, rBlock.getZ() + .5));
							break;
						}
					}
					
					plugin.regsterLifestone(new lifestoneLoc(pBlock.getWorld().getName(), pBlock.getX(), pBlock.getY(), pBlock.getZ()));
					plugin.database.saveLifestone(pBlock.getWorld().getName(), pBlock.getX(), pBlock.getY(), pBlock.getZ(), Config.preferAsyncDBCalls);

					return true;
				}	else if (args[0].equalsIgnoreCase("reload")) {
					if (!hasCommandPermission(sender, "lifestones.reload")) {
						return true;
					}
					
					plugin.getPluginLoader().disablePlugin(plugin);
					plugin.getPluginLoader().enablePlugin(plugin);
					
					plugin.sendMessage(sender, "Plugin reloaded.");
					return true;
				} else if (args[0].equalsIgnoreCase("randomtp")) {
					if (!hasCommandPermission(sender, "lifestones.randomtp")) {
						return true;
					}
					Player player = (Player) sender;
					
					Location tpLoc = plugin.getRandomLocation(player.getWorld());
					
					if (tpLoc != null){
						player.teleport(tpLoc);
						plugin.sendMessage(player, ChatColor.GRAY+"Teleporting to " + ChatColor.GREEN + tpLoc.getBlockX() + ChatColor.GRAY+"x" +ChatColor.GREEN + tpLoc.getBlockZ() + ChatColor.GRAY+ ".");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("near")) {
					if (!hasCommandPermission(sender, "lifestones.near")) {
						return true;
					}
					
					List<lifestoneDistance> lifestones = new ArrayList<lifestoneDistance>();
					
					
					//CompareWarps comparator = new CompareWarps();
					//Collections.sort(warps, comparator);
					
					Player player = (Player) sender;
					lifestoneLoc ls;
					double dist;
					for (int i = 0; i < plugin.lifestoneLocations.size(); i++) {
						ls = plugin.lifestoneLocations.get(i);
						if (player.getWorld().getName().equals(ls.world)){
							dist = player.getLocation().distance(new Location(player.getWorld(), ls.X, ls.Y, ls.Z));
							lifestones.add(new lifestoneDistance(ls.world, ls.X, ls.Y, ls.Z, dist));
						}
					}
					if (lifestones.size() > 0){
					
						compareLifestones comparator = new compareLifestones();
						Collections.sort(lifestones, comparator);
					
						double pX = player.getLocation().getX();
						double pZ = player.getLocation().getZ();
						
						String sDir = MathUtil.DegToDirection(MathUtil.AngleCoordsToCoords(pX, pZ, lifestones.get(0).X, lifestones.get(0).Z));

						plugin.sendMessage(sender, "Nearest lifestone is at " + lifestones.get(0).X + " " + lifestones.get(0).Y + " " + lifestones.get(0).Z + ", " + Math.round(lifestones.get(0).distance) + " blocks " + sDir + ".");

						if (Config.lookAtNearestLS == true){
							Location pLoc = player.getLocation();
							Location lsLoc = new Location(player.getWorld(), lifestones.get(0).X,lifestones.get(0).Y,lifestones.get(0).Z);
	
							float yaw = MathUtil.getLookAtYaw(pLoc, lsLoc) + 90;
							pLoc.setYaw(yaw);
							
							double motX = lifestones.get(0).X - player.getLocation().getX();
							double motY = lifestones.get(0).Y - player.getLocation().getY();
							double motZ = lifestones.get(0).Z - player.getLocation().getZ();
							
							float pitch = MathUtil.getLookAtPitch(motX, motY, motZ);
						
							pLoc.setPitch(pitch);
							
							player.teleport(pLoc);
						}
						
					}else{
						plugin.sendMessage(sender, "There are no lifestones near you.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("list")) {
					if (!hasCommandPermission(sender, "lifestones.list")) {
						return true;
					}
					
					int page = 1;
					if (args.length > 1) {// && args[1].equalsIgnoreCase("compact"))
						if (plugin.isInt(args[1])) {
							page = Math.abs(Integer.parseInt(args[1]));
						} else {
							plugin.sendMessage(sender, "Invalid page number: " + args[1]);
							return true;
						}
					}
					
					
					
					int rows = plugin.lifestoneLocations.size();
					int maxPages = (int) Math.ceil((float) rows / (float) Config.rowsPerPage);

					if (rows > Config.rowsPerPage){
						plugin.sendMessage(sender, "Page " + (page) + "/" + (maxPages));
					}
					
					int start = ((page-1) * Config.rowsPerPage);
					int end = start + Config.rowsPerPage;
					if (end > rows)
						end = rows;
					
					lifestoneLoc ls; 
					for (int i = start; i < end; i++) {
						ls = plugin.lifestoneLocations.get(i);
						plugin.sendMessage(sender, (i+1) + ": " + ls.world + " " + ls.X + " " + ls.Y + " " + ls.Z, false);
					}
					return true;
				} else if (args[0].equalsIgnoreCase("tp")) {
					if (!hasCommandPermission(sender, "lifestones.tp")) {
						return true;
					}
					int lsID;
					if (args.length > 1) {// && args[1].equalsIgnoreCase("compact"))
						if (plugin.isInt(args[1])) {
							lsID = Math.abs(Integer.parseInt(args[1]));
						} else {
							plugin.sendMessage(sender, "Invalid id: " + args[1]);
							return true;
						}
					}else{
						plugin.sendMessage(sender, "Include a id number from the lifestone list.");
						return true;
					}
						
					lsID-=1;
					
					if (lsID > plugin.lifestoneLocations.size()){
						plugin.sendMessage(sender, lsID + " is too high!");
						return true;
					}
					
					lifestoneLoc lsLoc = plugin.lifestoneLocations.get(lsID);
					
					Block lsBlock = plugin.getServer().getWorld(lsLoc.world).getBlockAt(lsLoc.X, lsLoc.Y, lsLoc.Z);
					Block rBlock;
					Player player = (Player) sender;
					for (int y=1; y < (256-lsBlock.getY()); y++){
						rBlock = lsBlock.getRelative(0, y, 0);
						if (rBlock.getTypeId() == 0){
							
							player.teleport(new Location(player.getWorld(), rBlock.getX() + .5, rBlock.getY() + 1, rBlock.getZ() + .5));
							plugin.sendMessage(player, ChatColor.GRAY+"Teleporting to " + ChatColor.GREEN + rBlock.getX() + ChatColor.GRAY+"x" +ChatColor.GREEN + rBlock.getZ() + ChatColor.GRAY+ ".");
							
							return true;
						}
					}
						
					
					
				}
			}
			
			plugin.sendMessage(sender, plugin.pluginName + " v" + plugin.getDescription().getVersion());
			
			if (plugin.hasPermission(sender, "lifestones.recall") && (sender instanceof Player))
				plugin.sendMessage(sender, "§a/lifestone §f- Recall to your lifestone.", true, false);
			
			if (plugin.hasPermission(sender, "lifestones.create") && (sender instanceof Player))
				plugin.sendMessage(sender, "§a/" + commandLabel + " create §a- Create a lifestone at your location.", true, false);
			if (plugin.hasPermission(sender, "lifestones.list") && (sender instanceof Player))
				plugin.sendMessage(sender, "§a/" + commandLabel + " list §a- List all lifestone locations.", true, false);
			if (plugin.hasPermission(sender, "lifestones.tp") && (sender instanceof Player))
				plugin.sendMessage(sender, "§a/" + commandLabel + " tp [#] §a- Teleport to a lifestone.", true, false);
			
			if (plugin.hasPermission(sender, "lifestones.reload") && (sender instanceof Player))
				plugin.sendMessage(sender, "§a/" + commandLabel + " reload §a- Reload the plugin.", true, false);
			if (plugin.hasPermission(sender, "lifestones.randomtp") && (sender instanceof Player))
				plugin.sendMessage(sender, "§a/" + commandLabel + " randomtp §a- Teleport to a random location in the world.", true, false);
			
			return true;
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
		plugin.sendMessage(player, ChatColor.GRAY + "You do not have permission: " + ChatColor.YELLOW + permission);
		return false;
	}

	public class lifestoneDistance extends lifestoneLoc {
		double distance;
		public lifestoneDistance(String world, int X, int Y, int Z, double distance) {
			super(world, X, Y, Z);
			// TODO Auto-generated constructor stub
			this.distance = distance;
		}

	}
	
	/**/
	public class compareLifestones implements Comparator<lifestoneDistance> {

		@Override
		public int compare(lifestoneDistance o1, lifestoneDistance o2) {
			if (o1.distance > o2.distance) {
				return +1;
			} else if (o1.distance < o2.distance) {
				return -1;
			} else {
				return 0;
			}
		}

	}
	

	
}
