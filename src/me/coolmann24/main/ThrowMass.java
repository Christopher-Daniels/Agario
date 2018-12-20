package me.coolmann24.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;

public class ThrowMass implements Listener
{
	@EventHandler
	public void onRightClick(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)//or block but it launches two, fix
		{
			if(Main.mapmanager.isPlayerPlaying(p))
			{
				Map playersmap = Main.mapmanager.getPlayersMap(p);
				AgarPlayer agarplayer = playersmap.getAgarPlayer(p);
				Cell playercell = agarplayer.getPlayerCell();
				
				if(p.getInventory().getItemInMainHand().getType().equals(agarplayer.getMaterials().get(0)) && (!agarplayer.getCoolDown()))
				{
					agarplayer.setCoolDown(true);
					if(playercell.getSize() < playersmap.getMassToThrow())
					{
						p.sendMessage(ChatColor.RED + "You need at least " + ((int)playersmap.getMassToThrow()) + " to throw mass!");
					}
					else
					{
						playersmap.getBigMassBlobsMoving().add(new BigMassBlob(p.getWorld(), p, agarplayer.getWoolColor()));
						playercell.setSize(playercell.getSize() - playersmap.getMassThrowLoss());
					}
				}
			}
		}
	}
}
