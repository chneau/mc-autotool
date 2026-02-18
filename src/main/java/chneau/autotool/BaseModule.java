package chneau.autotool;

import net.minecraft.client.Minecraft;

public abstract class BaseModule implements Module {
	protected final String name;
	protected BaseModule(String name) {
		this.name = name;
	}
	protected static Config config() {
		return ConfigManager.getConfig();
	}
	protected static Minecraft client() {
		return Minecraft.getInstance();
	}
}
