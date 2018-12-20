package Main;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;



public class MapManager //singleton
{
	private ArrayList<Map> maps;
	private Main plugin;
	private Scoreboard blankscoreboard;
	private HashMap<Player, Inventory> storedinventories;
	
	public MapManager(Main plugin)
	{
		blankscoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		maps = new ArrayList<Map>();
		storedinventories = new HashMap<Player, Inventory>();
		this.plugin = plugin;
	}
	
	public Scoreboard getBlankScoreboard()
	{
		return blankscoreboard;
	}
	public void addMap(Map map)
	{
		maps.add(map);
	}
	public void removeMap(Map map)
	{
		maps.remove(map);
	}
	public ArrayList<Map> getMaps()
	{
		return maps;
	}
	public Inventory getStoredInventory(Player p)
	{
		return storedinventories.get(p);
	}
	public void setStoredInventory(Player p, Inventory i)
	{
		storedinventories.put(p, i);
	}
	public void deleteStoredInventory(Player p)
	{
		storedinventories.remove(p);
	}
	public void restorePlayerInventory(Player p)
	{
		Inventory i = this.getStoredInventory(p);
		
		if(i == null) return;
		
		for(int j = 0; j <= 35; j++)
		{
			p.getInventory().setItem(j, i.getItem(j));
		}
		p.getInventory().setHelmet(i.getItem(36));
		p.getInventory().setChestplate(i.getItem(37));
		p.getInventory().setLeggings(i.getItem(38));
		p.getInventory().setBoots(i.getItem(39));
		p.getInventory().setItemInOffHand(i.getItem(40));
		
		this.deleteStoredInventory(p);
	}
	@SuppressWarnings("deprecation")
	public void updateCells()
	{
		Block b = null;
		for(Map map : maps)
		{
			for(int i = map.getBlocksUsed().size() - 1; i >= 0; i--)
			{
				map.getBlocksUsed().get(i).setType(map.getMapMaterial());
				map.getBlocksUsed().remove(i);
			}
			for(AgarPlayer player : map.getPlayers())
			{
				player.setCoolDown(false);//for throwing mass
				for(Cell cell : player.getCells())
				{
					double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
					double radius = Math.sqrt(blockarea/Math.PI);
					
					for(double i = cell.getCellLocation().getX() - radius - 1.0; i <= cell.getCellLocation().getX() + radius + 1.0; i += 1.0)
					{
						for(double j = cell.getCellLocation().getZ() - radius - 1.0; j <= cell.getCellLocation().getZ() + radius + 1.0; j += 1.0)
						{
							b = map.getWorld().getBlockAt((int)i, map.getYLevel(), (int)j);
							
							if(Main.distance(cell.getCellLocation().getX(), cell.getCellLocation().getZ(), ((double)b.getX()) + .5, ((double)b.getZ()) + .5) < radius)
							{
								if(b.getType() != Material.AIR)
								{
									b.setType(player.getMaterials().get(Main.random.nextInt(player.getMaterials().size())));
									try
									{
										//b.getState().setData() is not working so I'm switching to deprecated methods
										/*Wool m = (Wool)b.getState().getData();//fix this, only white wool
										m = (Wool)player.getWoolColor();
										b.getState().setData(m);
										Bukkit.getServer().broadcastMessage("" + b.getState().getData());
										b.getState().update(true);*/
										b.setData((byte)player.getWoolColor());
										
										
									}
									catch(Exception e) {e.printStackTrace();}
									map.addBlockUsed(b);
								}
							}
						}
					}
					
					//blockloc + 0.5
				}
			}
		}
	}
	
	public void updateEating()
	{
		for(Map map: maps)
		{
			for(int k = map.getPlayers().size() - 1; k >= 0; k--)
			{
				AgarPlayer player = map.getPlayers().get(k);
				for(int l = player.getCells().size() - 1; l >= 0; l--)
				{
					Cell cell = player.getCells().get(l);
					double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
					double radius = Math.sqrt(blockarea/Math.PI);
					
					ArrayList<Entity> entities = (ArrayList<Entity>)cell.getEntity().getNearbyEntities(radius, 10, radius);
					
					for(int i = entities.size() - 1; i >= 0; i--)
					{
						if(entities.get(i) instanceof ArmorStand && Main.distance(cell.getCellLocation().getX(), cell.getCellLocation().getZ(), entities.get(i).getLocation().getX(), entities.get(i).getLocation().getZ()) < radius)
						{
							ArmorStand a = (ArmorStand) entities.get(i);
							if(a.isSmall())
							{
								cell.setSize(cell.getSize() + 1);
								if(a.getBoots().getType() == Material.STRING)
								{
									map.setRedVirusBlobCount(map.getRedVirusBlobCount() - 1);
								}
								else
								{
									map.setSmallBlobCount(map.getSmallBlobCount() - 1);//not mass from red viruses
								}
							
								entities.get(i).remove();
								a.remove();
							}
							else
							{
								boolean ismoving = false;
								for(BigMassBlob movingblob : map.getBigMassBlobsMoving())
								{
									if(movingblob.getBlob().equals(a)) ismoving = true;
								}
								if(!ismoving)
								{
									cell.setSize(cell.getSize() + map.getMassOfBigMassBlob());
									a.remove();
								}
							}
						}
					}
					
					
					for(int i = map.getPlayers().size() - 1; i >= 0; i--)
					{
						AgarPlayer playernext = map.getPlayers().get(i);
						if((map.isTeamMode() && (!(player.getTeam().equals(playernext.getTeam())))) || (!player.getPlayer().equals(playernext.getPlayer()) && !map.isTeamMode()))
						{
							for(int j = playernext.getCells().size() - 1; j >= 0; j--)
							{
								Cell cellnext = playernext.getCells().get(j);
								if(entities.contains(cellnext.getEntity()))
								{
									if(((double)cellnext.getSize()*1.1 < (double)cell.getSize()) && Main.distance(cell.getCellLocation().getX(), cell.getCellLocation().getZ(), cellnext.getCellLocation().getX(), cellnext.getCellLocation().getZ()) < radius)
									{
										cell.setSize(cell.getSize() + cellnext.getSize());
										playernext.removeCell(cellnext, map.getSpectatorSpawn());
										if(playernext.getCells().size() == 0)
										{
											playernext.getPlayer().setWalkSpeed((float).2);
											playernext.getPlayer().setFoodLevel(20);
											playernext.getPlayer().setLevel(0);
											playernext.getPlayer().getInventory().clear();
											this.restorePlayerInventory(playernext.getPlayer());
											playernext.getPlayer().setScoreboard(blankscoreboard);
											map.getPlayers().remove(playernext);
										}
									}
								}
							}
						}
					}
					while(l > player.getCells().size())
					{
						l--;
					}
				}
				while(k > map.getPlayers().size())
				{
					k--;
				}
			}
		}
	}
	public void decayCells()
	{
		for(Map map : maps)
		{
			for(AgarPlayer player: map.getPlayers())
			{
				for(Cell cell : player.getCells())
				{
					cell.setSize(cell.getSize()*(1.0 - map.getDecay()));
				}
			}
		}
	}
	public void spawnSmallBlobs()
	{
		for(Map map : maps)
		{
			for(int i = 0; i <= map.getSmallMassBlobSpawnRate(); i++)
			{
				if(map.getSmallBlobCount() < map.getMaxSmallBlobs())
				{
					double xlength = ((double)map.getXMaxBounds()) - ((double)map.getXMinBounds());
					double zlength = ((double)map.getZMaxBounds()) - ((double)map.getZMinBounds());
				
					new SmallMassBlob(map.getWorld(), new Location(map.getWorld(), (double)map.getXMinBounds() + .5 + (Main.random.nextDouble()*xlength), (double)map.getYLevel() + 1.0, (double)map.getZMinBounds() + .5 + (Main.random.nextDouble()*zlength)), false, map.getMaterialDataList().get(Main.random.nextInt(map.getMaterialDataList().size())));
					map.setSmallBlobCount(map.getSmallBlobCount() + 1);
				}
			}
		}
	}
	public void spawnViruses()
	{
		for(Map map : maps)
		{
			if(map.getGreenViruses().size() < map.getMaxGreenVirus())
			{
				double xlength = ((double)map.getXMaxBounds()) - ((double)map.getXMinBounds());
				double zlength = ((double)map.getZMaxBounds()) - ((double)map.getZMinBounds());
				
				Location loc = new Location(map.getWorld(), (double)map.getXMinBounds() + .5 + (Main.random.nextDouble()*xlength), (double)map.getYLevel() + 1, (double)map.getZMinBounds() + .5 + (Main.random.nextDouble()*zlength));
				
				boolean canspawn = true;
				
				for(AgarPlayer player : map.getPlayers())
				{
					for(Cell cell : player.getCells())
					{
						double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
						double radius = Math.sqrt(blockarea/Math.PI);
						
						if(Main.distance(loc.getX(), loc.getZ(), cell.getCellLocation().getX(), cell.getCellLocation().getZ()) < radius + 3.0)
						{
							canspawn = false;
						}
					}
					
				}
				if(canspawn)
				{
					if(loc.getX() > ((double)map.getXMinBounds() + 5.5) && loc.getX() < ((double)map.getXMaxBounds() - 4.5))
					{
						if(loc.getZ() > ((double)map.getZMinBounds() + 5.5) && loc.getZ() < ((double)map.getZMaxBounds() - 4.5))
						{
							map.getGreenViruses().add(new GreenVirus(map.getWorld(), loc, 4, 0, loc, null, map));
						}
					}
				}
			}
			
			if(map.getRedViruses().size() < map.getMaxRedVirus())
			{
				double xlength = ((double)map.getXMaxBounds()) - ((double)map.getXMinBounds());
				double zlength = ((double)map.getZMaxBounds()) - ((double)map.getZMinBounds());
				
				Location loc = new Location(map.getWorld(), (double)map.getXMinBounds() + .5 + (Main.random.nextDouble()*xlength), (double)map.getYLevel() + 1, (double)map.getZMinBounds() + .5 + (Main.random.nextDouble()*zlength));
				
				boolean canspawn = true;
				
				for(AgarPlayer player : map.getPlayers())
				{
					for(Cell cell : player.getCells())
					{
						double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
						double radius = Math.sqrt(blockarea/Math.PI);
						
						if(Main.distance(loc.getX(), loc.getZ(), cell.getCellLocation().getX(), cell.getCellLocation().getZ()) < radius + 3.0)
						{
							canspawn = false;
						}
					}
				}
				if(canspawn)
				{
					if(loc.getX() > ((double)map.getXMinBounds() + 5.5) && loc.getX() < ((double)map.getXMaxBounds() - 4.5))
					{
						if(loc.getZ() > ((double)map.getZMinBounds() + 5.5) && loc.getZ() < ((double)map.getZMaxBounds() - 4.5))
						{
							map.getRedViruses().add(new RedVirus(map.getWorld(), loc, 4, 0));
						}
					}
				}
			}
			/*for(GreenVirus virus : map.getGreenViruses())//new launch system to be done
			{
				if(virus.getPermanentLocation() == null && virus.getSlime().getVelocity().length() < .01)
				{
					virus.setPermanentLocation(virus.getGreenVirusLocation());
				}
			}*/
		}
		
	}
	public String getActiveGamesMessage()
	{
		String builder = "";
		if(maps.size() == 0) return ChatColor.RED + "There are currently no agario games available!";
		
		builder += ChatColor.GOLD + "Current Games Available: " + ChatColor.AQUA;
		for(Map map : maps)
		{
			builder += "Mapname: " + map.getMapName() + " Players: " + map.getPlayers().size() + "/" + map.getMaxPlayerCount() + ", ";
		}
		return builder.substring(0, builder.length() - 2);
	}
	public boolean playerJoinMap(Player p, String mapname)
	{
		for(Map map : maps)
		{
			if(map.getMapName().equalsIgnoreCase(mapname))
			{
				if(map.getPlayers().size() >= map.getMaxPlayerCount())
				{
					return false;
				}
				
				AgarPlayer newplayer;
				int data = map.getMaterialDataList().get(Main.random.nextInt(map.getMaterialDataList().size()));
				if(map.isTeamMode())
				{
					newplayer = new AgarPlayer(p, map.getTeams().get(Main.random.nextInt(map.getTeams().size())), data);
				}
				else
				{
					newplayer = new AgarPlayer(p, null, data);
				}
				newplayer.addCell(p, map.getDefaultSize(), 0);
				double xlength = ((double)map.getXMaxBounds()) - ((double)map.getXMinBounds());
				double zlength = ((double)map.getZMaxBounds()) - ((double)map.getZMinBounds());
				
				p.setGameMode(GameMode.ADVENTURE);
				p.setFoodLevel(6);
				if(map.usingLeaderboard())
					p.setScoreboard(map.getScoreboardObjective().getScoreboard());
				
				Location loc = null;
				boolean safe = false;
				
				while(!safe)
				{
					safe = true;
					loc = new Location(map.getWorld(), (double)map.getXMinBounds() + .5 + (Main.random.nextDouble()*xlength), (double)map.getYLevel() + 1, (double)map.getZMinBounds() + .5 + (Main.random.nextDouble()*zlength));
					for(AgarPlayer player : map.getPlayers())
					{
						for(Cell cell : player.getCells())
						{
							double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
							double radius = Math.sqrt(blockarea/Math.PI);
							
							if(Main.distance(loc.getX(), loc.getZ(), cell.getCellLocation().getX(), cell.getCellLocation().getZ()) < radius + 2.0)
							{
								safe = false;
							}
							
						}
					}
					for(RedVirus r : map.getRedViruses())
					{
						if(Main.distance(loc.getX(), loc.getZ(), r.getMagma().getLocation().getX(), r.getMagma().getLocation().getZ()) <  3.0)
						{
							safe = false;
						}
					}
				}
				p.teleport(loc);
				map.getPlayers().add(newplayer);
				
				Inventory savedinv = Bukkit.createInventory(null, 45);
				savedinv.setItem(36, p.getInventory().getHelmet());
				savedinv.setItem(37, p.getInventory().getChestplate());
				savedinv.setItem(38, p.getInventory().getLeggings());
				savedinv.setItem(39, p.getInventory().getBoots());
				savedinv.setItem(40, p.getInventory().getItemInOffHand());
				this.setStoredInventory(p, savedinv);
				
				for(int i = 0; i <= 35; i++)
				{
					savedinv.setItem(i, p.getInventory().getItem(i));
				}
				p.getInventory().clear();
				for(int i = 0; i <= 8; i++)
				{
					p.getInventory().setItem(i, new ItemStack(newplayer.getMaterials().get(0), 1, (short)newplayer.getWoolColor()));;
				}
				return true;
			}
		}
		return false;
	}
	public boolean isPlayerPlaying(Player p)
	{
		for(Map map : maps)
		{
			for(AgarPlayer player : map.getPlayers())
			{
				if(player.getPlayer().equals(p))
				{
					return true;
				}
			}
		}
		return false;
	}
	public Map getPlayersMap(Player p)
	{
		for(Map map : maps)
		{
			for(AgarPlayer player : map.getPlayers())
			{
				if(player.getPlayer().equals(p))
				{
					return map;
				}
			}
		}
		return null;
	}
	public void showPlayerCellsMass()
	{
		for(Map map : maps)
		{
			for(AgarPlayer player : map.getPlayers())
			{
				for(Cell cell : player.getCells())
				{
					if(cell.isCellPlayer())
					{
						Player p = (Player)cell.getEntity();
						p.setLevel((int)cell.getSize());
					}
				}
			}
		}
	}
	public void setCellsWalkSpeed()
	{
		for(Map map : maps)
		{
			for(AgarPlayer player : map.getPlayers())
			{
				for(Cell cell : player.getCells())
				{
					if(cell.isCellPlayer())
					{
						Player p = (Player) cell.getEntity();
						float walkspeed = (float) (map.getWalkSpeedMultiplier()/(Math.sqrt(cell.getSize())*5.0));
						if(walkspeed > 1.0) walkspeed = (float) .999;
						p.setWalkSpeed(walkspeed);
					}
					else
					{
						Entity e = cell.getEntity();
						Player p = null;
						Cell cellneed = null;
						for(Cell cell1 : player.getCells())
						{
							if(cell1.isCellPlayer())
							{
								p = (Player) cell1.getEntity();
								cellneed = cell1;
							}
						}
						Vector entitydirection = new Vector(p.getLocation().getBlockX() - e.getLocation().getX(), 0, p.getLocation().getZ() - e.getLocation().getZ());
						entitydirection.normalize();
						e.getLocation().setDirection(entitydirection);
						
						double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
						double radius = Math.sqrt(blockarea/Math.PI);
						
						double blockarea2 = ((double)cellneed.getSize())/map.getMassPerBlock();
						double radius2 = Math.sqrt(blockarea2/Math.PI);
						
						if(Main.distance(p.getLocation().getX(), p.getLocation().getZ(), e.getLocation().getX(), e.getLocation().getZ()) > radius + radius2)
						{
							float speed = (float) (1.5*map.getWalkSpeedMultiplier()/(Math.sqrt(cell.getSize())*5.0));
							entitydirection.multiply(speed);
							e.teleport(new Location(e.getWorld(), e.getLocation().getX() + (double) entitydirection.getX(), e.getLocation().getY(), e.getLocation().getZ() + (double) entitydirection.getZ()));
						}
					}
				}
			}
		}
	}
	
	public void cellSplitUpdate()
	{
		for(Map map : maps)
		{
			for(AgarPlayer player : map.getPlayers())
			{
				Player p = player.getPlayer();
				Cell playercell = null;
				for(Cell cell : player.getCells())
				{
					
					if(cell.isCellPlayer()) playercell = cell;
				}
				if(p.getLocation().getY() > ((double)(map.getYLevel()) + 1.01) && !player.isSplitBuffered() && playercell.getSize() >= map.getMinMassForSplit())
				{
					Villager z = p.getWorld().spawn(new Location(p.getWorld(), p.getLocation().getX(), (double)(map.getYLevel() + 1), p.getLocation().getZ()), Villager.class);
					z.setAI(false);
					Cell newcell = new Cell(z, playercell.getSize()/2.0, map.getRecombineDelay());
					playercell.setRecombineDelay(map.getRecombineDelay());
					player.getCells().add(newcell);//direct adding
					playercell.setSize(playercell.getSize()/2.0);
					p.setVelocity(new Vector(p.getLocation().getDirection().getX()*3.0, p.getVelocity().getY(), p.getLocation().getDirection().getZ()*3.0));//split speed
					player.setSplitBuffered(true);
				}
				if(p.getLocation().getY() < ((double)(map.getYLevel()) + 1.01)) player.setSplitBuffered(false);
			}
		}
	}
	
	public void upDateCellRecombine()
	{
		for(Map map : maps)
		{
			for(AgarPlayer player : map.getPlayers())
			{
				for(int i = player.getCells().size() - 1; i >= 0; i--)
				{
					Cell cell1 = player.getCells().get(i);
					if(cell1.getRecombineDelay() != 0) cell1.setRecombineDelay(cell1.getRecombineDelay() - 2);
					for(int j = i - 1; j >= 0; j--)
					{
						Cell cell2 = player.getCells().get(j);
						
						double blockarea = ((double)cell1.getSize())/map.getMassPerBlock();
						double radius = Math.sqrt(blockarea/Math.PI);
						
						double blockarea2 = ((double)cell2.getSize())/map.getMassPerBlock();
						double radius2 = Math.sqrt(blockarea2/Math.PI);
						
						if(Main.distance(cell1.getCellLocation().getX(), cell1.getCellLocation().getZ(), cell2.getCellLocation().getX(), cell2.getCellLocation().getZ()) <= radius + radius2 && cell1.getRecombineDelay() == 0 && cell2.getRecombineDelay() == 0)
						{
							Cell playercell = null;
							Cell nonplayercell = null;
							
							if(cell1.isCellPlayer())
							{
								playercell = cell1;
								nonplayercell = cell2;
							}
							if(cell2.isCellPlayer())
							{
								playercell = cell2;
								nonplayercell = cell1;
							}
							
							
							if(playercell == null)
							{
								cell2.setSize(cell2.getSize() + cell1.getSize());
								player.removeCell(cell1, map.getSpectatorSpawn());
							}
							else
							{
								playercell.setSize(nonplayercell.getSize() + playercell.getSize());
								player.removeCell(nonplayercell, map.getSpectatorSpawn());
								player.getPlayer().playSound(playercell.getCellLocation(), Sound.BLOCK_NOTE_HAT, 1, 1);
							}
							break;
						}
					}
					while(i > player.getCells().size())
					{
						i--;
					}
				}
			}
		}
	}
	
	public void killEntitiesInMap()
	{
		for(Map map : maps)
		{
			ArrayList<ArmorStand> food = (ArrayList<ArmorStand>)map.getWorld().getEntitiesByClass(ArmorStand.class);
			ArrayList<Slime> greenviruses = (ArrayList<Slime>)map.getWorld().getEntitiesByClass(Slime.class);
			ArrayList<MagmaCube> redviruses = (ArrayList<MagmaCube>)map.getWorld().getEntitiesByClass(MagmaCube.class);
			ArrayList<Villager> villagers = (ArrayList<Villager>)map.getWorld().getEntitiesByClass(Villager.class);
			
			for(int i = food.size() - 1; i >= 0; i--)
			{
				ArmorStand a = food.get(i);
				if( a.getLocation().getX() >= ((double)map.getXMinBounds() - .5) &&
					a.getLocation().getX() <= ((double)map.getXMaxBounds() + 1.5) &&
					a.getLocation().getZ() >= ((double)map.getZMinBounds() - .5) &&
					a.getLocation().getZ() <= ((double)map.getZMaxBounds() + 1.5) &&
					a.getLocation().getY() >= ((double)map.getYLevel() - 10.0) &&
					a.getLocation().getY() <= ((double)map.getYLevel() + 10.0))
				{
					a.remove();
				}
			}
			for(int i = greenviruses.size() - 1; i >= 0; i--)
			{
				Slime a = greenviruses.get(i);
				if( a.getLocation().getX() >= ((double)map.getXMinBounds() - .5) &&
					a.getLocation().getX() <= ((double)map.getXMaxBounds() + 1.5) &&
					a.getLocation().getZ() >= ((double)map.getZMinBounds() - .5) &&
					a.getLocation().getZ() <= ((double)map.getZMaxBounds() + 1.5) &&
					a.getLocation().getY() >= ((double)map.getYLevel() - 10.0) &&
					a.getLocation().getY() <= ((double)map.getYLevel() + 10.0))
				{
					a.remove();
				}
			}
			for(int i = redviruses.size() - 1; i >= 0; i--)
			{
				MagmaCube a = redviruses.get(i);
				if( a.getLocation().getX() >= ((double)map.getXMinBounds() - .5) &&
					a.getLocation().getX() <= ((double)map.getXMaxBounds() + 1.5) &&
					a.getLocation().getZ() >= ((double)map.getZMinBounds() - .5) &&
					a.getLocation().getZ() <= ((double)map.getZMaxBounds() + 1.5) &&
					a.getLocation().getY() >= ((double)map.getYLevel() - 10.0) &&
					a.getLocation().getY() <= ((double)map.getYLevel() + 10.0))
				{
					a.remove();
				}
			}
			for(AgarPlayer player : map.getPlayers())
			{
				for(Cell cell : player.getCells())
				{
					if(villagers.contains(cell.getEntity()))
					{
						cell.getEntity().remove();
					}
				}
				player.getPlayer().teleport(map.getSpectatorSpawn());
				player.getPlayer().setWalkSpeed((float).2);
				player.getPlayer().setFoodLevel(20);
				player.getPlayer().setLevel(0);
				player.getPlayer().getInventory().clear();
				this.restorePlayerInventory(player.getPlayer());
			}
		}
	}
	
	public void restoreMapMaterial()
	{
		for(Map map : maps)
		{
			Block b = null;
			for(int i = map.getXMinBounds(); i <= map.getXMaxBounds(); i++)
			{
				for(int j = map.getZMinBounds(); j <= map.getZMaxBounds(); j++)
				{
					b = map.getWorld().getBlockAt(i, map.getYLevel(), j);
					if(b.getType() != map.getMapMaterial())
					{
						b.setType(map.getMapMaterial());
					}
				}
			}
		}
	}
	public void touchVirusUpdate()
	{
		for(Map map : maps)
		{
			
			for(int a = map.getPlayers().size() - 1; a >= 0; a--)
			{
				AgarPlayer player = map.getPlayers().get(a);
				for(int i = player.getCells().size() - 1; i >= 0; i--)
				{
					Cell cell = player.getCells().get(i);
					double blockarea = ((double)cell.getSize())/map.getMassPerBlock();
					double radius = Math.sqrt(blockarea/Math.PI);
					
					ArrayList<Entity> nearbyentities = (ArrayList<Entity>) cell.getEntity().getNearbyEntities(radius, 10, radius);
					for(int j = map.getGreenViruses().size() - 1; j >= 0; j--)
					{
						GreenVirus virus = map.getGreenViruses().get(j);
						if(nearbyentities.contains(virus.getSlime()))
						{
							if(Main.distance(cell.getCellLocation().getX(), cell.getCellLocation().getZ(), virus.getGreenVirusLocation().getX(), virus.getGreenVirusLocation().getZ()) < radius && cell.getSize() >= map.getMinMassVirusSplit())
							{
								double sumofmass = cell.getSize()*.75;
								cell.setSize(sumofmass/2.0);
								cell.setRecombineDelay(map.getRecombineDelay());
								Villager v = cell.getEntity().getWorld().spawn(cell.getCellLocation(), Villager.class);
								v.setAI(false);
								Cell newcell = new Cell(v, sumofmass/2.0, map.getRecombineDelay());
								player.getCells().add(newcell);
								
								Slime s = virus.getSlime();
								map.getGreenViruses().remove(virus);
								s.remove();
								
								if(cell.isCellPlayer()) 
								{
									player.getPlayer().sendMessage(ChatColor.RED + "You hit a virus and split!");
								}
								else
								{
									player.getPlayer().sendMessage(ChatColor.RED + "One of your cells hit a virus and split!");
								}
								
							}
						}
					}
					for(RedVirus virus : map.getRedViruses())
					{
						if(nearbyentities.contains(virus.getMagma()))
						{
							if(Main.distance(cell.getCellLocation().getX(), cell.getCellLocation().getZ(), virus.getRedVirusLocation().getX(), virus.getRedVirusLocation().getZ()) < (radius/2.0))
							{
								virus.setDoubleSize(virus.getDoubleSize() + cell.getSize());
								player.removeCell(cell, map.getSpectatorSpawn());
								if(player.getCells().size() == 0)
								{
									player.getPlayer().setWalkSpeed((float).2);
									player.getPlayer().setFoodLevel(20);
									player.getPlayer().setLevel(0);
									player.getPlayer().getInventory().clear();
									this.restorePlayerInventory(player.getPlayer());
									player.getPlayer().setScoreboard(blankscoreboard);
									map.getPlayers().remove(player);
								}
								
								
							}
						}
					}
					
				}
			}
		}
	}
	
	public void returnViruses()
	{
		for(Map map : maps)
		{
			for(GreenVirus virus : map.getGreenViruses())
			{
				if((!(virus.getPermanentLocation() == null)) && (!virus.getGreenVirusLocation().equals(virus.getPermanentLocation())))
				{
					virus.getSlime().teleport(virus.getPermanentLocation());
				}
			}
			for(RedVirus virus : map.getRedViruses())
			{
				if(!virus.getRedVirusLocation().equals(virus.getPermanentLocation()))
				{
					virus.getMagma().teleport(virus.getPermanentLocation());
				}
			}
		}
	}
	
	public void updateThrownMass()
	{
		for(Map map : maps)
		{
			for(BigMassBlob b : map.getBigMassBlobsMoving())
			{
				b.setVelocity(new Vector(b.getVelocity().getX(), b.getVelocity().getY() - 0.25, b.getVelocity().getZ()));
				if( (b.getBlob().getLocation().getX() + b.getVelocity().getX() < (double)map.getXMinBounds() ||
					 b.getBlob().getLocation().getX() + b.getVelocity().getX() > (double)map.getXMaxBounds() + 1.0) ||
					(b.getBlob().getLocation().getZ() + b.getVelocity().getZ() < (double)map.getZMinBounds() ||
					 b.getBlob().getLocation().getZ() + b.getVelocity().getZ() > (double)map.getZMaxBounds() + 1.0))
				{
					b.setVelocity(new Vector(0, b.getVelocity().getY(), 0));
				}
				if(b.getBlob().getLocation().getY() + b.getVelocity().getY() < (double)map.getYLevel())
				{
					b.setVelocity(new Vector(0, 0, 0));
					b.getBlob().teleport(new Location(b.getBlob().getWorld(), b.getBlob().getLocation().getX(), (double)map.getYLevel() + .5, b.getBlob().getLocation().getZ()));
				}
				b.getBlob().teleport(new Location(b.getBlob().getWorld(), b.getBlob().getLocation().getX() + b.getVelocity().getX(), b.getBlob().getLocation().getY() + b.getVelocity().getY(), b.getBlob().getLocation().getZ() + b.getVelocity().getZ()));
				
			}
			for(int i = map.getBigMassBlobsMoving().size() - 1; i >= 0; i--)
			{
				BigMassBlob b = map.getBigMassBlobsMoving().get(i);
				ArrayList<Entity> nearentities = (ArrayList<Entity>)b.getBlob().getNearbyEntities(1, 10, 1);
				boolean deleted = false;
				for(int j = map.getGreenViruses().size() - 1; j >= 0; j--)
				{
					GreenVirus virus = map.getGreenViruses().get(j);
					if(nearentities.contains(virus.getSlime()))
					{
						Vector v = b.getVelocity();
						b.getBlob().remove();
						map.getBigMassBlobsMoving().remove(i);
						deleted = true;
						virus.setIntSize(virus.getIntSize() + 1);
						if(virus.getIntSize() == map.getGreenVirusSplitSize())
						{
							virus.setIntSize(0);
							map.getGreenViruses().add(new GreenVirus(map.getWorld(), virus.getGreenVirusLocation(), 4, 0, null, v.normalize(), map));
						}
						break;
					}
				}
				if(!deleted)
				{
					for(RedVirus virus : map.getRedViruses())
					{
						if(nearentities.contains(virus.getMagma()))
						{
							b.getBlob().remove();
							map.getBigMassBlobsMoving().remove(i);
							deleted = true;
							virus.setDoubleSize(virus.getDoubleSize() + map.getMassOfBigMassBlob());
							break;
						}
					}
				}
			}
			for(int i = map.getBigMassBlobsMoving().size() - 1; i >= 0; i--)
			{
				BigMassBlob b = map.getBigMassBlobsMoving().get(i);
				if(b.getVelocity().getX() == 0 && b.getVelocity().getY() == 0 && b.getVelocity().getZ() == 0 && b.getBlob().getLocation().getY() < (double)map.getYLevel() + .501)
				{
					map.getBigMassBlobsMoving().remove(i);
				}
			}
		}
	}
	
	public void spawnRedVirusSmallBlobs()
	{
		for(Map map : maps)
		{
			if(!(map.getRedVirusBlobCount() > map.getMaxredVirusMassSpawn()))
			for(RedVirus virus : map.getRedViruses())
			{
				int spawns = 0;
				double decsize = 0;
				if(virus.getDoubleSize() >= 2.0){spawns = 3; decsize = 2.0;}
				else if(virus.getDoubleSize() >= 1.0) {spawns = 2; decsize = 1.0;}
				else spawns = 1;
				
				Location loc = null;
				int spawned = 0;
				while(spawned < spawns)
				{
					loc = new Location(map.getWorld(), virus.getRedVirusLocation().getX() + (Main.random.nextDouble()*12 - 6.0), (double)map.getYLevel() + 1.0, virus.getRedVirusLocation().getZ() + (Main.random.nextDouble()*12 - 6.0));
					if(Main.distance(loc.getX(), loc.getZ(), virus.getRedVirusLocation().getX(), virus.getRedVirusLocation().getZ()) > 1.0)
					{
						if(Main.distance(loc.getX(), loc.getZ(), virus.getRedVirusLocation().getX(), virus.getRedVirusLocation().getZ()) < 6.0)
						{
							new SmallMassBlob(map.getWorld(), loc, true, map.getMaterialDataList().get(Main.random.nextInt(map.getMaterialDataList().size())));
							map.setRedVirusBlobCount(map.getRedVirusBlobCount() + 1);
							
							spawned++;
						}
					}
				}
				virus.setDoubleSize(virus.getDoubleSize() - decsize);
			}
		}
	}
	
	public boolean chunkInUse(Chunk c)
	{
		for(Map map : maps)
		{
			if(map.getChunks().contains(c)) return true;
		}
		return false;
	}
	
	public void killCellsOutOfMap()
	{
		for(Map map : maps)
		{
			for(int i = map.getPlayers().size() - 1; i >= 0; i--)
			{
				AgarPlayer aplayer = map.getPlayers().get(i);
				for(int j = aplayer.getCells().size() - 1; j >= 0; j--)
				{
					Cell cell = aplayer.getCells().get(j);
					Location loc = cell.getCellLocation();
					if(		loc.getY() < ((double)map.getYLevel()) - 3.0 ||
							loc.getX() < (double)map.getXMinBounds() ||
							loc.getX() > (double)(map.getXMaxBounds() + 1.0) ||
							loc.getZ() < (double)map.getZMinBounds() ||
							loc.getZ() > (double)(map.getZMaxBounds() + 1.0))
					{
						aplayer.removeCell(cell, map.getSpectatorSpawn());
						if(aplayer.getCells().size() == 0)
						{
							aplayer.getPlayer().setFallDistance(0);
							aplayer.getPlayer().setWalkSpeed((float).2);
							aplayer.getPlayer().setFoodLevel(20);
							aplayer.getPlayer().setLevel(0);
							aplayer.getPlayer().getInventory().clear();
							this.restorePlayerInventory(aplayer.getPlayer());
							aplayer.getPlayer().setScoreboard(blankscoreboard);
							map.getPlayers().remove(aplayer);
						}
					}
				}
			}
		}
	}
	
	public void displayMassToPlayers()
	{
		for(Map map : maps)
		{
			if(map.usingLeaderboard())
			{
				ArrayList<String> oldentries = new ArrayList<String>(map.getScoreboardObjective().getScoreboard().getEntries());
				for(String entry : oldentries)
				{
					map.getScoreboardObjective().getScoreboard().resetScores(entry);
				}
				HashMap<Player, Integer> entries = new HashMap<Player, Integer>();
				for(AgarPlayer player : map.getPlayers())
				{
					entries.put(player.getPlayer(), (int)player.getTotalMass());
				}
				ArrayList<Player> sorted = new ArrayList<Player>();
				ArrayList<Player> names = new ArrayList<Player>(entries.keySet());
				while(true)
				{
					Player largest = null;
					int value = 0;
					for(int i = 0; i < names.size(); i++)
					{
						if(entries.get(names.get(i)) > value)
						{
							largest = names.get(i);
							value = entries.get(names.get(i));
						}
					}
					if(value != 0)
					{
						sorted.add(largest);
						names.remove(largest);
					}
					if(names.size() == 0) break;
				}
				int entriesnum = 0;
				for(Player s : sorted)
				{
					map.getScoreboardObjective().getScore(s.getName()).setScore((int)map.getAgarPlayer(s).getTotalMass());
					entriesnum++;
					if(entriesnum == 15) break;
				}
			}
		}
		
	}
	public void doGreenVirusSplitTimer(GreenVirus g, Vector direction, Map map)
	{
		new GreenVirusSplit(g, direction, map).runTaskTimer(plugin, 0, 4);
	}
}
