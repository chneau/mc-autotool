package chneau.autotool;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
public class ConfigScreen extends BaseConfigScreen {
	public ConfigScreen(Screen parent, Options options) {
		super(parent, options, Component.literal("Autotool Config"));
	}
	@Override
	protected void addOptions() {
		Config config = ConfigManager.getConfig();
		this.list.addSmall(
				createEnumOption("Auto Attack",
						"Attack entities you look at using cooldown. SWORD mode only with swords.",
						Config.AttackMode.values(), config.autoAttack, v -> config.autoAttack = v),
				createEnumOption("Auto Farm", "Harvest mature crops in a 3x3 area. BOTH mode also replants.",
						Config.FarmMode.values(), config.autoFarm, v -> config.autoFarm = v));
		this.list.addSmall(
				createEnumOption("Auto Fish", "Automatically reel in and recast when a fish bites.",
						Config.FishMode.values(), config.autoFish, v -> config.autoFish = v),
				createEnumOption("Auto Eat",
						"Automatically eat when hungry/injured and inactive. SMART optimizes food usage.",
						Config.EatMode.values(), config.autoEat, v -> config.autoEat = v));
		this.list.addSmall(
				createEnumOption("Auto Swap", "Swap to the best hotbar tool or weapon when attacking.",
						Config.Strategy.values(), config.autoSwap, v -> config.autoSwap = v),
				createEnumOption("Auto Refill", "Refill held stack from inventory. SMART only refills last item.",
						Config.RefillMode.values(), config.autoRefill, v -> config.autoRefill = v));
		this.list.addSmall(
				createEnumOption("Auto Sort", "Sort hotbar and inventory when the inventory screen is opened.",
						Config.SortMode.values(), config.autoSort, v -> config.autoSort = v),
				createEnumOption("Auto Armor", "Equip best armor from inventory. SMART mode considers enchantments.",
						Config.ArmorMode.values(), config.autoArmor, v -> config.autoArmor = v));
		this.list.addSmall(
				createEnumOption("Auto Deposit", "Add a 'D' button to containers to deposit items already in them.",
						Config.DepositMode.values(), config.autoDeposit, v -> config.autoDeposit = v),
				createEnumOption("Auto Sprint", "Toggle sprinting. HUNGER_50 requires 50% hunger (10 points).",
						Config.SprintMode.values(), config.autoSprint, v -> config.autoSprint = v));
		this.list.addSmall(
				createEnumOption("Auto Step", "Increase step height to 1 block, allowing you to walk up full blocks.",
						Config.StepMode.values(), config.autoStep, v -> config.autoStep = v),
				null);
		this.list.addBig(new OptionInstance<>("Targeting Settings...", OptionInstance.noTooltip(),
				(caption, value) -> Component.empty(), OptionInstance.BOOLEAN_VALUES, true,
				(v) -> this.minecraft.setScreen(new TargetConfigScreen(this, this.options))));
	}
	@Override
	protected void addFooter() {
		LinearLayout linearLayout = LinearLayout.horizontal().spacing(8);
		linearLayout.addChild(Button.builder(Component.literal("Reset to Defaults"), (button) -> {
			ConfigManager.getConfig().resetToDefault();
			ConfigManager.save();
			AutoStep.update();
			this.minecraft.setScreen(new ConfigScreen(this.lastScreen, this.options));
		}).width(150).build());
		linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			this.onClose();
		}).width(150).build());
		this.layout.addToFooter(linearLayout);
	}
}
