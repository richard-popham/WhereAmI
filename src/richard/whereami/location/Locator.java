package richard.whereami.location;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.google.gson.Gson;

import richard.whereami.WhereAmI;

public class Locator implements Runnable{

	private static final String CONFIG_LOCATION_INDEX_DATA = "locationIndex.data";
	public LocationIndex getLocationIndex(String world) {
    	LocationIndex index = locationIndexMap.get(world);
    	if (index==null)
    	{
    		index = new LocationIndex(world);
    		locationIndexMap.put(world, index);
    	}
    	return index;
    }

	private WhereAmI whereAmI;
    private ConcurrentHashMap<String, LocationIndex> locationIndexMap = new ConcurrentHashMap<String, LocationIndex>();
    private ConcurrentHashMap<String, double[]> playerLocationMap = new ConcurrentHashMap<String, double[]>();
	private ConcurrentHashMap<String, PlayerLocationHistory> playerLocationHistoryMap = new ConcurrentHashMap<String, PlayerLocationHistory>();
	private ConcurrentHashMap<String, String> playerWorldMap = new ConcurrentHashMap<String, String>();
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
				List<MapArea> insideOf = getLocationIndex((playerWorldMap.get(playerLocationEntry.getKey()))).getNearest(currentLocationXYZ[0], currentLocationXYZ[1], currentLocationXYZ[2],10,0);
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


	public void save(Configuration configuration)
	{
		List<MapArea> mapAreaFromAllWorlds = new ArrayList<MapArea>();
		for(LocationIndex locationIndex : locationIndexMap.values())
		{
			mapAreaFromAllWorlds.addAll(locationIndex.mapAreaHashMap.values());
		}
		configuration.setProperty(CONFIG_LOCATION_INDEX_DATA, new Gson().toJson(mapAreaFromAllWorlds.toArray(new MapArea[]{})));
		
	}
	
	public void load(Configuration configuration)
	{
		for(LocationIndex locationIndex : locationIndexMap.values())
		{
			locationIndex.clear();
		}
		String dataToLoad = configuration.getString(CONFIG_LOCATION_INDEX_DATA);
		if (dataToLoad!=null&&dataToLoad.length()>0)
		{
			MapArea[] mapAreaData =  new Gson().fromJson(dataToLoad, MapArea[].class);
			for (MapArea mapArea : mapAreaData)
			{
				if (mapArea.getWorld()!=null)
				{
					getLocationIndex(mapArea.getWorld()).addLocation(mapArea);
				}
				else
				{
					mapArea.setWorld(whereAmI.getServer().getWorlds().get(0).getName());
					getLocationIndex(whereAmI.getServer().getWorlds().get(0).getName()).addLocation(mapArea);
				}
			}
		}
	}


	public ConcurrentHashMap<String, String> getPlayerWorldMap() {
	    // TODO Auto-generated method stub
	    return playerWorldMap;
    }

	



}
