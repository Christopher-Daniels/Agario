package Main;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandManager implements CommandExecutor
{
	private Main plugin;
	public CommandManager(Main plugin)
	{
		this.plugin = plugin;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("agario"))
		{
			if(!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			
			if(args.length == 0)
			{
				p.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
				p.sendMessage(ChatColor.AQUA + "Usage: /agario <args> <args>");
				p.sendMessage(ChatColor.AQUA + "/agario games -- shows active games on the server");
				p.sendMessage(ChatColor.AQUA + "/agario join <map name> -- join a map of specified name");
				p.sendMessage(ChatColor.AQUA + "/agario leave -- leave your current Agario game");
				p.sendMessage(ChatColor.GREEN + "Developed by coolmann24 in 2016, thanks for playing!");
				p.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
				if(p.hasPermission("agario.createmap"))
				{
					p.sendMessage(ChatColor.AQUA + "...also...");
					p.sendMessage(ChatColor.AQUA + "/agario createmap <int xminbound> <int xmaxbound> <int zminbound> <int zmaxbound> <int ymaplevel> <double xspectatorspawn> <double yspectatorspawn> <double zspectatorspawn> <String mapname>");
					p.sendMessage(ChatColor.GREEN + "Map specifications can be set in the config, check the spigotmc plugin page for more info!");
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("join"))
			{
				if(args.length == 1)
				{
					p.sendMessage(ChatColor.AQUA + "/agario join <mapname> -- to join a game!");
					p.sendMessage(Main.mapmanager.getActiveGamesMessage());
					if(plugin.getConfig().getBoolean("guisettings.openguiwithagariogamescommand"))
					{
						Main.openGameGui(p);
					}
					return true;
				}
				if(Main.mapmanager.isPlayerPlaying(p))
				{
					p.sendMessage(ChatColor.RED + "You are already playing Agario!");
					return true;
				}
				boolean joined = Main.mapmanager.playerJoinMap(p, args[1]);
				if(joined)
				{
					p.sendMessage(ChatColor.GREEN + "You joined the game with map name " + args[1] + "!");
				}
				else
				{
					p.sendMessage(ChatColor.RED + "The game with map name " + args[1] + " is invalid or currently full!");
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("leave"))
			{
				if(Main.mapmanager.isPlayerPlaying(p))
				{
					Map playersmap = Main.mapmanager.getPlayersMap(p);
					AgarPlayer agarplayer = playersmap.getAgarPlayer(p);
					
					p.teleport(playersmap.getSpectatorSpawn());
					p.setWalkSpeed((float).2);
					p.setFoodLevel(20);
					p.setLevel(0);
					p.getInventory().clear();
					Main.mapmanager.restorePlayerInventory(p);
					p.setScoreboard(Main.mapmanager.getBlankScoreboard());
					p.sendMessage(ChatColor.GREEN + "Leaving Agario map " + playersmap.getMapName() + "!");
					
					for(int i = agarplayer.getCells().size() - 1; i >= 0; i--)
					{
						Cell c = agarplayer.getCells().get(i);
						if(!c.isCellPlayer())
						{
							c.getEntity().remove();
						}
						agarplayer.getCells().remove(i);
					}
					playersmap.getPlayers().remove(agarplayer);
					return true;
				}
				else
				{
					p.sendMessage(ChatColor.RED + "You are not currently playing Agario!");
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("games"))
			{
				p.sendMessage(Main.mapmanager.getActiveGamesMessage());
				if(plugin.getConfig().getBoolean("guisettings.openguiwithagariogamescommand"))
				{
					Main.openGameGui(p);
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("createmap"))
			{
				if(!p.hasPermission("agario.createmap"))
				{
					p.sendMessage(ChatColor.RED + "You don't have permission to create an Agario map!");
					return true;
				}
				if(args.length < 7)
				{
					p.sendMessage(ChatColor.RED + "/agario createmap <int xminbound> <int xmaxbound> <int zminbound> <int zmaxbound> <int ymaplevel> <double xspectatorspawn> <double yspectatorspawn> <double zspectatorspawn> <String mapname>");
					return true;
				}
				int xmin;
				int xmax;
				int zmin;
				int zmax;
				int y;
				
				double xspawn;
				double yspawn;
				double zspawn;
				try
				{
					xmin = Integer.parseInt(args[1]);
					xmax = Integer.parseInt(args[2]);
					zmin = Integer.parseInt(args[3]);
					zmax = Integer.parseInt(args[4]);
					y = Integer.parseInt(args[5]);
					
					xspawn = Double.parseDouble(args[6]);
					yspawn = Double.parseDouble(args[7]);
					zspawn = Double.parseDouble(args[8]);
					
					if(xmin > xmax)
					{
						int a = xmin;
						xmin = xmax;
						xmax = a;
					}
					if(zmin > zmax)
					{
						int a = zmin;
						zmin = zmax;
						zmax = a;
					}
				}
				catch(Exception e)
				{
					p.sendMessage(ChatColor.RED + "/agario createmap <int xminbound> <int xmaxbound> <int zminbound> <int zmaxbound> <int ymaplevel> <double xspectatorspawn> <double yspectatorspawn> <double zspectatorspawn> <String mapname>");
					return true;
				}
				for(Map map : Main.mapmanager.getMaps())
				{
					if(map.getMapName().equals(args[9]))
					{
						p.sendMessage(ChatColor.RED + "A map with that name already exists! Choose a different name or delete the old map in the config file and reload the server!");
						return true;
					}
				}
				Material m = p.getWorld().getBlockAt(xmin, y, zmin).getType();
				for(int i = xmin; i <= xmax; i++)
				{
					for(int j = zmin; j <= zmax; j++)
					{
						if((!(p.getWorld().getBlockAt(i, y, j).getType() == m)) || m == Material.AIR)
						{
							p.sendMessage(ChatColor.RED + "The whole map area must have be the same non-air block material!");
							return true;
						}
					}
				}
				ArrayList<Integer> datalist = new ArrayList<Integer>();
				/*datalist.add(new Wool(DyeColor.BLACK));
				datalist.add(new Wool(DyeColor.BLUE));
				datalist.add(new Wool(DyeColor.BROWN));
				datalist.add(new Wool(DyeColor.CYAN));
				datalist.add(new Wool(DyeColor.GRAY));
				datalist.add(new Wool(DyeColor.GREEN));
				datalist.add(new Wool(DyeColor.LIGHT_BLUE));
				datalist.add(new Wool(DyeColor.LIME));
				datalist.add(new Wool(DyeColor.MAGENTA));
				datalist.add(new Wool(DyeColor.ORANGE));
				datalist.add(new Wool(DyeColor.PINK));
				datalist.add(new Wool(DyeColor.PURPLE));
				datalist.add(new Wool(DyeColor.RED));
				datalist.add(new Wool(DyeColor.SILVER));
				datalist.add(new Wool(DyeColor.YELLOW));*/
				datalist.add(0);
				datalist.add(1);
				datalist.add(2);
				datalist.add(3);
				datalist.add(4);
				datalist.add(5);
				datalist.add(6);
				datalist.add(7);
				datalist.add(8);
				datalist.add(9);
				datalist.add(10);
				datalist.add(11);
				datalist.add(12);
				datalist.add(13);
				datalist.add(14);
				datalist.add(15);
				
				
				if(!plugin.getConfig().contains("Maps"))
				{
					plugin.getConfig().createSection("Maps");
				}
				int availablemap = 1;
				boolean available = false;
				while(!available)
				{
					if(plugin.getConfig().contains("Maps." + availablemap))
					{
						availablemap++;
					}
					else
					{
						available = true;
						plugin.getConfig().createSection("Maps." + availablemap);
					}
				}
				String mapnumber = availablemap + "";
				plugin.getConfig().set("Maps." + mapnumber + ".mapname", args[9]);
				plugin.getConfig().set("Maps." + mapnumber + ".xminbound", xmin);
				plugin.getConfig().set("Maps." + mapnumber + ".xmaxbound", xmax);
				plugin.getConfig().set("Maps." + mapnumber + ".zminbound", zmin);
				plugin.getConfig().set("Maps." + mapnumber + ".zmaxbound", zmax);
				plugin.getConfig().set("Maps." + mapnumber + ".ylevel", y);
				plugin.getConfig().set("Maps." + mapnumber + ".world", p.getWorld().getName());
				plugin.getConfig().set("Maps." + mapnumber + ".mapmaterial", m.toString());
				plugin.getConfig().set("Maps." + mapnumber + ".massperblock", 5);
				plugin.getConfig().set("Maps." + mapnumber + ".maxsmallblobsnatural", 1000);
				plugin.getConfig().set("Maps." + mapnumber + ".decayrate", .005);
				plugin.getConfig().set("Maps." + mapnumber + ".defaultcellsizeint", 20);
				plugin.getConfig().set("Maps." + mapnumber + ".deathspawnlocationx", xspawn);
				plugin.getConfig().set("Maps." + mapnumber + ".deathspawnlocationy", yspawn);
				plugin.getConfig().set("Maps." + mapnumber + ".deathspawnlocationz", zspawn);
				plugin.getConfig().set("Maps." + mapnumber + ".walkspeedmultiplier", 25);
				plugin.getConfig().set("Maps." + mapnumber + ".recombinedelayticks", 800);
				plugin.getConfig().set("Maps." + mapnumber + ".smallmassblobspawnrate", 5);
				plugin.getConfig().set("Maps." + mapnumber + ".usegreenvirus", true);
				plugin.getConfig().set("Maps." + mapnumber + ".useredvirus", true);
				plugin.getConfig().set("Maps." + mapnumber + ".maxgreenviruscountint", 6);
				plugin.getConfig().set("Maps." + mapnumber + ".maxredviruscountint", 2);
				plugin.getConfig().set("Maps." + mapnumber + ".massthrowloss", 15.0);
				plugin.getConfig().set("Maps." + mapnumber + ".minmasstothrow", 35.0);
				plugin.getConfig().set("Maps." + mapnumber + ".minmassgreenvirussplit", 100.0);
				plugin.getConfig().set("Maps." + mapnumber + ".greenvirusduplicatesize", 6);
				plugin.getConfig().set("Maps." + mapnumber + ".massofthrownmassblob", 10.0);
				plugin.getConfig().set("Maps." + mapnumber + ".maxmassredvirusspawn", 400);
				plugin.getConfig().set("Maps." + mapnumber + ".minmassforcellsplit", 35.0);
				plugin.getConfig().set("Maps." + mapnumber + ".cellwoolcolorlist", datalist);
				plugin.getConfig().set("Maps." + mapnumber + ".gamemodeafterdeath", GameMode.ADVENTURE.toString());
				plugin.getConfig().set("Maps." + mapnumber + ".commandscantrunwhenplaying", new ArrayList<String>());
				plugin.getConfig().set("Maps." + mapnumber + ".useleaderboard", false);
				plugin.getConfig().set("Maps." + mapnumber + ".maxplayercount", 20);
				plugin.saveConfig();
				
				Map newmap = new Map(new int[]{xmin, xmax, zmin, zmax}, m, args[9], y, p.getWorld(), false, 5, 1000, .005, null, 20, new Location(p.getWorld(), xspawn, yspawn, zspawn), 25, 800, 5, true, true, 6, 2, 15, 35, 100, 6, 10, 400, 35, datalist, GameMode.ADVENTURE, new ArrayList<String>(), false, 20);
				if(Main.mapmanager.getMaps().size() == 0)
				{
					new GameTimer().runTaskTimer(plugin, 1, 2);//10 times/sec
				}
				Main.mapmanager.addMap(newmap);
				p.sendMessage(ChatColor.GREEN + "Map " + args[9] + " created!");
				return true;
			}
		}
		return true;
	}

}
