package me.coolmann24.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItem implements Listener
{
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if(Main.mapmanager.isPlayerPlaying(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
}
