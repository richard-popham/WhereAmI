package richard.whereami.listener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.material.MaterialData;

import com.infomatiq.jsi.Rectangle;

import richard.whereami.WhereAmI;
import richard.whereami.location.LocationUtil;
import richard.whereami.location.MapArea;
import richard.whereami.location.MapArea.MapAreaDistanceToPoint;
import richard.whereami.signs.LocationSign;
import richard.whereami.signs.LocationSignAsyncTextProcessor;

public class WhereAmILocationSignListener extends PlayerListener {

	private WhereAmI whereAmI;

	public WhereAmILocationSignListener(WhereAmI whereAmI) {
	   	this.whereAmI = whereAmI;

    }
	
	
	
	@Override
    public void onPlayerInteract(PlayerInteractEvent event) {
	   if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
	   {
		   Block blockClicked = event.getClickedBlock();
		   if (blockClicked!=null)
		   {
				if (event.getClickedBlock().getState() instanceof Sign)
			  	{
			  		Sign sign = (Sign) blockClicked.getState();
			  		String firstLine = sign.getLine(0);
			  		String secondLine = sign.getLine(1);
			  		//click on a sign with ls: on it;
			  		if (firstLine.contains("ls:"))
			  		{
			  			//extract args from the ls on the sign
			  			String[] lineSplit = firstLine.split(":");
			  			if (lineSplit.length>=2)
			  			{
			  				try
			  				{
			  					int recLength = Integer.parseInt(lineSplit[1]);
			  					int recWidth = 300;
			  					if(lineSplit.length>=3)
			  					{
			  						recWidth = Integer.parseInt(lineSplit[2]);
			  					}
			  					
			  					int minRange =0;
			  					int maxRange = Integer.MAX_VALUE;
			  					
			  					if (secondLine.length()>0)
			  					{
			  						String[] secondLineSplit = secondLine.split(":");
			  						try{
			  							minRange = Integer.parseInt(secondLineSplit[0]);
			  						}
			  						catch(Exception e){}
			  						try{
			  							maxRange = Integer.parseInt(secondLineSplit[1]);
			  						}
			  						catch(Exception e){}
			  					}
			  					
			  					Location signLocation = blockClicked.getLocation();
			  					BlockFace blockFacing = getFacing(sign);
			  					Rectangle signAreaRec = calculateRectangle(signLocation,blockFacing,recWidth,recLength);
			  					this.whereAmI.getServer().getScheduler().scheduleAsyncDelayedTask(whereAmI,new LocationSignAsyncTextProcessor(whereAmI,new LocationSign(signLocation.getBlockX(), signLocation.getBlockY(), signLocation.getBlockZ(), firstLine, signAreaRec, blockFacing, minRange,maxRange, blockClicked.getWorld().getName())));
			  					
			  				}
			  				catch(Exception e){}
			  			}
			  		}
			  	} 
		   }
	   }
    }
	
	/**
	 * get the rectangle that the sign creates in front of it to capture the areas within it, 
	 * @param signLocation the positon of the sign in the world
	 * @param blockFacing direction the block is facing the rectangle is created in the opposite direction to this
	 * @param width of rectangle
	 * @param length of rectangle
	 * @return the signs area capture rectangle
	 */
    private Rectangle calculateRectangle(Location signLocation, BlockFace blockFacing, int width, int length) {
    	//start of rectangle is the sign which is in the middle at the bottom
    	double[] midBottom = new double[]{signLocation.getX(), signLocation.getZ()};
    	//get the opposite side which the length of the rectangle in the direction of where the block is facing rotated 180 (negative coordinates)
    	double[] midTop = LocationUtil.addInDirection(midBottom[0], midBottom[1], -blockFacing.getModX(), -blockFacing.getModZ(), length);
    	//need to get the rectangle side at 90 to the sign we then get the opposite corner of the top line
    	int[] bottomRotateDir = LocationUtil.rotate90(-blockFacing.getModX(),-blockFacing.getModZ(),true);
    	double[] bottomOneSide = LocationUtil.addInDirection(midBottom[0], midBottom[1], bottomRotateDir[0], bottomRotateDir[1], width);
    	int[] topRotateDir = LocationUtil.rotate90(-blockFacing.getModX(),-blockFacing.getModZ(),false);
    	double[] topOtherSide = LocationUtil.addInDirection(midTop[0], midTop[1], topRotateDir[0], topRotateDir[1], width);
    	return new Rectangle((float)bottomOneSide[0], (float)bottomOneSide[1], (float)topOtherSide[0], (float)topOtherSide[1]);
    	
    	
    
	    
    }

    /**
	 *  get the sign data and extract the facing enum
	 * @param sign
	 * @return
	 */
	public static BlockFace getFacing(Sign sign) {
    	org.bukkit.material.Sign signMaterialData = (org.bukkit.material.Sign) sign.getData();
    	return signMaterialData.getFacing();
    }

	
}
