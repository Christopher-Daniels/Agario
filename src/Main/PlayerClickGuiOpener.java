package Main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerClickGuiOpener implements Listener//not working yet
{
	private Main plugin;
	
	public PlayerClickGuiOpener(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(plugin.getConfig().getBoolean("guisettings.useobjectforopening") && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.getMaterial(plugin.getConfig().getString("guisettings.objectforopeningmaterial"))))
			{
				if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Agario"))
				{
					Main.openGameGui(e.getPlayer());
				}
			}
		}
	}
}
