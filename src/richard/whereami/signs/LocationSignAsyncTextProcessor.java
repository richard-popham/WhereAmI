package richard.whereami.signs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;

import richard.whereami.WhereAmI;
import richard.whereami.location.LocationUtil;
import richard.whereami.location.MapArea;
import richard.whereami.location.MapArea.MapAreaDistanceToPoint;

public class LocationSignAsyncTextProcessor implements Runnable{

	private WhereAmI whereAmI;
	private LocationSign locationSign;

	public LocationSignAsyncTextProcessor(WhereAmI whereAmI, LocationSign locationSign) {
	    this.whereAmI = whereAmI;
	    this.locationSign=locationSign;
    }
	

		
		@Override
		public void run() {
			List<MapArea> mapAreas = whereAmI.getLocator().getLocationIndex(locationSign.world).areaIntersects(locationSign.signArea);
			mapAreas.removeAll(whereAmI.getLocator().getLocationIndex(locationSign.world).getNearest(locationSign.blockX,locationSign.blockY,locationSign.blockZ, 10, 0));

			List<MapAreaDistanceToPoint> sortedDistanceMapAreas = MapArea.sortedDistanceToPoint(mapAreas, locationSign.blockX,locationSign.blockY,locationSign.blockZ);
			List<String> newSignStrings = new ArrayList<String>();
			for (MapAreaDistanceToPoint mapAreaDistanceToPoint : sortedDistanceMapAreas)
			{
				if (mapAreaDistanceToPoint.distance>locationSign.minRange && mapAreaDistanceToPoint.distance<locationSign.maxRange)
				{
					newSignStrings.add(signText(mapAreaDistanceToPoint));
				}
			}
			changeSignText(locationSign,newSignStrings);
		}

		private void changeSignText(final LocationSign locationSign,final List<String> newSignStrings) {
           whereAmI.getServer().getScheduler().scheduleSyncDelayedTask(whereAmI, new Runnable() {
			
			@Override
			public void run() {
				//look for signs around this block
				List<Sign> signs = getSignsAround(locationSign,whereAmI.getServer().getWorld(locationSign.world));
				
				
				for (int i=0;i<signs.size();i++)
				{
					setStringListAtIndexToSignBlock(signs.get(i),newSignStrings,i*4,4);
				}
			}

			private List<Sign> getSignsAround(LocationSign locationSign, World world) {

				List<Sign> signs = new ArrayList<Sign>();
				Block startBlock = world.getBlockAt(locationSign.blockX, locationSign.blockY, locationSign.blockZ);
				if (startBlock!=null && startBlock.getState() instanceof Sign)
				{
					
					signs.add((Sign) startBlock.getState());
					
					//find signs below this
					boolean lastBlockSign = true;
		            int blockX = locationSign.blockX;
		            int blockY = locationSign.blockY;
		            int blockZ = locationSign.blockZ;
					while(lastBlockSign)
					{
						
						Block currentBlock = world.getBlockAt(blockX, --blockY, blockZ);
						if (currentBlock!=null && currentBlock.getState() instanceof Sign)
						{
							lastBlockSign = true;
							signs.add((Sign) currentBlock.getState());
						}
						else
						{
							lastBlockSign = false;
						}
					}
					
					blockX = locationSign.blockX;
		            blockY = locationSign.blockY;
		            blockZ = locationSign.blockZ;
		            int[] xzRoatation = LocationUtil.rotate90(locationSign.blockFace.getModX(), locationSign.blockFace.getModZ(), false);
		        	lastBlockSign = true;
					while(lastBlockSign)
					{
						
						Block currentBlock = world.getBlockAt(blockX+=xzRoatation[0], blockY, blockZ+=xzRoatation[1]);
						System.out.println(blockX+" "+blockY+" "+blockZ);
						if (currentBlock!=null && currentBlock.getState() instanceof Sign)
						{
							lastBlockSign = true;
							signs.add((Sign) currentBlock.getState());
						}
						else
						{
							lastBlockSign = false;
						}
					}
					
				}
				return signs;
            }

			private void setStringListAtIndexToSignBlock(final Sign sign, final List<String> signStrings, final int startAtIndex, final int noForSign) {
				
				//signs don't seem to update in the client if they are next to each other unless update done in different tick
				//use the start index offset to update on that tick
				whereAmI.getServer().getScheduler().scheduleSyncDelayedTask(whereAmI, new Runnable() {
					
					@Override
					public void run() {
						for(int i=startAtIndex;i<newSignStrings.size()&&i<(startAtIndex+noForSign);i++)
						{
							sign.setLine(i-startAtIndex, newSignStrings.get(i));
						}
						System.out.println("updating sign at "+sign.getX()+" "+sign.getY()+" "+sign.getZ());
						sign.update();
						
					}
				},startAtIndex*5);
				
	            
            }
		});
            
        }

		private String signText(MapAreaDistanceToPoint mapAreaDistanceToPoint) {
            return areaText(mapAreaDistanceToPoint.mapArea)+" "+distanceText(mapAreaDistanceToPoint.distance);
        }

		private String distanceText(Double distance) {
			NumberFormat formatter = NumberFormat.getInstance();
			Integer distanceInt = distance.intValue();
			int digits = distanceInt.toString().length();
			String returnString;
			if (digits <=3)
			{
				returnString =  distanceInt.toString();
			}
			else
			{
				if (digits <=4)
				{
					formatter.setMaximumFractionDigits(1);
					returnString =  formatter.format(distance/1000)+"k";
				}
				else
				{
					if (digits <=5)
					{
						formatter.setMaximumFractionDigits(0);
						returnString =  formatter.format(distance/1000)+"k";
					}
					else
					{

						returnString = "VFAR";
						
					}
				}
			}
			return insertSpaces(returnString,4-returnString.length(),false);
			
           
        }

		private String areaText(MapArea mapArea) {
            // TODO Auto-generated method stub
			String name = mapArea.getName();
			if (name.length()>10)
			{
				name = reduceChars(name,name.length()-10,10);
			}
			return insertSpaces(name,10-name.length(),true);
        }

		private String insertSpaces(String name, int spaceCount,boolean append) {
			if (!append)
			{
				System.out.println(name+" "+spaceCount);
			}
           	StringBuffer buffer = new StringBuffer(name);
           	for(int i=0;i<spaceCount;i++)
           	{
           		if (append)
           		{
           			buffer.append(" ");
           		}
           		else
           		{
           			buffer.insert(0," ");
           		}
           	}
           	if (!append)
			{
				System.out.println(buffer.toString()+" "+buffer.toString().length());
			}
           	return buffer.toString();
        }

		private String reduceChars(String name, int by, int lengthAim) {
			System.out.println(name+" "+by+" "+lengthAim);
           String[] nameSplit = name.split(" |-");
           System.out.println(nameSplit.length);
           if (nameSplit.length>1)
           {
        	   String reducedString = null;
        	   reduceStrings(nameSplit,by);
        	   for (String namePart : nameSplit)
        	   {
        		   if (reducedString==null)
        		   {
        			   reducedString=namePart;
        		   }
        		   else
        		   {
        			   reducedString+=(" "+namePart);
        		   }
        	   }
        	   return reducedString;
           }
           else
           {
        	   return name.substring(0,lengthAim);
           }
            
        }

		private void reduceStrings(String[] nameSplit, int by) {
			System.out.println(nameSplit.length);
		   if (by<=0)
		   {
			   return;
		   }
           int minPos = posOfMinStringLength(nameSplit);
           String original = nameSplit[minPos];
           int maxBy = original.length()-1;
           String reduced;
           if (maxBy>by)
           {
        	   reduced = original.substring(0,original.length()-by);
           }
           else
           {
        	   reduced = original.substring(0,1);
           }
          
           nameSplit[minPos] = reduced;
           int reducedBy = original.length()-reduced.length();
           if (reducedBy==0)
           {
        	   return;
           }
           else
           {
        	   reduceStrings(nameSplit, by - reducedBy);
           }
        }

		private int posOfMinStringLength(String[] nameSplit) {
			int min=Integer.MAX_VALUE;
			int pos=0;
            for(int i=0; i<nameSplit.length;i++)
            {
            	int partLength = nameSplit[i].length();
            	if (partLength<min && partLength!=1)
            	{
            		pos = i;
            		min = partLength;
            	}
            }
            return pos;
        }
	
	

}
