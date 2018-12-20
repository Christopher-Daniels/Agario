package Main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;


public class SmallMassBlob 
{
	public SmallMassBlob(World world, Location location, boolean redvirus, int woolcolor)
	{
		ArmorStand blob = world.spawn(location, ArmorStand.class);
		blob.setVisible(false);
		blob.setItemInHand(new ItemStack(Material.WOOL, 1, (short)woolcolor));
		blob.setSmall(true);
		blob.setGravity(false);
		blob.setVisible(false);
		blob.setSmall(true);
		blob.setGravity(false);
		if(redvirus)
		{
			blob.setBoots(new ItemStack(Material.STRING));
		}
			
	}
}
