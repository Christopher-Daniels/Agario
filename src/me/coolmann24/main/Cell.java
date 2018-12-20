package me.coolmann24.main;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Cell 
{
	private Entity entity;
	private double size;
	private int recombinedelay;
	
	public Cell(Entity entity, double size, int recombinedelay)
	{
		this.entity = entity;
		this.size = size;
		this.recombinedelay = recombinedelay;
	}
	
	public double getSize()
	{
		return size;
	}
	public void setSize(double size)
	{
		this.size = size;
	}
	public Entity getEntity()
	{
		return entity;
	}
	public boolean isCellPlayer()
	{
		return (entity instanceof Player);
	}
	public Location getCellLocation()
	{
		return entity.getLocation();
	}
	public void setEntity(Entity e)
	{
		entity.remove();
		entity = e;
	}
	public int getRecombineDelay()
	{
		return recombinedelay;
	}
	public void setRecombineDelay(int s)
	{
		recombinedelay = s;
	}
}
