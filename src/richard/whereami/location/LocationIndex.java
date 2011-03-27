package richard.whereami.location;

import gnu.trove.TIntProcedure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.util.config.Configuration;

import com.google.gson.Gson;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

public class LocationIndex {


	RTree rTree;
	int indexInt = 0;
	public HashMap<Integer, MapArea> mapAreaHashMap = new HashMap<Integer, MapArea>();
	public HashMap<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
	private String world;
	
	public String getWorld() {
    	return world;
    }

	public LocationIndex(String world)
	{
		clear();
		this.world = world;
	}
	
	public synchronized int getSize()
	{
		return mapAreaHashMap.size();
	}
	
	public void clear() {
		rTree = new RTree();
		rTree.init(null);
		mapAreaHashMap.clear();
	    
    }

	public void addLocation(double x,double y,double z, int northSouthEastWest,  String name)
	{
		addLocation(x, y, z, northSouthEastWest, northSouthEastWest, northSouthEastWest, northSouthEastWest, name);
	}
	
	public void addLocation(double x,double y,double z, int northSouth, int eastwest,  String name)
	{
		addLocation(x, y, z, northSouth, northSouth, eastwest, eastwest, name);
	}
	
	public void addLocation(double x,double y,double z, int px, int mx, int pz, int mz, String name)
	{
		//rectangle is ((x+px),(y+py) top right and then bottom left (y-my,x-mx)
		Rectangle rectangle = new Rectangle((float)x+px, (float)z+pz, (float)x-mx, (float)z-mz);
		MapArea mapArea = new MapArea(rectangle,x,y,z,px,mx,pz,mz,name, world);
		addLocation(mapArea);
	}
	public synchronized void addLocation(MapArea mapArea)
	{
		int locationIndex = indexInt++;
		rTree.add(mapArea.getLocationArea(), locationIndex);
		mapAreaHashMap.put(locationIndex,mapArea);
		nameToIndexMap.put(mapArea.getName(), locationIndex);
	}
	
	public synchronized boolean removeLocation(String name) {
	  Integer index = nameToIndexMap.get(name);
	  if (index!=null)
	  {
		  MapArea mapArea = mapAreaHashMap.get(index);
		  nameToIndexMap.remove(name);
		  mapAreaHashMap.remove(index);
		  return rTree.delete(mapArea.getLocationArea(), index);

	  }
	  
	  return false;	    
    }
	
	public List<MapArea> areaContains(Rectangle rectangle)
	{
		final List<MapArea> mapAreaList = new ArrayList<MapArea>();
		rTree.contains(rectangle, new TIntProcedure() {
			
			@Override
			public boolean execute(int index) {
				mapAreaList.add(mapAreaHashMap.get(new Integer(index)));
				return true;
			}
		});
		return mapAreaList;
	}
	
	public List<MapArea> areaIntersects(Rectangle rectangle)
	{
		final List<MapArea> mapAreaList = new ArrayList<MapArea>();
		rTree.intersects(rectangle, new TIntProcedure() {
			
			@Override
			public boolean execute(int index) {
				mapAreaList.add(mapAreaHashMap.get(new Integer(index)));
				return true;
			}
		});
		return mapAreaList;
	}
	
	public String removeLocationNearest(double x,double y, double z) {
		MapArea mapArea = MapArea.nearestToPoint(x,y,z,getNearest(x, y, z, 10, 1000));
		if (mapArea!=null)
		{
			if (removeLocation(mapArea.getName()))
			{
				return mapArea.getName();
			}
		}
		return null;
	    
    }
	
	public synchronized List<MapArea> getNearest(double x,double y, double z, int limit, int distanceLimit)
	{
		final List<MapArea> mapAreaList = new ArrayList<MapArea>();
		final Point currentPoint = new Point((float)x, (float)z);
		rTree.nearestNUnsorted(currentPoint,new TIntProcedure() {
			
			@Override
			public boolean execute(int index) {
				mapAreaList.add(mapAreaHashMap.get(new Integer(index)));
				return true;
			}
		},limit, distanceLimit);
		return mapAreaList;
	}
	
	

	


	
}
