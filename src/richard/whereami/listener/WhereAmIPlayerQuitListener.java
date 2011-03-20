package richard.whereami.listener;

import java.io.*;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;
import org.bukkit.*;

import richard.whereami.WhereAmI;





/**
 * Handle events for all Player related events
 * @author <Your Name>
 */
public class WhereAmIPlayerQuitListener extends PlayerListener {
    private final WhereAmI plugin;

    public WhereAmIPlayerQuitListener(WhereAmI instance) {
        plugin = instance;
    }

	@Override
	public void onPlayerQuit(PlayerEvent event) {
		plugin.getLocator().getPlayerLocationMap().remove(event.getPlayer().getName());
	}

   
    

}

