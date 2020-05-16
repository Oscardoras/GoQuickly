package org.bukkitplugin.goquickly.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkitplugin.goquickly.Message;
import org.bukkitplugin.goquickly.TeleportablePlayer;
import org.bukkitutils.command.v1_14_3_V1.CustomArgument;

public class HomeArgument extends CustomArgument<Entry<String, Location>> {

	public HomeArgument() {
		withSuggestionsProvider((cmd) -> {
			return new TeleportablePlayer((Player) cmd.getExecutor()).getHomes().keySet();
		});
	}

	@Override
	public Entry<String, Location> parse(String arg, SuggestedCommand cmd) throws Exception {
		Location home = new TeleportablePlayer((Player) cmd.getExecutor()).getHome(arg);
		if (home == null) throw new CustomArgumentException(new Message("home.does_not_exist").getMessage(cmd.getLanguage(), arg));
		else {
			Map<String, Location> map = new HashMap<String, Location>();
			map.put(arg, home);
			return map.entrySet().iterator().next();
		}
	}
	
}