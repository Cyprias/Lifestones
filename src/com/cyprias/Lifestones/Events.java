package com.cyprias.Lifestones;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;

import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;
import com.cyprias.Lifestones.VersionChecker.VersionCheckerEvent;

public class Events implements Listener {
	private Lifestones plugin;

	public Events(Lifestones plugin) {
		this.plugin = plugin;
	}

	public void unregisterEvents() {
		BlockBreakEvent.getHandlerList().unregister(this);
		BlockBurnEvent.getHandlerList().unregister(this);
		BlockPistonExtendEvent.getHandlerList().unregister(this);
		BlockPistonRetractEvent.getHandlerList().unregister(this);
		BlockPlaceEvent.getHandlerList().unregister(this);
		EntityChangeBlockEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
		EntityExplodeEvent.getHandlerList().unregister(this);
		PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		PlayerRespawnEvent.getHandlerList().unregister(this);
		VersionCheckerEvent.getHandlerList().unregister(this);
	}

	/*
	 * @EventHandler(priority = EventPriority.NORMAL) public void
	 * onBlockRedstone(BlockRedstoneEvent event) {
	 * plugin.info(event.getEventName()); }
	 */
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

	public class attuneTask implements Runnable {
		Player player;

		int pX, pY, pZ;

		public attuneTask(Player player) {
			this.player = player;

			pX = player.getLocation().getBlockX();
			pY = player.getLocation().getBlockY();
			pZ = player.getLocation().getBlockZ();
			// "Attuning to lifestone in " + Config.attuneDelay +
			// " seconds, move to cancel."
			plugin.sendMessage(player, GRAY + F("attuningToLifestone", GREEN + Config.attuneDelay + GRAY));
		}

		public void run() {
			if (player.getLocation().getBlockX() != pX || player.getLocation().getBlockY() != pY || player.getLocation().getBlockZ() != pZ) {
				plugin.sendMessage(player, GRAY + L("movedTooFarAttunementFailed"));
				return;
			}

			Location pLoc = player.getLocation();
			String pWorld = pLoc.getWorld().getName();
			double pX = pLoc.getX();
			double pY = pLoc.getY();
			double pZ = pLoc.getZ();

			float pYaw = pLoc.getYaw();
			float pPitch = pLoc.getPitch();

			String pName = player.getName();

			Attunements.put(pName, new Attunement(pName, pWorld, pX, pY, pZ, pYaw, pPitch));
			plugin.sendMessage(player, GRAY + L("attunedToLifestone"));

			try {
				Database.saveAttunment(pName, pWorld, pX, pY, pZ, pYaw, pPitch, Config.preferAsyncDBCalls);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Config.allowPerWorldAttunement == false)
				try {
					Database.removeOtherWorldAttunments(pName, pWorld, Config.preferAsyncDBCalls);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		Action action = event.getAction();
		// log.info("action " + action);

		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block cBlock = event.getClickedBlock();
			// log.info("cBlock: " + cBlock.getType());
			if (cBlock.getTypeId() == 77 || cBlock.getTypeId() == 143) {// BUTTON
				if (plugin.isLifestone(cBlock) == true) {
					Player player = event.getPlayer();
					if (!plugin.commands.hasCommandPermission(player, "lifestones.attune")) {
						return;
					}

					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new attuneTask(player), Config.attuneDelay * 20L);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Attunements.defaultAttunement != null) {

			String playerName = event.getPlayer().getName();
			String worldName = plugin.getServer().getWorlds().get(0).getName();
			if ((new File(worldName + "/players/" + playerName + ".dat")).exists() == false) {
				Attunements.put(playerName, new Attunement(playerName, Attunements.defaultAttunement));
				plugin.debug(playerName + " is new to the server, setting their default attunement location.");
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		plugin.debug(event.getEventName());
		if (Attunements.containsKey(event.getPlayer().getName())) {
			Attunement attunement = Attunements.get(event.getPlayer());
			event.setRespawnLocation(attunement.loc);
			plugin.playerProtections.put(event.getPlayer().getName(), Lifestones.getUnixTime() + Config.protectPlayerAfterRecallDuration);
		}
	}

	/*
	 * @EventHandler(priority = EventPriority.NORMAL) public void
	 * onBlockDamage(BlockDamageEvent event) { if (event.isCancelled()) return;
	 * plugin.debug(event.getEventName()); }
	 */

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (event.isCancelled())
			return;
		// plugin.debug(event.getEventName());

		Block block = event.getBlock();
		if (plugin.isProtected(block)) {
			event.setCancelled(true);
			plugin.debug("Blocking enty change block at " + event.getBlock().getWorld().getName() + " " + event.getBlock().getX() + " "
				+ event.getBlock().getY() + " " + event.getBlock().getZ());
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		Block piston = event.getBlock();
		BlockState state = piston.getState();
		MaterialData data = state.getData();
		BlockFace direction = null;

		if (data instanceof PistonBaseMaterial) {
			direction = ((PistonBaseMaterial) data).getFacing();
			Block block = event.getBlock().getRelative(direction);

			if (plugin.isProtected(block)) {
				event.setCancelled(true);
				plugin.debug("Blocking piston extend at " + event.getBlock().getWorld().getName() + " " + event.getBlock().getX() + " "
					+ event.getBlock().getY() + " " + event.getBlock().getZ());

				return;
			}
		}

		// if no direction was found, no point in going on
		if (direction == null) {
			return;
		}

		for (int i = 0; i < event.getLength() + 2; i++) {
			Block block = piston.getRelative(direction, i);

			// We don't want that!
			if (block.getType() == Material.AIR) {
				break;
			}

			if (plugin.isProtected(block)) {
				event.setCancelled(true);
				plugin.debug("Blocking piston extend at " + block.getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
				break;
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled())
			return;
		// plugin.debug(event.getEventName());
		Block block = event.getBlock();
		if (plugin.isProtected(block)) {
			plugin.debug("Blocking block burn at " + block.getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled())
			return;
		// plugin.debug(event.getEventName());

		for (Block block : event.blockList()) {
			if (plugin.isProtected(block)) {
				plugin.debug("Blocking explostion at " + block.getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;

		Block piston = event.getBlock();
		BlockState state = piston.getState();
		MaterialData data = state.getData();
		BlockFace direction = null;

		// Check the block it pushed directly
		if (data instanceof PistonBaseMaterial) {
			direction = ((PistonBaseMaterial) data).getFacing();
		}

		if (direction == null) {
			return;
		}

		// the block that the piston moved
		Block moved = piston.getRelative(direction, 2);

		if (plugin.isProtected(moved)) {
			plugin.debug("Blocking piston retract at " + event.getBlock().getWorld().getName() + " " + event.getBlock().getX() + " " + event.getBlock().getY()
				+ " " + event.getBlock().getZ());
			event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) throws SQLException {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();

		Block block = event.getBlock();
		if (plugin.isLifestone(block) == true) {

			if (!(player.hasPermission("lifestones.breaklifestone"))) {
				plugin.sendMessage(player, GRAY + L("cannotModifyLifestone"));
				event.setCancelled(true);
				return;
			}

			//

			// plugin.isLifestoneCache.get(block)
			Block cBlock = plugin.getLifestoneCenterBlock(block);

			Database.removeLifestone(cBlock.getWorld().getName(), cBlock.getX(), cBlock.getY(), cBlock.getZ(), Config.preferAsyncDBCalls);
			plugin.unregsterLifestone(event.getPlayer(), new lifestoneLoc(cBlock.getWorld().getName(), cBlock.getX(), cBlock.getY(), cBlock.getZ()));

			plugin.sendMessage(player, GRAY + L("lifestoneUnregistered"));

			event.setCancelled(true);// Stop the block from falling, our
										// unregister function should air the
										// block.

		} else if (plugin.isProtected(block) == true) {
			if (!(player.hasPermission("lifestones.modifyprotectedblocks"))) {
				plugin.sendMessage(player, GRAY + L("blockProtectedByLifestone"));
				event.setCancelled(true);
				return;
			}

		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		Block block = event.getBlock();
		if (plugin.isLifestone(block) == true) {

			if (!(player.hasPermission("lifestones.breaklifestone"))) {
				plugin.sendMessage(player, GRAY + L("cannotModifyLifestone"));
				event.setCancelled(true);
				return;
			}

		} else if (plugin.isProtected(block) == true) {
			if (!(player.hasPermission("lifestones.modifyprotectedblocks"))) {
				plugin.sendMessage(player, GRAY + L("blockProtectedByLifestone"));
				event.setCancelled(true);
				return;
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVersionChecker(VersionCheckerEvent event) {
		if (event.getPluginName() == plugin.getName()) {
			VersionChecker.versionInfo info = event.getVersionInfo(0);
			String curVersion = plugin.getDescription().getVersion();
			int compare = VersionChecker.compareVersions(curVersion, info.getTitle());
			if (compare < 0) {
				plugin.info("We're running " + plugin.getName() + " v" + curVersion + ", v" + info.getTitle() + " is available");
				plugin.info(info.getLink());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;

		if (event.getEntity().getType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();

			if (plugin.playerProtections.containsKey(player.getName())) {
				Double recalled = plugin.playerProtections.get(player.getName());

				if (recalled >= Lifestones.getUnixTime()) {
					plugin.debug("Protecting recalled player " + player.getName());
					plugin.sendMessage(player, GRAY + L("playerProtectedByLifestone"));
					
					event.setCancelled(true);
					return;
				}

			}

		}else if (event.getDamager().getType() == EntityType.PLAYER){
			Player player = (Player) event.getDamager();
			if (plugin.playerProtections.containsKey(player.getName())) 
				plugin.playerProtections.remove(player.getName());
			
		}
		
		
		

	}

	public static HashMap<String, String> aliases = new HashMap<String, String>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage();
		String command = msg.split(" ")[0].replace("/", "");

		if (aliases.containsKey(command.toLowerCase())) {
			event.setMessage(msg.replaceFirst("/" + command, "/" + aliases.get(command.toLowerCase())));
			return;
		}
	}
}
