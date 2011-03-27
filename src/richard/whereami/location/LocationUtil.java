package richard.whereami.location;

public class LocationUtil {
	public static double distance(double fromX, double fromY, double fromZ, double toX, double toY, double toZ)
	{
		double dx = fromX-toX;
		double dy = fromY-toY;
		double dz = fromZ-toZ;
		return Math.sqrt((dx*dx)+(dy*dy)+(dz*dz));
	}

	/**
     * take the unit vector and multiple each part by the distance to get the new coordinates distaceToAdd in the direction of the unit vector
     * @param x starting x
     * @param z starting z
     * @param dirX the X coordinate of the direction to go a distance by
     * @param dirZ the Z coordinate of the direction to go a distance by
     * @param distanceToAdd
     * @return new position
     */
    public static double[] addInDirection(double x, double z,double dirX, double dirZ, double distanceToAdd) {
    	//calc unit vector 
    	double directionLength = Math.sqrt((dirX*dirX)+(dirZ*dirZ));
    	dirX=dirX/directionLength;
    	dirZ=dirZ/directionLength;;
    	//calc new x,z in the direction 
    	return new double[]{(distanceToAdd*dirX) +x,(distanceToAdd*dirZ) +z};
    	
    }

	/**
     * to rotate 90 we switch x and z coordinates and negate one
     * @param modX
     * @param modZ
     * @param negativeX
     * @return x,z rotated 90
     */
    public static int[] rotate90(int modX, int modZ, boolean negativeX) {
        int tempZ = modZ;
        modZ = modX;
        modX= tempZ;
        return new int[]{negativeX ? -modX : modX,!negativeX ? -modZ : modZ};
    }
}
