package com.cyprias.Lifestones;

import java.util.HashMap;

public class Attunements {
	
	
	static HashMap<String, Attunement> players = new HashMap<String, Attunement>();
	
	static public class Attunement{
		String player;
		String world;
		double x, y, z;
		
		float pitch, yaw;
		
		public Attunement(String player, String world, double x, double y, double z, float pitch, float yaw){
			this.player = player;
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.pitch = pitch;
			this.yaw = yaw;
		}
		
	}
	
	
	
}
