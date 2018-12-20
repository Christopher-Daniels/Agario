package Main;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BigMassBlob 
{
	private ArmorStand a;
	private Vector velocity;
	
	public BigMassBlob(World world, Player p, int woolcolor)
	{
		a = world.spawn(p.getEyeLocation(), ArmorStand.class);
		a.setVisible(false);
		a.setGravity(false);
		a.setItemInHand(new ItemStack(Material.WOOL, 1, (short)woolcolor));
		velocity = new Vector(p.getLocation().getDirection().getX()*3.0, .5, p.getLocation().getDirection().getZ()*3.0);
	}
	
	public Vector getVelocity()
	{
		return velocity;
	}
	public void setVelocity(Vector v)
	{
		velocity = v;
	}
	public ArmorStand getBlob()
	{
		return a;
	}
}
