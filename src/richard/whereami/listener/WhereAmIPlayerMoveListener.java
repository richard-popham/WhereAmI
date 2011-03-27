package richard.whereami.listener;

import java.io.*;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;
import org.bukkit.*;

import richard.whereami.WhereAmI;

/**
 * Handle events for all Player related events
 * @author <Your Name>
 */

public class WhereAmIPlayerMoveListener extends PlayerListener {
    private final WhereAmI plugin;

    public WhereAmIPlayerMoveListener(WhereAmI instance) {
        plugin = instance;
    }

	@Override
	public void onPlayerJoin(PlayerEvent event) {
		location(event);
	}

	private void location(PlayerEvent event) {
		Location location = event.getPlayer().getLocation();
    	double[] locationArray = {location.getX(),location.getY(),location.getZ()};
    	plugin.getLocator().getPlayerLocationMap().put(event.getPlayer().getName(), locationArray);
    	plugin.getLocator().getPlayerWorldMap().put(event.getPlayer().getName(), event.getPlayer().getWorld().getName());
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		location(event);
	}

	@Override
	public void onPlayerTeleport(PlayerMoveEvent event) {
		location(event);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		location(event);
	}


    

}

