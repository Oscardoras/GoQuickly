package org.bukkitplugin.goquickly;

import org.bukkitutils.io.TranslatableMessage;

public class Message extends TranslatableMessage {
	
	public Message(String path, String... arg) {
		super(GoQuicklyPlugin.plugin, path, arg);
	}
	
}