package me.oscardoras.goquickly.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.oscardoras.goquickly.Message;
import me.oscardoras.goquickly.TeleportablePlayer;
import me.oscardoras.spigotutils.command.v1_16_1_V1.CustomArgument;

public class HomeArgument extends CustomArgument<Entry<String, Location>> {

	public HomeArgument() {
		withSuggestionsProvider((cmd) -> {
			return new TeleportablePlayer((Player) cmd.getExecutor()).getHomes().keySet();
		});
	}

	@Override
	public Entry<String, Location> parse(String arg, SuggestedCommand cmd) throws Exception {
		Location home = new TeleportablePlayer((Player) cmd.getExecutor()).getHome(arg);
		if (home == null) throw getCustomException(new Message("home.does_not_exist").getMessage(cmd.getLanguage(), arg));
		else {
			Map<String, Location> map = new HashMap<String, Location>();
			map.put(arg, home);
			return map.entrySet().iterator().next();
		}
	}
	
}