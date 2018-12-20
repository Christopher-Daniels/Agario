package me.coolmann24.main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class Map 
{
	private ArrayList<AgarPlayer> agarplayers;
	private ArrayList<GreenVirus> greenviruses;
	private ArrayList<RedVirus> redviruses;
	private ArrayList<Block> cellblocks;
	private ArrayList<BigMassBlob> movingthrownmass;
	private ArrayList<String> teams;
	private ArrayList<Integer> materialdatalist;
	private ArrayList<String> commandscantrunwhenplaying;
	private ArrayList<Chunk> chunks;
	
	private int[] mapbounds;
	private int smallblobcount, mapylevel, maxsmallblobs, defaultsize, recombinedelay, smallmassblobspawnrate, maxgreenvirus, maxredvirus, greenvirussplitsize, maxredvirusmassspawn, redvirusblobcount, maxplayercount;
	private Material mapmaterial;
	private String mapname;
	private World world;
	private boolean teammode, usegreenviruses, useredviruses, leaderboard;
	private double massperblock, decay, walkspeedmultiplier, massthrowloss, masstothrow, minmassvirussplit, massofbigmassblob, minmassforsplit;
	private Location spectatorlocation;
	private GameMode gamemodeafterdeath;
	private Objective objective;
	
	public Map(int [] mapbounds, Material material, String mapname, int ylevel, World world, boolean teammode, double massperblock, int maxsmallblobs, double decay, ArrayList<String> teams, int defaultsize, Location spectatorlocation, double walkspeedmultiplier, int recombinedelay, int smallmassblobspawnrate, boolean usegreenviruses, boolean useredviruses, int maxgreenvirus, int maxredvirus, double massthrowloss, double masstothrow, double minmassvirussplit, int greenvirussplitsize, double massofbigmassblob, int maxredvirusmassspawn, double minmassforsplit, ArrayList<Integer> materialdata, GameMode gamemodeafterdeath, ArrayList<String> commandscantrunwhenplaying, boolean leaderboard, int maxplayercount)
	{
		this.mapbounds = mapbounds;
		mapmaterial = material;
		this.mapname = mapname;
		smallblobcount = 0;
		mapylevel = ylevel;
		this.world = world;
		this.teammode = teammode;
		this.massperblock = massperblock;
		this.maxsmallblobs = maxsmallblobs;
		this.decay = decay;
		this.defaultsize = defaultsize;
		this.spectatorlocation = spectatorlocation;
		this.walkspeedmultiplier = walkspeedmultiplier;
		this.recombinedelay = recombinedelay;
		this.smallmassblobspawnrate = smallmassblobspawnrate;
		this.usegreenviruses = usegreenviruses;
		this.useredviruses = useredviruses;
		this.maxgreenvirus = maxgreenvirus;
		this.maxredvirus = maxredvirus;
		this.massthrowloss = massthrowloss;
		this.masstothrow = masstothrow;
		this.minmassvirussplit = minmassvirussplit;
		this.greenvirussplitsize = greenvirussplitsize;
		this.massofbigmassblob = massofbigmassblob;
		this.maxredvirusmassspawn = maxredvirusmassspawn;
		redvirusblobcount = 0;
		this.minmassforsplit = minmassforsplit;
		this.gamemodeafterdeath = gamemodeafterdeath;
		this.maxplayercount = maxplayercount;
		
		agarplayers = new ArrayList<AgarPlayer>();
		greenviruses = new ArrayList<GreenVirus>();
		redviruses = new ArrayList<RedVirus>();
		cellblocks = new ArrayList<Block>();
		chunks = new ArrayList<Chunk>();
		movingthrownmass = new ArrayList<BigMassBlob>();
		this.materialdatalist = materialdata;
		this.teams = teams;
		this.commandscantrunwhenplaying = commandscantrunwhenplaying;
		this.leaderboard = leaderboard;
		if(leaderboard)
		{
			objective = Bukkit.getServer().getScoreboardManager().getNewScoreboard().registerNewObjective(mapname, "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		for(int i = mapbounds[0]; i <= mapbounds[1]; i++)
		{
			for(int j = mapbounds[2]; j <= mapbounds[3]; j++)
			{
				Block b = world.getBlockAt(i, ylevel, j);
				if(!chunks.contains(b.getChunk()))
				{
					chunks.add(b.getChunk());
				}
			}
		}
	}
	
	public int getXMinBounds()
	{
		return mapbounds[0];
	}
	public int getXMaxBounds()
	{
		return mapbounds[1];
	}
	public int getZMinBounds()
	{
		return mapbounds[2];
	}
	public int getZMaxBounds()
	{
		return mapbounds[3];
	}
	public int getYLevel()
	{
		return mapylevel;
	}
	public Material getMapMaterial()
	{
		return mapmaterial;
	}
	public World getWorld()
	{
		return world;
	}
	public double getMassPerBlock()
	{
		return massperblock;
	}
	public ArrayList<AgarPlayer> getPlayers()
	{
		return agarplayers;
	}
	public int getMaxSmallBlobs()
	{
		return maxsmallblobs;
	}
	public boolean isTeamMode()
	{
		return teammode;
	}
	public int getSmallBlobCount()
	{
		return smallblobcount;
	}
	public void setSmallBlobCount(int s)
	{
		smallblobcount = s;
	}
	public double getDecay()
	{
		return decay;
	}
	public ArrayList<GreenVirus> getGreenViruses()
	{
		return greenviruses;
	}
	public ArrayList<RedVirus> getRedViruses()
	{
		return redviruses;
	}
	public String getMapName()
	{
		return mapname;
	}
	public void addBlockUsed(Block b)
	{
		cellblocks.add(b);
	}
	public ArrayList<Block> getBlocksUsed()
	{
		return cellblocks;
	}
	public ArrayList<String> getTeams()
	{
		return teams;
	}
	public int getDefaultSize()
	{
		return defaultsize;
	}
	public Location getSpectatorSpawn()
	{
		return spectatorlocation;
	}
	public double getWalkSpeedMultiplier()
	{
		return walkspeedmultiplier;
	}
	public int getRecombineDelay()
	{
		return recombinedelay;
	}
	public void setRecombineDelay(int s)
	{
		recombinedelay = s;
	}
	public int getSmallMassBlobSpawnRate()
	{
		return smallmassblobspawnrate;
	}
	public boolean useGreenViruses()
	{
		return usegreenviruses;
	}
	public boolean useRedViruses()
	{
		return useredviruses;
	}
	public int getMaxGreenVirus()
	{
		return maxgreenvirus;
	}
	public int getMaxRedVirus()
	{
		return maxredvirus;
	}
	public ArrayList<BigMassBlob> getBigMassBlobsMoving()
	{
		return movingthrownmass;
	}
	public AgarPlayer getAgarPlayer(Player p)
	{
		for(AgarPlayer agp : agarplayers)
		{
			if(agp.getPlayer().equals(p)) return agp;
		}
		return null;
	}
	public double getMassThrowLoss()
	{
		return massthrowloss;
	}
	public double getMassToThrow()
	{
		return masstothrow;
	}
	public double getMinMassVirusSplit()
	{
		return minmassvirussplit;
	}
	public int getGreenVirusSplitSize()
	{
		return greenvirussplitsize;
	}
	public double getMassOfBigMassBlob()
	{
		return massofbigmassblob;
	}
	public int getMaxredVirusMassSpawn()
	{
		return maxredvirusmassspawn;
	}
	public int getRedVirusBlobCount()
	{
		return redvirusblobcount;
	}
	public void setRedVirusBlobCount(int s)
	{
		redvirusblobcount = s;
	}
	public double getMinMassForSplit()
	{
		return minmassforsplit;
	}
	public ArrayList<Integer> getMaterialDataList()
	{
		return materialdatalist;
	}
	public GameMode getGameModeAfterDeath()
	{
		return gamemodeafterdeath;
	}
	public ArrayList<String> getCommandsCantRunWhenPlaying()
	{
		return commandscantrunwhenplaying;
	}
	public boolean contains(Player p)
	{
		for(AgarPlayer player : agarplayers)
		{
			if(p.equals(player.getPlayer())) return true;
		}
		return false;
	}
	public ArrayList<Chunk> getChunks()
	{
		return chunks;
	}
	public Objective getScoreboardObjective()
	{
		return objective;
	}
	public boolean usingLeaderboard()
	{
		return leaderboard;
	}
	public int getMaxPlayerCount()
	{
		return maxplayercount;
	}
}
