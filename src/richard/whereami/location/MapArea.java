package richard.whereami.location;

import java.util.List;

import com.infomatiq.jsi.Rectangle;

public class MapArea {

	public double x;
	public double y;
	public double z;
	private int px; //south
	private int mx; //north
	private int mz; //east
	private int pz; //west
	private String name;
	private Rectangle locationArea;
	
	//in minecraft x runs south+/north- z runs  west+/east- and y up down
	private MapArea(){}
	public MapArea(Rectangle locationArea, double x, double y, double z, int px, int mx, int pz, int mz,
			String name) {
		this.x = x;
		this.y=y;
		this.z=z;
		this.px=px;
		this.mx = mx;
		this.pz=pz;
		this.mz=mz;
		this.name=name;
		this.locationArea=locationArea;
	}

	public Rectangle getLocationArea() {
		return locationArea;
	}

	public String getName() {
		return name;
	}
	
	public static MapArea nearestToPoint(double x, double y, double z, List<MapArea> insideOf) {
		double minDistance=Double.MAX_VALUE;
		MapArea closest = null;
		//work out which area mark point this point is closest to
		for (MapArea location : insideOf)
		{
		
			double distance = LocationUtil.distance(x,y,z,location.x,location.y,location.z);
			if (distance<minDistance)
			{
				minDistance = distance;
				closest=location;
			}
		}
		return closest;
    }

}