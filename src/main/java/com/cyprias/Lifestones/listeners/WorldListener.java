package com.cyprias.Lifestones.listeners;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

import com.cyprias.Lifestones.Commands;
import com.cyprias.Lifestones.Config;
import com.cyprias.Lifestones.Database;
import com.cyprias.Lifestones.Config.lifestoneStructure;
import com.cyprias.Lifestones.Lifestones.lifestoneLoc;
import com.cyprias.Lifestones.Lifestones;

public class WorldListener implements Listener {

	@EventHandler
	public void onChunkPopulateEvent(ChunkPopulateEvent e)
	{
		Chunk c = e.getChunk();
		List<String> genWorlds = Config.getStringList("generate-lifestone-worlds");
		
		for (int i=0; i<genWorlds.size(); i++)
		{
			if (genWorlds.get(i).equalsIgnoreCase(c.getWorld().getName()))
			{
				break;
			}
			else if (i == (genWorlds.size() -1))
			{
				return;
			}
		}

		Lifestones.debug("<ChunkPopulateEvent> " + c.getWorld() + " "  + c.getX() + " "  + c.getZ());
		
		Double chance = Config.getDouble("generate-chunk-change");
		double random = Math.random();
		if (random <= chance)
		{
			
			Block mBlock = c.getBlock(8, 1, 8);
			Block sBlock = Lifestones.getSurfaceBlock(mBlock.getWorld(), (int) mBlock.getX(), (int) mBlock.getZ());
			if (sBlock == null)
				return;

			// Go up one block so our lifestone isn't in the ground.
			sBlock = sBlock.getRelative(0,1,0);
			
			Lifestones.info("Generating a lifestone at " + sBlock.getWorld().getName() + " " + sBlock.getX() + " "  + sBlock.getY() + " " + sBlock.getZ());
			
			lifestoneStructure lsStructure;
			Block rBlock;
			
			for (int i = 0; i < Config.structureBlocks.size(); i++) {
				lsStructure = Config.structureBlocks.get(i);
				rBlock = sBlock.getRelative(lsStructure.rX, lsStructure.rY, lsStructure.rZ);

				rBlock.setTypeId(lsStructure.bID);
				rBlock.setData(lsStructure.bData);
			}
			
			Lifestones.regsterLifestone(new lifestoneLoc(sBlock.getWorld().getName(), sBlock.getX(), sBlock.getY(), sBlock.getZ()));
			try {
				Database.saveLifestone(sBlock.getWorld().getName(), sBlock.getX(), sBlock.getY(), sBlock.getZ(), Config.preferAsyncDBCalls);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	

}
