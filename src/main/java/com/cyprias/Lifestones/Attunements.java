package com.cyprias.Lifestones;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Attunements {
	static private Server server;
	public Attunements(Server server){
		Attunements.server = server;
	}
	
	
	
	//private static HashMap<String, Attunement> players = new HashMap<String, Attunement>();
	public static HashMap<String, List <Attunement>> players = new HashMap<String, List <Attunement>>();

	static Location defaultAttunement = null;
	
	public static void setDefaultAttunement(String world, double x, double y, double z,  float yaw, float pitch){
		defaultAttunement = new Location(server.getWorld(world), x, y, z, yaw, pitch);
	}
	
	static public class Attunement{
		String player;
		Location loc;
		public Attunement(String player, String world, double x, double y, double z,  float yaw, float pitch){
			this.player = player;
			this.loc = new Location(server.getWorld(world), x, y, z, yaw, pitch);
		}
		public Attunement(String player, Location loc){
			this.player = player;
			this.loc = loc;
		}
		
		public Boolean remove() throws SQLException{
			int s = Database.removeAttunment(player, loc.getWorld().getName());
			
			if (s > 0){
				for (int i=players.get(player).size()-1;i>=0;i--)
					if (players.get(player).get(i).equals(this))
						players.get(player).remove(i);

				if (players.get(player).size() == 0)
					players.remove(player);
				
				return true;
			}
			return false;
		}
		
	}
	
	public static void onEnable(){
		players.clear();
	}
	
	public static Attunement get(String key, String worldName){
		return get2(key, server.getWorld(worldName));
	}
	
	public static Attunement get2(String key, World world){
		//return players.get(key);
		if (players.containsKey(key)){
			for (int i=0;i<players.get(key).size();i++){
				if ((players.get(key).get(i).loc.getWorld() != null && players.get(key).get(i).loc.getWorld().equals(world)) || (Config.fallbackToOtherWorldLifestone == true && i == (players.get(key).size()-1)))
					return players.get(key).get(i);
			}
		}

		return null;
	}
	
	public static Attunement get(Player player){
		return get2(player.getName(), player.getWorld());
	}
	
	public static void put(String key, Attunement value){
		if (!players.containsKey(key)){
			players.put(key, new ArrayList<Attunement>());
		}

		if (Config.allowPerWorldAttunement == true){
			for (int i=0;i<players.get(key).size();i++){
				if (players.get(key).get(i).loc.getWorld() != null && players.get(key).get(i).loc.getWorld().equals(value.loc.getWorld())){
					players.get(key).set(i, value);
					return;
				}
			}
		
			players.get(key).add(value);
		}else{
			players.get(key).clear();
			players.get(key).add(value);
		}
		
		//return players.put(key, value);
	}
	public static boolean containsKey2(String key){
		return players.containsKey(key);
	}
	
	public static boolean containsKey(Player player){
		return containsKey2(player.getName(), player.getWorld().getName());
	}
	
	public static boolean containsKey2(String key, String worldName){
		if (players.containsKey(key)){
			for (int i=0;i<players.get(key).size();i++){
				if (players.get(key).get(i).loc.getWorld().getName().equalsIgnoreCase(worldName))
					return true;
				
			}
		}
		return false;
	}
	
	
}
