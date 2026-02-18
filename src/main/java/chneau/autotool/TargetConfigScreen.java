package chneau.autotool;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
public class TargetConfigScreen extends BaseConfigScreen {
	public TargetConfigScreen(Screen parent, Options options) {
		super(parent, options, Component.literal("Targeting Settings"));
	}
	private enum HudColor {
		WHITE(0xFFFFFFFF), YELLOW(0xFFFFFF00), RED(0xFFFF0000), AQUA(0xFF00FFFF), GREEN(0xFF00FF00);
		final int color;
		HudColor(int color) {
			this.color = color;
		}
	}
	@Override
	protected void addOptions() {
		Config config = ConfigManager.getConfig();
		this.list.addSmall(
				createIntOption("👹 Monsters", "Max number of monsters to show on HUD.", config.targetMonster,
						v -> config.targetMonster = v),
				createIntOption("🐷 Passives", "Max number of passive mobs to show on HUD.", config.targetPassive,
						v -> config.targetPassive = v));
		this.list.addSmall(
				createIntOption("👤 Players", "Max number of players to show on HUD.", config.targetPlayer,
						v -> config.targetPlayer = v),
				createIntOption("📦 Chests", "Max number of chests/barrels to show on HUD.", config.targetChest,
						v -> config.targetChest = v));
		this.list.addSmall(
				createIntOption("👾 Spawners", "Max number of mob spawners to show on HUD.", config.targetSpawner,
						v -> config.targetSpawner = v),
				createIntOption("💎 Diamond", "Max number of diamond ores to show on HUD.", config.targetDiamond,
						v -> config.targetDiamond = v));
		this.list.addSmall(
				createIntOption("🟢 Emerald", "Max number of emerald ores to show on HUD.", config.targetEmerald,
						v -> config.targetEmerald = v),
				createIntOption("🟡 Gold", "Max number of gold ores to show on HUD.", config.targetGold,
						v -> config.targetGold = v));
		this.list.addSmall(
				createIntOption("⚪ Iron", "Max number of iron ores to show on HUD.", config.targetIron,
						v -> config.targetIron = v),
				createIntOption("🧱 Debris", "Max number of ancient debris to show on HUD.", config.targetDebris,
						v -> config.targetDebris = v));
		HudColor currentColor = HudColor.WHITE;
		for (HudColor hc : HudColor.values()) {
			if (hc.color == config.targetHudColor) {
				currentColor = hc;
				break;
			}
		}
		this.list.addSmall(
				createEnumOption("HUD Position", "The corner of the screen where the target list is shown.",
						Config.HudPosition.values(), config.targetHudPosition, v -> config.targetHudPosition = v),
				createIntOption("HUD Limit", "Total maximum number of targets to show on HUD.", config.targetLimit, 10,
						v -> config.targetLimit = v));
		this.list.addSmall(createEnumOption("HUD Color", "Text color for the HUD targets.", HudColor.values(),
				currentColor, v -> config.targetHudColor = v.color), null);
	}
	@Override
	protected void addFooter() {
		LinearLayout linearLayout = LinearLayout.horizontal().spacing(8);
		linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			this.onClose();
		}).width(150).build());
		this.layout.addToFooter(linearLayout);
	}
}
