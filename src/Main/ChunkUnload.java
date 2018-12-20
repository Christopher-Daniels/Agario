package Main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnload implements Listener
{
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event)
	{
		if(Main.mapmanager.chunkInUse(event.getChunk()))
		{
			event.setCancelled(true);
		}
	}
}
