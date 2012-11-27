package com.cyprias.Lifestones;

import java.util.logging.Logger;

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
					plugin.sendMessage(event.getPlayer(), "You hit a LS button!");
					
				}
			}
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
