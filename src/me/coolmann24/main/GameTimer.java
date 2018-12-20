package me.coolmann24.main;

import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable
{
	private int time, latertime;
	
	public GameTimer()
	{
		time = 0;
		latertime = 0;
	}
	
	@Override
	public void run() 
	{
		if(time == 20)
		{
			time = 0;
			latertime++;
			
			Main.mapmanager.decayCells();
			Main.mapmanager.spawnSmallBlobs();
			Main.mapmanager.spawnRedVirusSmallBlobs();
			Main.mapmanager.returnViruses();
			Main.mapmanager.spawnViruses();
			Main.mapmanager.killCellsOutOfMap();
			
			if(latertime == 2)
			{
				latertime = 0;
				Main.mapmanager.displayMassToPlayers();
			}
		}
		
		
		Main.mapmanager.setCellsWalkSpeed();
		Main.mapmanager.cellSplitUpdate();
		Main.mapmanager.updateEating();
		Main.mapmanager.upDateCellRecombine();
		Main.mapmanager.touchVirusUpdate();
		Main.mapmanager.updateCells();
		Main.mapmanager.showPlayerCellsMass();
		Main.mapmanager.updateThrownMass();
		
		
		time++;
		
		
	}

}
