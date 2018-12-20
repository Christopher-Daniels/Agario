package me.coolmann24.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener
{
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		if(Main.mapmanager.isPlayerPlaying(p))
		{
			Map playersmap = Main.mapmanager.getPlayersMap(p);
			AgarPlayer agarplayer = playersmap.getAgarPlayer(p);
			
			p.teleport(playersmap.getSpectatorSpawn());
			p.setWalkSpeed((float).2);
			p.setFoodLevel(20);
			p.setLevel(0);
			p.getInventory().clear();
			Main.mapmanager.restorePlayerInventory(p);
			p.setScoreboard(Main.mapmanager.getBlankScoreboard());
			
			for(int i = agarplayer.getCells().size() - 1; i >= 0; i--)
			{
				Cell c = agarplayer.getCells().get(i);
				if(!c.isCellPlayer())
				{
					c.getEntity().remove();
				}
				agarplayer.getCells().remove(i);
			}
			playersmap.getPlayers().remove(agarplayer);
		}
	}
}
