package com.cyprias.Lifestones;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;


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
		plugin.info(event.getEventName());
		
		Action action = event.getAction();
		//log.info("action " + action);
		
		if (action.equals(action.RIGHT_CLICK_BLOCK)){
			Block cBlock = event.getClickedBlock();
			//log.info("cBlock: " + cBlock.getType());
			if (cBlock.getTypeId() == 77){//BUTTON
				if (plugin.isLifestone(cBlock) == true){
					event.getPlayer().sendMessage("You hit a LS button!");

					
					
					
				}
			}
		}
	}
	
	
	
}
