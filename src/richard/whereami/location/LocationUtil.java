package richard.whereami.location;

public class LocationUtil {
	public static double distance(double fromX, double fromY, double fromZ, double toX, double toY, double toZ)
	{
		double dx = fromX-toX;
		double dy = fromY-toY;
		double dz = fromZ-toZ;
		return Math.sqrt((dx*dx)+(dy*dy)+(dz*dz));
	}
}
