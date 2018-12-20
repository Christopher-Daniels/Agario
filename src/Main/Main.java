package Main;


import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;


public class Main extends JavaPlugin
{
	public static MapManager mapmanager;
	public static Random random;
	
	//use designated throwaway scoreboard in mapmanager class
	//bug where splitted slimes don't launch
	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(new EntityDamaged(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClicked(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
		Bukkit.getPluginManager().registerEvents(new FoodLevelChange(), this);
		Bukkit.getPluginManager().registerEvents(new ThrowMass(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerExecuteCommandEvent(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
		Bukkit.getPluginManager().registerEvents(new ChunkUnload(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDropItem(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerRightClickVillager(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerClickGuiOpener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		
		getLogger().info("Agario Enabled!");
		
		mapmanager = new MapManager(this);
		random = new Random();
		
		getCommand("agario").setExecutor(new CommandManager(this));
		
		this.loadMaps();
		mapmanager.killEntitiesInMap();
		mapmanager.restoreMapMaterial();
		
		if(mapmanager.getMaps().size() > 0)//don't run if no active games
		{
			new GameTimer().runTaskTimer(this, 1, 2);//10 times/sec
		}
		
		if(!getConfig().contains("guisettings"))
		{
			getConfig().set("guisettings.useobjectforopening", true);
			getConfig().set("guisettings.objectforopeningmaterial", "SLIME_BALL");
			getConfig().set("guisettings.openguiwithagariogamescommand", true);
			getConfig().set("guisettings.addguiopenitemtoinventory", false);
			saveConfig();
		}
		
		
	}
	public void onDisable()
	{
		mapmanager.killEntitiesInMap();
		mapmanager.restoreMapMaterial();
		
		mapmanager = null;
		random = null;
	}
	
	public static double distance(double x1, double z1, double x2, double z2)
	{
		double xchange = x1 - x2;
		double zchange = z1 - z2;
		
		return Math.sqrt((xchange*xchange) + (zchange*zchange));
	}
	
	public void loadMaps()//load in the maps on the config
	{
		if(!getConfig().contains("Maps")) return;
		
		int mapcount = 0;
		while(true)
		{
			if(getConfig().contains("Maps." + (mapcount + 1)))
			{
				int mapnumber = mapcount + 1;
				try
				{
					if(!getConfig().contains("Maps." + mapnumber + ".useleaderboard"))
					{
						getConfig().set("Maps." + mapnumber + ".useleaderboard", false);
						saveConfig();
					}
					if(!getConfig().contains("Maps." + mapnumber + ".maxplayercount"))
					{
						getConfig().set("Maps." + mapnumber + ".maxplayercount", 20);
						saveConfig();
					}
					
					Map map = new Map(
							new int[]{getConfig().getInt("Maps." + mapnumber + ".xminbound"),
									getConfig().getInt("Maps." + mapnumber + ".xmaxbound"),
									getConfig().getInt("Maps." + mapnumber + ".zminbound"),
									getConfig().getInt("Maps." + mapnumber + ".zmaxbound")},
							Material.valueOf(getConfig().getString("Maps." + mapnumber + ".mapmaterial")),
							getConfig().getString("Maps." + mapnumber + ".mapname"),
							getConfig().getInt("Maps." + mapnumber + ".ylevel"),
							Bukkit.getServer().getWorld(getConfig().getString("Maps." + mapnumber + ".world")),
							false,
							getConfig().getDouble("Maps." + mapnumber + ".massperblock"),
							getConfig().getInt("Maps." + mapnumber + ".maxsmallblobsnatural"),
							getConfig().getDouble("Maps." + mapnumber + ".decayrate"),
							new ArrayList<String>(),
							getConfig().getInt("Maps." + mapnumber + ".defaultcellsizeint"),
							new Location(
									Bukkit.getServer().getWorld(getConfig().getString("Maps." + mapnumber + ".world")),
									getConfig().getDouble("Maps." + mapnumber + ".deathspawnlocationx"),
									getConfig().getDouble("Maps." + mapnumber + ".deathspawnlocationy"),
									getConfig().getDouble("Maps." + mapnumber + ".deathspawnlocationz")),
							getConfig().getDouble("Maps." + mapnumber + ".walkspeedmultiplier"),
							getConfig().getInt("Maps." + mapnumber + ".recombinedelayticks"),
							getConfig().getInt("Maps." + mapnumber + ".smallmassblobspawnrate"),
							getConfig().getBoolean("Maps." + mapnumber + ".usegreenvirus"),
							getConfig().getBoolean("Maps." + mapnumber + ".useredvirus"),
							getConfig().getInt("Maps." + mapnumber + ".maxgreenviruscountint"),
							getConfig().getInt("Maps." + mapnumber + ".maxredviruscountint"),
							getConfig().getDouble("Maps." + mapnumber + ".massthrowloss"),
							getConfig().getDouble("Maps." + mapnumber + ".minmasstothrow"),
							getConfig().getDouble("Maps." + mapnumber + ".minmassgreenvirussplit"),
							getConfig().getInt("Maps." + mapnumber + ".greenvirusduplicatesize"),
							getConfig().getDouble("Maps." + mapnumber + ".massofthrownmassblob"),
							getConfig().getInt("Maps." + mapnumber + ".maxmassredvirusspawn"),
							getConfig().getDouble("Maps." + mapnumber + ".minmassforcellsplit"),
							(ArrayList<Integer>)getConfig().getIntegerList("Maps." + mapnumber + ".cellwoolcolorlist"),
							GameMode.valueOf(getConfig().getString("Maps." + mapnumber + ".gamemodeafterdeath")),
							(ArrayList<String>)getConfig().getStringList("Maps." + mapnumber + ".commandscantrunwhenplaying"),
							getConfig().getBoolean("Maps." + mapnumber + ".useleaderboard"),
							getConfig().getInt("Maps." + mapnumber + ".maxplayercount")
							);
					
					mapmanager.addMap(map);
					getLogger().info("Agario loaded map " + map.getMapName() + "!");
				
				}
				catch(Exception e)
				{
					e.printStackTrace();
					getLogger().info("[Error]: Agario couldn't load map " + mapnumber + " because of an error reading it from the config! Possible invalid config values, fix or re-create the map again!");
				}
				mapcount++;
			}
			else
			{
				return;
			}
			
		}
	}
	
	public static void openGameGui(Player p)
	{
		if(mapmanager.getMaps().size() == 0) return;
		
		Inventory i = Bukkit.createInventory(null, ((int)((mapmanager.getMaps().size()-1)/9)*9) + 9, "Agario Games");
		
		for(int j = 0; j < mapmanager.getMaps().size(); j++)
		{
			ItemStack is = new ItemStack(Material.SLIME_BLOCK);
			Map m = mapmanager.getMaps().get(j);
			ItemMeta s = is.getItemMeta();
			s.setDisplayName(ChatColor.AQUA + "Agario " + m.getMapName() + " Players: " + m.getPlayers().size() + "/" + m.getMaxPlayerCount());
			is.setItemMeta(s);
			
			i.setItem(j, is);
		}
		
		p.openInventory(i);
	}
}
