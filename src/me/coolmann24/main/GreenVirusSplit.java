package me.coolmann24.main;

import org.bukkit.Location;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GreenVirusSplit extends BukkitRunnable
{
	private GreenVirus g;
	private Vector direction;
	private int time;
	private Map map;
	
	public GreenVirusSplit(GreenVirus g, Vector direction, Map map)
	{
		this.g = g;
		this.direction = direction.multiply(3.0);
		this.map = map;
		time = 0;
	}
	
	
	@Override
	public void run()
	{
		time++;
		
		Slime s = g.getSlime();
		if(s == null)
		{
			this.cancel();
		}
		if(		s.getLocation().getX() + direction.getX() > map.getXMinBounds() &&
				s.getLocation().getX() + direction.getX() < map.getXMaxBounds() &&
				s.getLocation().getZ() + direction.getZ() > map.getZMinBounds() &&
				s.getLocation().getZ() + direction.getZ() < map.getZMaxBounds())
		{
			s.teleport(new Location(s.getWorld(), s.getLocation().getX() + direction.getX(), s.getLocation().getY(), s.getLocation().getZ() + direction.getZ()));
		}
		else
		{
			time = 5;
		}
		if(time == 5)
		{
			g.setPermanentLocation(g.getGreenVirusLocation());
			this.cancel();
		}
		
		
	}

}
