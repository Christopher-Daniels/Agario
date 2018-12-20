package me.coolmann24.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChange implements Listener
{
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event)
	{
		if(!(event.getEntity() instanceof Player)) return;
		if(Main.mapmanager.isPlayerPlaying((Player)event.getEntity()))
		{
			event.setCancelled(true);
		}
	}
}
