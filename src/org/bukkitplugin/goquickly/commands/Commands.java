package org.bukkitplugin.goquickly.commands;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkitplugin.goquickly.GoQuicklyPlugin;
import org.bukkitplugin.goquickly.GoQuicklyPlugin.Spectator;
import org.bukkitplugin.goquickly.Message;
import org.bukkitplugin.goquickly.TeleportablePlayer;
import org.bukkitutils.command.v1_15_V1.Argument;
import org.bukkitutils.command.v1_15_V1.CommandRegister;
import org.bukkitutils.command.v1_15_V1.LiteralArgument;
import org.bukkitutils.command.v1_15_V1.CommandRegister.CommandExecutorType;
import org.bukkitutils.command.v1_15_V1.arguments.EntitySelectorArgument;
import org.bukkitutils.command.v1_15_V1.arguments.FloatArgument;
import org.bukkitutils.command.v1_15_V1.arguments.StringArgument;
import org.bukkitutils.command.v1_15_V1.arguments.EntitySelectorArgument.EntitySelector;

public final class Commands {
	private Commands() {}
	
	
	public static void spawn() {
		CommandRegister.register("spawn", new LinkedHashMap<>(), new Permission("goquickly.command.spawn"), CommandExecutorType.ENTITY, (cmd) -> {
			((Entity) cmd.getExecutor()).teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			cmd.broadcastMessage(new Message("command.spawn"));
			return 1;
		});
	}
	
	public static void suicide() {
		CommandRegister.register("suicide", new LinkedHashMap<>(), new Permission("goquickly.command.suicide"), CommandExecutorType.ENTITY, (cmd) -> {
			if (cmd.getExecutor() instanceof LivingEntity) ((LivingEntity) cmd.getExecutor()).setHealth(0d);
			else ((Entity) cmd.getExecutor()).remove();
			return 1;
		});
	}
	
	public static void fly() {
		LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
		arguments.put("enable", new LiteralArgument("enable"));
		CommandRegister.register("fly", arguments, new Permission("goquickly.command.fly"), CommandExecutorType.PLAYER, (cmd) -> {
			((Player) cmd.getExecutor()).setAllowFlight(true);
			new TeleportablePlayer((Player) cmd.getExecutor()).setAutoFly(false);
			cmd.broadcastMessage(new Message("command.fly.enable"));
			return 1;
		});
		
		arguments = new LinkedHashMap<>();
		arguments.put("disable", new LiteralArgument("disable"));
		CommandRegister.register("fly", arguments, new Permission("goquickly.command.fly"), CommandExecutorType.PLAYER, (cmd) -> {
			((Player) cmd.getExecutor()).setFlying(false);
			((Player) cmd.getExecutor()).setAllowFlight(false);
			new TeleportablePlayer((Player) cmd.getExecutor()).setAutoFly(false);
			cmd.broadcastMessage(new Message("command.fly.disable"));
			return 1;
		});
		
		arguments = new LinkedHashMap<>();
		arguments.put("speed_literal", new LiteralArgument("speed"));
		arguments.put("speed", new FloatArgument(0, 10));
		CommandRegister.register("fly", arguments, new Permission("goquickly.command.fly"), CommandExecutorType.PLAYER, (cmd) -> {
			((Player) cmd.getExecutor()).setAllowFlight(true);
			((Player) cmd.getExecutor()).setFlySpeed(((float) cmd.getArg(0)) / 10f);
			new TeleportablePlayer((Player) cmd.getExecutor()).setAutoFly(false);
			cmd.broadcastMessage(new Message("command.fly.speed", ""+cmd.getArg(0)));
			return 1;
		});
		
		arguments = new LinkedHashMap<>();
		arguments.put("auto", new LiteralArgument("auto"));
		CommandRegister.register("fly", arguments, new Permission("goquickly.command.fly"), CommandExecutorType.PLAYER, (cmd) -> {
			((Player) cmd.getExecutor()).setAllowFlight(true);
			((Player) cmd.getExecutor()).setFlySpeed(0.1f);
			new TeleportablePlayer((Player) cmd.getExecutor()).setAutoFly(true);
			cmd.broadcastMessage(new Message("command.fly.auto"));
			return 1;
		});
	}
	
	public static void spectator() {
		CommandRegister.register("spectator", new LinkedHashMap<>(), new Permission("goquickly.command.spectator"), CommandExecutorType.PLAYER, (cmd) -> {
			GoQuicklyPlugin pl = GoQuicklyPlugin.plugin;
			Player player = (Player) cmd.getExecutor();
			if (player.getGameMode() == GameMode.SPECTATOR) {
				if (pl.spectators.containsKey(player)) {
					Spectator spectator = pl.spectators.get(player);
					player.teleport(spectator.location);
					player.setGameMode(spectator.gameMode);
					pl.spectators.remove(player);
				} else player.setGameMode(Bukkit.getDefaultGameMode());
				cmd.broadcastMessage(new Message("command.spectator.disable"));
			} else {
				Spectator spectator = new Spectator();
				spectator.location = player.getLocation();
				spectator.gameMode = player.getGameMode();
				pl.spectators.put(player, spectator);
				player.setGameMode(GameMode.SPECTATOR);
				cmd.broadcastMessage(new Message("command.spectator.enable"));
			}
			return 1;
		});
	}
	
	public static void tprequest() {
		LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
		arguments.put("targets", new EntitySelectorArgument(EntitySelector.ONE_PLAYER));
		CommandRegister.register("tprequest", arguments, new Permission("goquickly.command.tprequest"), CommandExecutorType.PLAYER, (cmd) -> {
			Player target = (Player) cmd.getArg(0);
			new TeleportablePlayer((Player) cmd.getExecutor()).tpRequest(target);
			cmd.broadcastMessage(new Message("command.tprequest", target.getName()));
			return 1;
		});
	}
	
	public static void tpcancel() {
		CommandRegister.register("tpcancel", new LinkedHashMap<>(), new Permission("goquickly.command.tpcancel"), CommandExecutorType.PLAYER, (cmd) -> {
			if (new TeleportablePlayer((Player) cmd.getExecutor()).tpCancel()) {
				cmd.broadcastMessage(new Message("command.tpcancel"));
				return 1;
			} else {
				cmd.sendFailureMessage(new Message("tp.does_not_exist"));
				return 0;
			}
		});
	}
	
	public static void tpaccept() {
		CommandRegister.register("tpaccept", new LinkedHashMap<>(), new Permission("goquickly.command.tpaccept"), CommandExecutorType.PLAYER, (cmd) -> {
			if (new TeleportablePlayer((Player) cmd.getExecutor()).tpAccept()) {
				cmd.broadcastMessage(new Message("command.tpaccept"));
				return 1;
			} else {
				cmd.sendFailureMessage(new Message("tp.does_not_exist"));
				return 0;
			}
		});
	}
	
	public static void tprefuse() {
		CommandRegister.register("tprefuse", new LinkedHashMap<>(), new Permission("goquickly.command.tprefuse"), CommandExecutorType.PLAYER, (cmd) -> {
			if (new TeleportablePlayer((Player) cmd.getExecutor()).tpRefuse()) {
				cmd.broadcastMessage(new Message("command.tprefuse"));
				return 1;
			} else {
				cmd.sendFailureMessage(new Message("tp.does_not_exist"));
				return 0;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void home() {
		CommandRegister.register("home", new LinkedHashMap<>(), new Permission("goquickly.command.home"), CommandExecutorType.PLAYER, (cmd) -> {
			Set<String> homes = new TeleportablePlayer((Player) cmd.getExecutor()).getHomes().keySet();
			cmd.sendListMessage(homes, new Object[] {new Message("command.home.list")}, new Object[] {new Message("command.home.empty")});
			return homes.size();
		});
		
		LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
		arguments.put("home", new HomeArgument());
		CommandRegister.register("home", arguments, new Permission("goquickly.command.home"), CommandExecutorType.PLAYER, (cmd) -> {
			Entry<String, Location> home = (Entry<String, Location>) cmd.getArg(0);
			((Player) cmd.getExecutor()).teleport(home.getValue());
			cmd.broadcastMessage(new Message("command.home.teleport", home.getKey()));
			return 1;
		});
	}
	
	public static void addhome() {
		LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
		arguments.put("home", new StringArgument());
		CommandRegister.register("addhome", arguments, new Permission("goquickly.command.addhome"), CommandExecutorType.PLAYER, (cmd) -> {
			if (new TeleportablePlayer((Player) cmd.getExecutor()).addHome((String) cmd.getArg(0), cmd.getLocation())) {
				cmd.broadcastMessage(new Message("command.addhome", (String) cmd.getArg(0)));
				return 1;
			} else {
				cmd.sendFailureMessage(new Message("home.already_exists", (String) cmd.getArg(0)));
				return 0;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void delhome() {
		LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
		arguments.put("home", new HomeArgument());
		CommandRegister.register("delhome", arguments, new Permission("goquickly.command.delhome"), CommandExecutorType.PLAYER, (cmd) -> {
			String home = ((Entry<String, Location>) cmd.getArg(0)).getKey();
			new TeleportablePlayer((Player) cmd.getExecutor()).deleteHome(home);
			cmd.broadcastMessage(new Message("command.delhome", home));
			return 1;
		});
	}
	
}