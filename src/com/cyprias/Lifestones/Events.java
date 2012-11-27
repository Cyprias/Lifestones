package com.cyprias.Lifestones;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.cyprias.Lifestones.Attunements.Attunement;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;


public class Events implements Listener {
	private Lifestones plugin;
	public Events(Lifestones plugin) {
		this.plugin = plugin;
	}
	
	public Boolean isLifestoneButton(Block button){
		if (true)
			return true;
		
		return false;
	}
	
	
	/*@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRedstone(BlockRedstoneEvent event) {
		plugin.info(event.getEventName());
	}*/
	
	private Logger log = Logger.getLogger("Minecraft");
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		//plugin.info(event.getEventName());
		
		Action action = event.getAction();
		//log.info("action " + action);
		
		if (action.equals(action.RIGHT_CLICK_BLOCK)){
			Block cBlock = event.getClickedBlock();
			//log.info("cBlock: " + cBlock.getType());
			if (cBlock.getTypeId() == 77 || cBlock.getTypeId() == 143){//BUTTON
				if (plugin.isLifestone(cBlock) == true){
					Player player = event.getPlayer();
					//plugin.sendMessage(player,  "You hit a LS button!");
					
					Location pLoc = player.getLocation();
					String pWorld = pLoc.getWorld().getName();
					double pX = pLoc.getX();
					double pY = pLoc.getY();
					double pZ = pLoc.getZ();
					
					float pYaw = pLoc.getYaw();
					float pPitch = pLoc.getPitch();
					
					
					String pName = player.getName();
					
					
					Attunements.players.put(pName, new Attunement(pName, pWorld, pX, pY, pZ, pYaw, pPitch));
					plugin.sendMessage(player, "Attuned to lifestone");
					
					plugin.database.saveAttunment(pName, pWorld, pX, pY, pZ, pYaw, pPitch, Config.preferAsyncDBCalls);
					
					
					
				}
			}
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		plugin.info(event.getEventName());
		if (Attunements.players.containsKey(event.getPlayer().getName())){
			Attunement attunement = Attunements.players.get(event.getPlayer().getName());
			Location loc = new Location(plugin.getServer().getWorld(attunement.world), attunement.x, attunement.y, attunement.z, attunement.yaw, attunement.pitch);
			event.setRespawnLocation(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		Block block = event.getBlock();
		if (plugin.isLifestone(block) == true){
			
			if (!(plugin.hasPermission(player, "lifestones.breaklifestone"))){
				plugin.sendMessage(player, "You cannot modify the lifestone.");
				event.setCancelled(true);
				return;
			}
			
			//
			
			//plugin.isLifestoneCache.get(block)
			Block cBlock = plugin.getLifestoneCenterBlock(block);
			
			plugin.database.removeLifestone(cBlock.getWorld().getName(), cBlock.getX(), cBlock.getY(), cBlock.getZ(), Config.preferAsyncDBCalls);
			plugin.unregsterLifestone(new lifestoneLoc(cBlock.getWorld().getName(), cBlock.getX(), cBlock.getY(), cBlock.getZ()));
			
			plugin.sendMessage(player, "Lifestone unregistered.");
			
			
			event.setCancelled(true);//Stop the block from falling, our unregister function should air the block.
			
			
		}else if (plugin.isProtected(block) == true){
			if (!(plugin.hasPermission(player, "lifestones.modifyprotectedblocks"))){
				plugin.sendMessage(player, "That block is protected by the lifestone.");
				event.setCancelled(true);
				return;
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		Block block = event.getBlock();
		if (plugin.isLifestone(block) == true){
			
			if (!(plugin.hasPermission(player, "lifestones.breaklifestone"))){
				plugin.sendMessage(player, "You cannot modify the lifestone.");
				event.setCancelled(true);
				return;
			}
			
			
		}else if (plugin.isProtected(block) == true){
			if (!(plugin.hasPermission(player, "lifestones.modifyprotectedblocks"))){
				plugin.sendMessage(player, "That block is protected by the lifestone.");
				event.setCancelled(true);
				return;
			}
		}
		
	}
}
