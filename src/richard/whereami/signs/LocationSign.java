package richard.whereami.signs;

import org.bukkit.block.BlockFace;

import com.infomatiq.jsi.Rectangle;

public class LocationSign
{
	public int blockX;
	public int blockY;
	public int blockZ;
	public String[] signText;
	public String originalText;
	public Rectangle signArea;
	public BlockFace blockFace;
	public int maxRange;
	public int minRange;
	public LocationSign(int blockX, int blockY, int blockZ, String originalText, Rectangle signArea, BlockFace blockFace, int minRange, int maxRange) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.originalText = originalText;
        this.signArea = signArea;
        this.blockFace = blockFace;
        this.minRange = minRange;
        this.maxRange = maxRange;
    }
	
	
}