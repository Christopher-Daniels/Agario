package me.coolmann24.main;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.MagmaCube;

public class RedVirus 
{
	private MagmaCube magmacube;
	private Location loc;
	private double size;
	
	public RedVirus(MagmaCube magmacube, int size)
	{
		this.magmacube = magmacube;
		this.size = size;
	}
	public RedVirus(World world, Location loc, int magmasize, double doublesize)
	{
		magmacube = world.spawn(loc, MagmaCube.class);
		magmacube.setAI(false);
		magmacube.setGravity(false);
		magmacube.setCollidable(false);//doesn't work
		magmacube.setRemoveWhenFarAway(false);
		magmacube.setSize(magmasize);
		size = doublesize;
		this.loc = magmacube.getLocation();
	}
	
	public MagmaCube getMagma()
	{
		return magmacube;
	}
	public double getDoubleSize()
	{
		return size;
	}
	public void setDoubleSize(double size)
	{
		this.size = size;
	}
	public Location getRedVirusLocation()
	{
		return magmacube.getLocation();
	}
	public Location getPermanentLocation()
	{
		return loc;
	}
}
