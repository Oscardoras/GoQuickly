package me.oscardoras.goquickly;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import me.oscardoras.goquickly.commands.Commands;
import me.oscardoras.spigotutils.BukkitPlugin;

public final class GoQuicklyPlugin extends BukkitPlugin implements Listener {
	
	public static GoQuicklyPlugin plugin;
	
	public GoQuicklyPlugin() {
		plugin = this;
	}
	
	
	public static class Spectator {
		public GameMode gameMode;
		public Location location;
	}
	public final Map<Player, Spectator> spectators = new HashMap<Player, Spectator>();
	
	@Override
	public void onLoad() {
		Commands.spawn();
		Commands.suicide();
		Commands.fly();
		Commands.spectator();
		Commands.tprequest();
		Commands.tpcancel();
		Commands.tpaccept();
		Commands.tprefuse();
		Commands.home();
		Commands.addhome();
		Commands.delhome();
	}
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		for (Entry<Player, Spectator> entry : spectators.entrySet()) {
			Player player = entry.getKey();
			Spectator spectator = entry.getValue();
				
			player.teleport(spectator.location);
			player.setGameMode(spectator.gameMode);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onAutoFly(PlayerToggleSprintEvent e) {
	    if (!e.isCancelled()) {
			Player player = e.getPlayer();
			if (player.isFlying() && (new TeleportablePlayer(player).isAutoFly() || spectators.containsKey(player))) {
				if (player.isSprinting()) player.setFlySpeed(0.1f);
				else player.setFlySpeed(1f);
			}
	    }
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (spectators.containsKey(player)) {
			Spectator spectator = spectators.get(player);
			player.teleport(spectator.location);
			player.setGameMode(spectator.gameMode);
		}
	}
	
}