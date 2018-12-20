package Main;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerRightClickVillager implements Listener
{
	@EventHandler
	public void onPlayerClick(PlayerInteractEntityEvent event)
	{
		if(Main.mapmanager.isPlayerPlaying(event.getPlayer()))
		{
			if(event.getRightClicked() instanceof Villager)//so players can't open trade gui
			{
				event.setCancelled(true);
			}
		}
	}
}
