package richard.whereami.location;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import richard.whereami.WhereAmI;

public class Locator implements Runnable{

	public LocationIndex getLocationIndex() {
    	return locationIndex;
    }

	private WhereAmI whereAmI;
    private LocationIndex locationIndex = new LocationIndex();
    private ConcurrentHashMap<String, double[]> playerLocationMap = new ConcurrentHashMap<String, double[]>();
	private ConcurrentHashMap<String, PlayerLocationHistory> playerLocationHistoryMap = new ConcurrentHashMap<String, PlayerLocationHistory>();
	
	public ConcurrentHashMap<String, PlayerLocationHistory> getPlayerLocationHistoryMap() {
		return playerLocationHistoryMap;
	}


	public Locator(WhereAmI whereAmI) {
		this.whereAmI = whereAmI;
		
	}
	

	


	@Override
	public void run() {
		for (Entry<String, double[]> playerLocationEntry : getPlayerLocationMap().entrySet())
			{
				double[] currentLocationXYZ = playerLocationEntry.getValue();
				//get everything the location is within (distance = 0 to the area)
				List<MapArea> insideOf = locationIndex.getNearest(currentLocationXYZ[0], currentLocationXYZ[1], currentLocationXYZ[2],10,0);
				MapArea closest = MapArea.nearestToPoint(currentLocationXYZ[0], currentLocationXYZ[1], currentLocationXYZ[2],insideOf);
				PlayerLocationHistory locationHistory = playerLocationHistoryMap.get(playerLocationEntry.getKey());
				if (locationHistory!=null)
				{
					if (closest!=null && locationHistory.previousLocation!=null &&!locationHistory.previousLocation.equals(closest))
					{
						playerLocationHistoryMap.get(playerLocationEntry.getKey()).previousLocation = closest;
						reportLocation(playerLocationEntry.getKey(),closest);
					}
					
				}
				else
				{
					locationHistory = new PlayerLocationHistory();
					playerLocationHistoryMap.put(playerLocationEntry.getKey(), locationHistory);
					playerLocationHistoryMap.get(playerLocationEntry.getKey()).previousLocation = closest;
					if (closest!=null)
					{
						reportLocation(playerLocationEntry.getKey(),closest);
					}
				}
				playerLocationHistoryMap.get(playerLocationEntry.getKey()).previousLocationXYZ = currentLocationXYZ;
			}
		
	
		
	}

	/**
	 * send message to player reporting entering of new area
	 * @param playerName the players name
	 * @param areaIn
	 */
	private void reportLocation(final String playerName, final MapArea areaIn) {
		final Server server = this.whereAmI.getServer();
		server.getScheduler().scheduleSyncDelayedTask(this.whereAmI, new Runnable() {
			
			@Override
			public void run() {
				server.getPlayer(playerName).sendMessage("Entering "+areaIn.getName());
				
			}
		});
		
	}
	
	public ConcurrentHashMap<String, double[]> getPlayerLocationMap() {
		return playerLocationMap;
	}


	public void load(Configuration configuration) {
	    this.getLocationIndex().load(configuration);
	    
    }


	public void save(Configuration configuration) {
		this.getLocationIndex().save(configuration);
	    
    }

	



}
