package chneau.autotool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mc-autotool.json");
	private static Config instance;
	public static Config getConfig() {
		if (instance == null) {
			load();
		}
		return instance;
	}
	public static void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
				instance = GSON.fromJson(reader, Config.class);
			} catch (Exception e) {
				Main.LOGGER.error("Failed to load config, using defaults", e);
				instance = new Config();
				save(); // Overwrite the invalid config with valid defaults
			}
		} else {
			instance = new Config();
			save();
		}
	}
	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException e) {
			Main.LOGGER.error("Failed to save config", e);
		}
	}
}
