package richard.whereami;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import richard.whereami.commands.MarkUnMarkAreas;
import richard.whereami.commands.Where;
import richard.whereami.listener.WhereAmILocationSignListener;
import richard.whereami.listener.WhereAmIPlayerMoveListener;
import richard.whereami.listener.WhereAmIPlayerQuitListener;
import richard.whereami.location.LocationIndex;
import richard.whereami.location.Locator;
import richard.whereami.location.PlayerLocationHistory;

/**
 * WhereAmI for Bukkit
 *
 * @author Richard Popham
 */
public class WhereAmI extends JavaPlugin {
	public static final String WHERE_COMMAND = "where";
	public static final String MARK_COMMAND = "mark";
	public static final String UNMARK_COMMAND = "unmark";
	private final WhereAmIPlayerMoveListener playerListener = new WhereAmIPlayerMoveListener(this);
    private final WhereAmIPlayerQuitListener playerQuitListener = new WhereAmIPlayerQuitListener(this);
    private final WhereAmILocationSignListener locationSignListener = new WhereAmILocationSignListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final Locator locator = new Locator(this);



    public void onEnable() {
    	load();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerQuitListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, locationSignListener, Priority.Normal, this);
        
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this,locator,0,100);
        
        getCommand(WHERE_COMMAND).setExecutor(new Where(this));
        MarkUnMarkAreas markUnMarkAreas =  new MarkUnMarkAreas(this);
        getCommand(MARK_COMMAND).setExecutor(markUnMarkAreas);
        getCommand(UNMARK_COMMAND).setExecutor(markUnMarkAreas);
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    

	private void load() {
	    this.getLocator().load(this.getConfiguration());
    }


	public void sendMessage(final CommandSender sender, final String msg, boolean inMainLoop) {
		if (inMainLoop)
		{
			sender.sendMessage(msg);
		}
		else
		{
			sender.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

				@Override
				public void run() {
					sender.sendMessage(msg);
				}});
		}
		
	}


	public Locator getLocator() {
		return locator;
	}


	public void onDisable() {
		save();
    	PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }
    private void save() {
	    this.getLocator().save(this.getConfiguration());
	    this.getConfiguration().save();
    }


	public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
	
}

