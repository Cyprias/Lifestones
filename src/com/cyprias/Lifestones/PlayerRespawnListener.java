package com.cyprias.Lifestones;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.cyprias.Lifestones.Attunements.Attunement;

public class PlayerRespawnListener implements Listener {

	public static void main(String[] args) {}

	
	@EventHandler
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		Lifestones.debug(event.getEventName());
		if (Attunements.containsKey(event.getPlayer())) {
			Attunement attunement = Attunements.get(event.getPlayer());
			if (attunement == null)
				return;

			event.setRespawnLocation(attunement.loc);
			Lifestones.playerProtections.put(event.getPlayer().getName(), Lifestones.getUnixTime() + Config.protectPlayerAfterRecallDuration);
		}
	}
	
	
}
