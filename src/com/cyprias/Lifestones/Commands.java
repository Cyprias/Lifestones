package com.cyprias.Lifestones;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Config.lifestoneStructure;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;

public class Commands implements CommandExecutor {
	private Lifestones plugin;

	public Commands(Lifestones plugin) {
		this.plugin = plugin;
	}

	static public String L(String key) {
		return Lifestones.L(key);
	}
	static public String F(String key, Object... args) {
		return Lifestones.F(key, args);
	}
	
	String GREEN = ChatColor.GREEN.toString();
	String RESET = ChatColor.RESET.toString();
	String GRAY = ChatColor.GRAY.toString();
	String YELLOW = ChatColor.YELLOW.toString();
	
	public class recallTask implements Runnable {
		Player player;

		int pX, pY, pZ;

		public recallTask(Player player) {
			this.player = player;

			pX = player.getLocation().getBlockX();
			pY = player.getLocation().getBlockY();
			pZ = player.getLocation().getBlockZ();

			plugin.sendMessage(player,GRAY+F("recallingToLifestone", GREEN + Config.recallDelay + GRAY));
		}

		public void run() {
			if (player.getLocation().getBlockX() != pX || player.getLocation().getBlockY() != pY || player.getLocation().getBlockZ() != pZ) {
				plugin.sendMessage(player,GRAY+F("recallingToLifestone", GREEN + Config.recallDelay + GRAY));
				//plugin.sendMessage(player, GRAY+L("movedTooFarAttunementFailed"));
				return;
			}

			Attunement attunement = Attunements.get(player);

			player.teleport(attunement.loc);
			plugin.sendMessage(player, GRAY+L("recalledToLifestone"));
			
			
			plugin.playerProtections.put(player.getName(), Lifestones.getUnixTime() + Config.protectPlayerAfterRecallDuration);
			//protectPlayerAfterRecallDuration
		}
	}

	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// final String message = getFinalArg(args, 0);
		// plugin.info(sender.getName() + ": /" + cmd.getName() + " " +
		// message);

		if (commandLabel.equals("lifestone")) {
			if (args.length > 0) {
				return onCommand(sender, cmd, "lifestones", args);
			}
			if (!hasCommandPermission(sender, "lifestones.recall")) {
				return true;
			}
			
			
			
			if (!(Attunements.containsKey(sender.getName()))) {
				plugin.sendMessage(sender, GRAY+L("notAttunedYet"));
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
						plugin.sendMessage(sender, GRAY+L("tooCloseToAnotherLifestone"));
						return true;
					}

					Block rBlock;

					lifestoneStructure lsStructure;
					BlockPlaceEvent e;

					if (Config.callBlockPlaceEvent == true){
						for (int i = 0; i < Config.structureBlocks.size(); i++) {
							lsStructure = Config.structureBlocks.get(i);
							rBlock = pBlock.getRelative(lsStructure.rX, lsStructure.rY, lsStructure.rZ);
							//don't change the block type, try placing the blocks down again to check if user has permission in the area to build. 
							e = new BlockPlaceEvent(rBlock, rBlock.getState(), pBlock, player.getItemInHand(), player, false);
							player.getServer().getPluginManager().callEvent(e);
							
							
							if (e.isCancelled()){
								plugin.sendMessage(sender, GRAY+L("anotherPluginBlockingCreation"));
								return true;
							}
						}
					}
					for (int i = 0; i < Config.structureBlocks.size(); i++) {
						lsStructure = Config.structureBlocks.get(i);
						rBlock = pBlock.getRelative(lsStructure.rX, lsStructure.rY, lsStructure.rZ);

						
						if (Config.callBlockPlaceEvent == true){
							e = new BlockPlaceEvent(rBlock, rBlock.getState(), pBlock, player.getItemInHand(), player, false);
							e.getBlock().setTypeId(lsStructure.bID);
							e.getBlock().setData(lsStructure.bData);

							player.getServer().getPluginManager().callEvent(e);
						}else{
							rBlock.setTypeId(lsStructure.bID);
							rBlock.setData(lsStructure.bData);
						}
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
					try {
						Database.saveLifestone(pBlock.getWorld().getName(), pBlock.getX(), pBlock.getY(), pBlock.getZ(), Config.preferAsyncDBCalls);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					plugin.sendMessage(sender, GRAY+L("lifestoneCreated"));
					
					return true;
				}	else if (args[0].equalsIgnoreCase("reload")) {
					if (!hasCommandPermission(sender, "lifestones.reload")) {
						return true;
					}
					
					plugin.getPluginLoader().disablePlugin(plugin);
					plugin.getPluginLoader().enablePlugin(plugin);

					plugin.sendMessage(sender, GRAY+L("pluginReloaded"));
					return true;
				} else if (args[0].equalsIgnoreCase("randomtp")) {
					if (!hasCommandPermission(sender, "lifestones.randomtp")) {
						return true;
					}
					Player player = (Player) sender;
					
					Location tpLoc = plugin.getRandomLocation(player.getWorld());
					
					if (tpLoc != null){
						player.teleport(tpLoc);
						plugin.sendMessage(sender, GRAY+F("teleportingToCoordinates", GREEN + tpLoc.getBlockX() + GRAY, GREEN + tpLoc.getBlockY() + GRAY, GREEN + tpLoc.getBlockZ() + GRAY));
						
						return true;
					}else{
						plugin.sendMessage(player, GRAY + L("cantFindSafeBlock"));
						
						return true;
					}
				} else if (args[0].equalsIgnoreCase("release")) {
					if (!hasCommandPermission(sender, "lifestones.release")) {
						return true;
					}
					
					
					if (Attunements.containsKey(sender.getName())) {
						Attunement attunement = Attunements.get(sender.getName(), ((Player) sender).getWorld().getName());
						

						
						
						
						try {
							String worldName = attunement.loc.getWorld().getName();
							if (attunement.remove()){
								plugin.sendMessage(sender, GRAY + F("releasedAttunement", ChatColor.WHITE+ worldName +GRAY));
							}
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							plugin.sendMessage(sender, "Error: " + e.getMessage());
							e.printStackTrace();
						}
						
	
						return true;
						//event.setRespawnLocation(attunement.loc);
						//plugin.playerProtections.put(event.getPlayer().getName(), Lifestones.getUnixTime() + Config.protectPlayerAfterRecallDuration);
					}
					
				
					plugin.sendMessage(sender, GRAY + L("notAttunedYet"));
				
					
					return true;
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

						//plugin.sendMessage(sender, "Nearest lifestone is at " + lifestones.get(0).X + " " + lifestones.get(0).Y + " " + lifestones.get(0).Z + ", " + Math.round(lifestones.get(0).distance) + " blocks " + sDir + ".");
						plugin.sendMessage(player, GRAY + F("nearestLifestoneAt", GREEN + lifestones.get(0).X + GRAY, GREEN + lifestones.get(0).Y + GRAY, GREEN + lifestones.get(0).Z + GRAY, GREEN + Math.round(lifestones.get(0).distance) + GRAY, GREEN + sDir + GRAY));
						
						
						if (Config.lookAtNearestLS == true){
							Location pLoc = player.getLocation();
							Location lsLoc = new Location(player.getWorld(), lifestones.get(0).X + 0.5,lifestones.get(0).Y,lifestones.get(0).Z + 0.5);
	
							float yaw = MathUtil.getLookAtYaw(pLoc, lsLoc) + 90;
							pLoc.setYaw(yaw);
							
							double motX = (lifestones.get(0).X + 0.5) - player.getLocation().getX();
							double motY = (lifestones.get(0).Y) - player.getLocation().getY();
							double motZ = (lifestones.get(0).Z + 0.5) - player.getLocation().getZ();
							
							float pitch = MathUtil.getLookAtPitch(motX, motY, motZ);
						
							pLoc.setPitch(pitch);
							
							player.teleport(pLoc);
						}
						
					}else{
						plugin.sendMessage(sender, GRAY + L("noLifestoneNear"));
					}
					return true;
				} else if (args[0].equalsIgnoreCase("list")) {
					if (!hasCommandPermission(sender, "lifestones.list")) {
						return true;
					}
					
					int page = 1;
					if (args.length > 1) {// && args[1].equalsIgnoreCase("compact"))
						if (Lifestones.isInt(args[1])) {
							page = Math.abs(Integer.parseInt(args[1]));
						} else {
							plugin.sendMessage(sender, GRAY + F("invalidPageNumber", args[1]));
							return true;
						}
					}
					
					
					
					int rows = plugin.lifestoneLocations.size();
					
					if (rows == 0 ){
						plugin.sendMessage(sender, GRAY + L("noRegisteredLifestones"));
						return true;
					}
					
					int maxPages = (int) Math.ceil((float) rows / (float) Config.rowsPerPage);

					if (rows > Config.rowsPerPage){
						//plugin.sendMessage(sender, "Page " + (page) + "/" + (maxPages));
						plugin.sendMessage(sender, GRAY+F("page", GREEN+page+GRAY, GREEN+maxPages+GRAY));
					}
					
					int start = ((page-1) * Config.rowsPerPage);
					int end = start + Config.rowsPerPage;
					if (end > rows)
						end = rows;
					
					lifestoneLoc ls; 
					for (int i = start; i < end; i++) {
						ls = plugin.lifestoneLocations.get(i);
						plugin.sendMessage(sender, GRAY+F("lifestoneIndex", GREEN+(i+1)+GRAY, GREEN+ls.world+GRAY, GREEN+ls.X+GRAY, GREEN+ls.Y+GRAY, GREEN+ls.Z+GRAY), false);

					}
					return true;
				} else if (args[0].equalsIgnoreCase("tp")) {
					if (!hasCommandPermission(sender, "lifestones.tp")) {
						return true;
					}
					int lsID;
					if (args.length > 1) {// && args[1].equalsIgnoreCase("compact"))
						if (Lifestones.isInt(args[1])) {
							lsID = Math.abs(Integer.parseInt(args[1]));
						} else {
							plugin.sendMessage(sender, GRAY+F("invalidID",args[1]));
							return true;
						}
					}else{
						plugin.sendMessage(sender, GRAY+L("includeIndexNum"));
						return true;
					}
					if (lsID<=0){
						plugin.sendMessage(sender, GRAY+F("invalidID",args[1]));
						return true;
					}
					lsID-=1;
					if (lsID > plugin.lifestoneLocations.size()){
						plugin.sendMessage(sender, GRAY+F("indexTooHigh",GREEN+lsID+GRAY));
						return true;
					}
					
					lifestoneLoc lsLoc = plugin.lifestoneLocations.get(lsID);
					
					World lsWorld = plugin.getServer().getWorld(lsLoc.world);
					
					Block lsBlock = lsWorld.getBlockAt(lsLoc.X, lsLoc.Y, lsLoc.Z);
					Block rBlock;
					Player player = (Player) sender;
					for (int y=1; y < (256-lsBlock.getY()); y++){
						rBlock = lsBlock.getRelative(0, y, 0);
						if (rBlock.getTypeId() == 0){
							
							player.teleport(new Location(lsWorld, rBlock.getX() + .5, rBlock.getY() + 1, rBlock.getZ() + .5));
							//plugin.sendMessage(player, ChatColor.GRAY+"Teleporting to " + ChatColor.GREEN + rBlock.getX() + ChatColor.GRAY+"x" +ChatColor.GREEN + rBlock.getZ() + ChatColor.GRAY+ ".");

							plugin.sendMessage(sender, GRAY+F("teleportingToCoordinates", GREEN + rBlock.getX() + GRAY, GREEN + rBlock.getY() + GRAY, GREEN + rBlock.getZ() + GRAY));
							
							
							return true;
						}
					}
						
					
				} else if (args[0].equalsIgnoreCase("attunement")) {
					if (!hasCommandPermission(sender, "lifestones.attunement")) {
						return true;
					}
					
					if (args.length > 1){
						if (args[1].equalsIgnoreCase("setdefault")) {
							if (!hasCommandPermission(sender, "lifestones.attunement.setdefault"))
								return true;
							
							if (Attunements.containsKey(sender.getName())) {
								Attunement attunement = Attunements.get((Player) sender);
								//event.setRespawnLocation(attunement.loc);
								
								//attunement.loc.getWorld().getName()
								
								Config.saveDefaultAttunement(attunement.loc);
								Attunements.defaultAttunement = attunement.loc;
								plugin.sendMessage(sender, L("defaultAttunementSet"));
							}else{
								plugin.sendMessage(sender, L("setYourAttunementFirst"));
								
							}
							return true;
							
						}else if (args[1].equalsIgnoreCase("list")) {
							if (!hasCommandPermission(sender, "lifestones.attunement.list"))
								return true;
							
							Iterator<List<Attunement>> vals = Attunements.players.values().iterator();
							
							List<List<Attunement>> names = new ArrayList<List<Attunement>>();
							while(vals.hasNext()){
								names.add(vals.next());
							}
							
							compareAttunementNames comparator = new compareAttunementNames();
							Collections.sort(names, comparator);
							
							List<Attunement> aList;
							Attunement att;
							String worlds;
							for (int i=0; i< names.size(); i++){
								aList = names.get(i);
								worlds = "";
								for (int w=0; w< aList.size(); w++){
									att =  aList.get(w);
									worlds += att.loc.getWorld().getName() + "("+att.loc.getBlockX()+","+att.loc.getBlockZ()+") ";
								}
								plugin.sendMessage(sender,aList.get(0).player + ": " + worlds, false);
							}

							return true;
						}else if (args[1].equalsIgnoreCase("tp")) {
							if (!hasCommandPermission(sender, "lifestones.attunement.tp"))
								return true;
							
							if (args.length > 2){
								String who = args[2];
								String world = plugin.getServer().getWorlds().get(0).getName();
								if (args.length > 3 && plugin.getServer().getWorld(args[3]) != null){
									world = plugin.getServer().getWorld(args[3]).getName();
								}
								
								Attunement att = Attunements.get(who, world);
								if (att != null){
									((Player) sender).teleport(att.loc);
									plugin.sendMessage(sender, GRAY+F("tpingToAttunement", GREEN+who+GRAY, GREEN+world+GRAY));
								}else{
									plugin.sendMessage(sender, GRAY+F("cannotFindAttunement", GREEN+who+GRAY));
								}
								
								return true;
							}
							
							plugin.sendMessage(sender, GREEN+"/"+commandLabel+" "+args[0] +" tp <who> [world]"+ GRAY+" - " + L("tpToAttunement"), true, false);
							return true;
						}
					}
					
					
					if (sender.hasPermission("lifestones.attunement.setdefault") && (sender instanceof Player))
						plugin.sendMessage(sender, GREEN+"/"+commandLabel+" "+args[0] +" setdefault"+ GRAY+" - " + L("setDefaultAttunementDesc"), true, false);
					if (sender.hasPermission("lifestones.attunement.list") && (sender instanceof Player))
						plugin.sendMessage(sender, GREEN+"/"+commandLabel+" "+args[0] +" list"+ GRAY+" - " + L("listAllAttunements"), true, false);
					if (sender.hasPermission("lifestones.attunement.tp") && (sender instanceof Player))
						plugin.sendMessage(sender, GREEN+"/"+commandLabel+" "+args[0] +" tp <who> [world]"+ GRAY+" - " + L("tpToAttunement"), true, false);
					
					
					
					
					return true;
				}
			}
			
			plugin.sendMessage(sender, F("nameAndVersion", GREEN+Lifestones.pluginName +GRAY, GREEN+plugin.getDescription().getVersion()+GRAY));

			if (sender.hasPermission("lifestones.recall") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+"" + GRAY+" - " + L("lifestoneDesc"), true, false);
			
			
			
			if (sender.hasPermission("lifestones.create") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" create" + GRAY+" - " + L("createALifestone"), true, false);
			
			if (sender.hasPermission("lifestones.list") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" list" + GRAY+" - " + L("lifeAllLifestones"), true, false);
			
			if (sender.hasPermission("lifestones.tp") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" tp [#]" + GRAY+" - " + L("tpToLifestone"), true, false);
			
			if (sender.hasPermission("lifestones.near") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" near" + GRAY+" - " + L("showNearestLifestone"), true, false);
			
			if (sender.hasPermission("lifestones.reload") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" reload" + GRAY+" - " + L("reloadThePlugin"), true, false);
			
			if (sender.hasPermission("lifestones.randomtp") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" randomtp" + GRAY+" - " + L("tpToRandomLoc"), true, false);
			
			if (sender.hasPermission("lifestones.attunement") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" attunement" + GRAY+" - " + L("attunementCommands"), true, false);
			
			if (sender.hasPermission("lifestones.release") && (sender instanceof Player))
				plugin.sendMessage(sender, GREEN+"/"+commandLabel+" release" + GRAY+" - " + L("releaseCommand"), true, false);
			//
			
			return true;
		}

		return false;
	}
	

	
	public boolean hasCommandPermission(CommandSender player, String permission) {
		if (player.hasPermission(permission)) {
			return true;
		}
		plugin.sendMessage(player, ChatColor.GRAY +F("noPermission", YELLOW+permission+GRAY));
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
	public class compareAttunementNames implements Comparator<List<Attunement>> {

		@Override
		public int compare(List<Attunement> o1, List<Attunement> o2) {
			return o1.get(0).player.toLowerCase().compareTo(o2.get(0).player.toLowerCase());
		}

	}

	
}
