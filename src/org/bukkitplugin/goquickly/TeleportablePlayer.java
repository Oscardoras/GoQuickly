package org.bukkitplugin.goquickly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkitutils.io.ConfigurationFile;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class TeleportablePlayer {
    
	protected static final Map<Player, Player> tpRequests = new HashMap<Player, Player>();
	
	
	protected final Player player;
	
	public TeleportablePlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isAutoFly() {
		ConfigurationFile config = new ConfigurationFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
		if (config.contains("autofly")) return config.getBoolean("autofly");
		return false;
	}
	
	public void setAutoFly(boolean autoFly) {
		ConfigurationFile config = new ConfigurationFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
		config.set("autofly", autoFly);
		config.save();
	}
	
	public Location getHome(String name) {
		for (World world : Bukkit.getWorlds()) {
			ConfigurationFile config = new ConfigurationFile(world.getWorldFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
			if (config.contains("homes." + name)) {
				ConfigurationSection section = config.getConfigurationSection("homes." + name);
				try {
					return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Map<String, Location> getHomes() {
		Map<String, Location> homes = new HashMap<String, Location>();
		for (World world : Bukkit.getWorlds()) {
			ConfigurationFile config = new ConfigurationFile(world.getWorldFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
			if (config.contains("homes")) {
				for (String name : config.getConfigurationSection("homes").getKeys(false)) {
					ConfigurationSection section = config.getConfigurationSection("homes." + name);
					try {
						homes.put(name, new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch")));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			}
		}
		return homes;
	}
	
	public boolean addHome(String name, Location location) {
		ConfigurationFile config = new ConfigurationFile(location.getWorld().getWorldFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
		if (!config.contains("homes." + name)) {
			config.createSection("homes." + name);
			ConfigurationSection section = config.getConfigurationSection("homes." + name);
			section.set("x", location.getX());
			section.set("y", location.getY());
			section.set("z", location.getZ());
			section.set("yaw", location.getYaw());
			section.set("pitch", location.getPitch());
			config.save();
			return true;
		}
		return false;
	}
	
	public void deleteHome(String name) {
		for (World world : Bukkit.getWorlds()) {
			ConfigurationFile config = new ConfigurationFile(world.getWorldFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
			if (config.contains("homes." + name)) {
				config.set("homes." + name, null);
				break;
			}
		}
	}
	
	public void tpRequest(Player target) {
		tpCancel();
		tpRequests.put(target, player);
		
		String uuid = UUID.randomUUID().toString();
		String[] text = new Message("tp.request").getMessage(target, player.getName(), uuid, uuid).split(uuid);
		if (text.length >= 3) {
			TextComponent component = new TextComponent();
			component.addExtra(text[0]);
			TextComponent accept = new TextComponent("/tpaccept");
			accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpaccept"));
			accept.setColor(ChatColor.GREEN);
			component.addExtra(accept);
			component.addExtra(text[1]);
			TextComponent refuse = new TextComponent("/tprefuse");
			refuse.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tprefuse"));
			refuse.setColor(ChatColor.GREEN);
			component.addExtra(refuse);
			component.addExtra(text[2]);
			target.spigot().sendMessage(component);
		} else target.sendMessage(new Message("tp.request").getMessage(target, player.getName(), "/tpaccept", "/tprefuse"));
	}
	
	public boolean tpCancel() {
		boolean found = false;
		List<Player> toRemove = new ArrayList<Player>();
		for (Entry<Player, Player> entry : tpRequests.entrySet()) {
			if (entry.getValue().equals(player)) {
				Player target = entry.getKey();
				toRemove.add(target);
				target.sendMessage(new Message("tp.cancel").getMessage(target, player.getName()));
				found = true;
			}
		};
		for (Player target : toRemove) tpRequests.remove(target);
		return found;
	}
	
	public boolean tpAccept() {
		if (tpRequests.containsKey(player)) {
			Player sender = tpRequests.get(player);
			sender.sendMessage(new Message("tp.accept").getMessage(sender, player.getName()));
			sender.teleport(player.getLocation());
			tpRequests.remove(player);
			return true;
		}
		return false;
	}
	
	public boolean tpRefuse() {
		if (tpRequests.containsKey(player)) {
			Player sender = tpRequests.get(player);
			sender.sendMessage(new Message("tp.refuse").getMessage(sender, player.getName()));
			tpRequests.remove(player);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object object) {
		return object != null && object instanceof TeleportablePlayer && player.equals(((TeleportablePlayer) object).player);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 4 + player.hashCode();
		return hash;
	}
	
}