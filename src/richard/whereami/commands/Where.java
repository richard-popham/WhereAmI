package richard.whereami.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import richard.whereami.WhereAmI;
import richard.whereami.location.PlayerLocationHistory;

public class Where implements CommandExecutor {

	private WhereAmI whereAmI;

	public Where(WhereAmI whereAmI) {
	    this.whereAmI = whereAmI;
    }

	public String generateWhereText() {

		StringBuffer whereBuffer = new StringBuffer();
		for (Player player : whereAmI.getServer().getOnlinePlayers()) {
			PlayerLocationHistory locationHistory = whereAmI.getLocator().getPlayerLocationHistoryMap().get(player.getName());
			if (locationHistory != null && locationHistory.previousLocation != null) {
				whereBuffer.append(player.getName() + " - " + locationHistory.previousLocation.getName() + "\n");
			}

		}
		return whereBuffer.toString();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		whereAmI.sendMessage(sender, generateWhereText(), true);
		return true;
	}

}
