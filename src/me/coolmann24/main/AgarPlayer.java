package me.coolmann24.main;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AgarPlayer 
{
	private ArrayList<Cell> cells;
	private ArrayList<Material> materials;
	private int materialdata;
	private Player player;
	private String team;
	private boolean splitbuffered, cooldown;
	
	public AgarPlayer(Player p, String team, int data)
	{
		player = p;
		cells = new ArrayList<Cell>();
		materials = new ArrayList<Material>();
		materialdata = data;
		materials.add(Material.WOOL);
		this.team = team;
		splitbuffered = true;
		cooldown = false;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	public void addCell(Entity e, int s, int recdelay)
	{
		cells.add(new Cell(e, s, recdelay));
	}
	public void removeCell(Entity e)
	{
		for(Cell c : cells)
		{
			if(c.getEntity().equals(e))
			{
				cells.remove(c);
				return;
			}
		}
	}
	public void removeCell(Cell c, Location spectatorspawn)//used upon a player being eaten
	{
		Cell c1 = c;
		cells.remove(c);
		if(c1.getEntity() instanceof Player)
		{
			Player p = (Player) c1.getEntity();
			if(cells.size() != 0)
			{
				Entity e = cells.get(0).getEntity();
				p.teleport(e);
				e.remove();
				cells.get(0).setEntity(p);
				p.sendMessage(ChatColor.RED + "You died so you were moved to another one of your cells!");
			}
			else
			{
				p.teleport(spectatorspawn);
				p.sendMessage(ChatColor.RED + "You died!");
			}
		}
		else
		{
			c1.getEntity().remove();
		}
	}
	public ArrayList<Cell> getCells()
	{
		return cells;
	}
	public ArrayList<Material> getMaterials()
	{
		return materials;
	}
	public String getTeam()
	{
		return team;
	}
	public boolean isSplitBuffered()
	{
		return splitbuffered;
	}
	public void setSplitBuffered(boolean s)
	{
		splitbuffered = s;
	}
	public Cell getPlayerCell()
	{
		for(Cell cell : cells)
		{
			if(cell.getEntity().equals(player))
			{
				return cell;
			}
		}
		return null;
	}
	public int getWoolColor()
	{
		return materialdata;
	}
	public boolean getCoolDown()
	{
		return cooldown;
	}
	public void setCoolDown(boolean s)
	{
		cooldown = s;
	}
	public double getTotalMass()
	{
		double mass = 0;
		for(Cell cell : cells)
		{
			mass += cell.getSize();
		}
		return mass;
	}
}
