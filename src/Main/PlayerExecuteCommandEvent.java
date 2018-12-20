package Main;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerExecuteCommandEvent implements Listener
{
	@EventHandler
	public void onPlayerExecuteCommand(PlayerCommandPreprocessEvent event)
	{
		for(Map map : Main.mapmanager.getMaps())
		{
			if(map.contains(event.getPlayer()))
			{
				if(map.getCommandsCantRunWhenPlaying().contains(event.getMessage()))
				{
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You can't execute the " + event.getMessage() + " command when playing Agario!");
				}
			}
		}
	}
}
