package richard.whereami.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import richard.whereami.WhereAmI;

public class MarkUnMarkAreas implements CommandExecutor {

	private final WhereAmI whereAmI;

	public MarkUnMarkAreas(WhereAmI whereAmI) {
		this.whereAmI = whereAmI;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals(WhereAmI.MARK_COMMAND)) {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				// get location name and bounding
				if (args.length >= 2) {
					final int boundSize = Integer.parseInt(args[0]);
					final String name = join(1, args);
					Location location = player.getLocation();
					final double x = location.getX();
					final double y = location.getY();
					final double z = location.getZ();
					final String world = player.getWorld().getName();
					sender.getServer().getScheduler().scheduleAsyncDelayedTask(whereAmI, new Runnable() {

						@Override
						public void run() {
							whereAmI.getLocator().getLocationIndex(world).addLocation(x, y, z, boundSize, name);
							whereAmI.sendMessage(player, "Added " + name, false);
						}
					});
				}
			}
		}
		if (command.getName().equals(WhereAmI.UNMARK_COMMAND)) {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				// get location name and bounding
				String argString = null;
				if (args.length > 0) {
					argString = join(0, args);
				}
				final String name = argString;
				Location location = player.getLocation();
				final double x = location.getX();
				final double y = location.getY();
				final double z = location.getZ();
				final String world = player.getWorld().getName();
				sender.getServer().getScheduler().scheduleAsyncDelayedTask(whereAmI, new Runnable() {

					@Override
					public void run() {
						if (name != null) {
							if (whereAmI.getLocator().getLocationIndex(world).removeLocation(name))
							{
								whereAmI.sendMessage(player, "Removed " + name, false);
							}
							else
							{
								whereAmI.sendMessage(player, "Nothing named "+name+" to remove!", false);
							}
							
						} else {
							String areaNameRemoved = whereAmI.getLocator().getLocationIndex(world).removeLocationNearest(x,y,z);
							if (areaNameRemoved!=null)
							{
								whereAmI.sendMessage(player, "Removed " + areaNameRemoved, false);
							}
							else
							{
								whereAmI.sendMessage(player, "Nothing to remove!", false);
							}
						}
						
					}
				});

			}
		}

		return true;
	}

	private String join(int startAt, String[] args) {
		StringBuffer joinBuffer = new StringBuffer();
		for (int i = startAt; i < args.length; i++) {
			if (i == startAt) {
				joinBuffer.append(args[i]);
			} else {
				joinBuffer.append(" " + args[i]);
			}
		}
		return joinBuffer.toString();
	}

}
