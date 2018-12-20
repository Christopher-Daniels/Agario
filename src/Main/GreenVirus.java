package Main;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Slime;
import org.bukkit.util.Vector;

public class GreenVirus 
{
	private Slime slime;
	private Location loc;
	private int size;
	
	public GreenVirus(Slime slime, int size)
	{
		this.slime = slime;
		this.size = size;
	}
	public GreenVirus(World world, Location loc, int slimesize, int intsize, Location permloc, Vector velocity, Map map)
	{
		slime = world.spawn(loc, Slime.class);
		slime.setAI(false);
		slime.setGravity(false);
		slime.setCollidable(false);//doesn't work
		slime.setRemoveWhenFarAway(false);
		slime.setSize(slimesize);
		size = intsize;
		this.loc = permloc;
		if(!(velocity == null))
		{
			Main.mapmanager.doGreenVirusSplitTimer(this, velocity, map);
		}
	}
	
	public Slime getSlime()
	{
		return slime;
	}
	public int getIntSize()
	{
		return size;
	}
	public void setIntSize(int size)
	{
		this.size = size;
	}
	public Location getGreenVirusLocation()
	{
		return slime.getLocation();
	}
	public Location getPermanentLocation()
	{
		return loc;
	}
	public void setPermanentLocation(Location loc)
	{
		this.loc = loc;
	}
}
