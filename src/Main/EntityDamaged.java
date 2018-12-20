package Main;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamaged implements Listener
{
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		Entity entity = event.getEntity();
		for(Map map : Main.mapmanager.getMaps())
		{
			for(AgarPlayer player : map.getPlayers())
			{
				for(Cell cell : player.getCells())
				{
					if(cell.getEntity().equals(entity)) 
					{
						event.setCancelled(true);
						return;
					}
				}
			}
			for(GreenVirus virus : map.getGreenViruses())
			{
				if(virus.getSlime().equals(entity))
				{
					event.setCancelled(true);
					return;
				}
			}
			for(RedVirus virus : map.getRedViruses())
			{
				if(virus.getMagma().equals(entity))
				{
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
