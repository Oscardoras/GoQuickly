package me.oscardoras.goquickly;

import me.oscardoras.spigotutils.io.TranslatableMessage;

public class Message extends TranslatableMessage {
	
	public Message(String path, String... arg) {
		super(GoQuicklyPlugin.plugin, path, arg);
	}
	
}