package me.coolmann24.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClicked implements Listener
{
	private Main plugin;
	public InventoryClicked(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		Player p = (Player)event.getWhoClicked();
		
		if(Main.mapmanager.isPlayerPlaying(p)) event.setCancelled(true);
		
		if(event.getClickedInventory() == null) return;
		
		ItemStack i = event.getCurrentItem();
		
		if(i == null) return; 
		
		if(event.getClickedInventory().getName().equals("Agario Games"))
		{
			event.setCancelled(true);
			if(i.getType().equals(Material.SLIME_BLOCK))
			{
				String s = i.getItemMeta().getDisplayName().substring(6);
				for(Map map : Main.mapmanager.getMaps())
				{
					if(s.contains(map.getMapName()))
					{
						Bukkit.getServer().dispatchCommand((CommandSender)p, "agario join " + map.getMapName());
						p.closeInventory();
						return;
					}
				}
			}
		}
		if(plugin.getConfig().getBoolean("guisettings.useobjectforopening") && i.getType().equals(Material.getMaterial(plugin.getConfig().getString("guisettings.objectforopeningmaterial"))))
		{
			if(i.getItemMeta().getDisplayName().contains("Agario"))
			{
				event.setCancelled(true);
				Main.openGameGui(p);
			}
		}
	}
}
