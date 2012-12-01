package com.cyprias.Lifestones;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Server;

public class Attunements {
	static private Server server;
	public Attunements(Server server){
		this.server = server;
	}
	
	private static HashMap<String, Attunement> players = new HashMap<String, Attunement>();
	static public class Attunement{
		String player;
		Location loc;
		public Attunement(String player, String world, double x, double y, double z,  float yaw, float pitch){
			this.player = player;
			this.loc = new Location(server.getWorld(world), x, y, z, yaw, pitch);
		}
	}
	
	public static Attunement get(String key){
		return players.get(key);
	}
	public static Attunement put(String key, Attunement value){
		return players.put(key, value);
	}
	public static boolean containsKey(String key){
		return players.containsKey(key);
	}
	
	
}
