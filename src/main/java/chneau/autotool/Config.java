package chneau.autotool;
public class Config {
	public enum Strategy {
		OFF, // Disable tool swapping
		FIRST, // Pick the first compatible tool found
		BEST // Pick the most efficient tool (e.g., Diamond over Stone)
	}
	public enum AttackMode {
		OFF, // Disable auto-attack
		SWORD, // Only auto-attack when a sword is held
		ALL // Auto-attack with any item held
	}
	public enum SprintMode {
		OFF, // Disable auto-sprint
		ON, // Sprint if hunger is above vanilla threshold (6 points)
		HUNGER_50 // Sprint only if hunger is above 50% (10 points)
	}
	public enum FarmMode {
		OFF, // Disable auto-farming
		HARVEST, // Only harvest mature crops
		BOTH // Harvest mature crops and replant seeds
	}
	public enum RefillMode {
		OFF, // Disable auto-refill
		ON, // Keep the held stack full whenever possible
		SMART // Only refill when the held stack is down to the last item
	}
	public enum EatMode {
		OFF, // Disable auto-eat
		HUNGER, // Eat whenever hunger is missing
		HEALTH, // Only eat when health is not full
		SMART // Optimize food usage and avoid over-eating
	}
	public enum SortMode {
		OFF, // Disable auto-sort
		HOTBAR, // Only sort the hotbar
		INVENTORY, // Only sort the main inventory
		BOTH, // Sort both hotbar and main inventory
		ALL // Sort everything including external containers
	}
	public enum ArmorMode {
		OFF, // Disable auto-armor
		BETTER, // Equip armor with higher raw armor value
		SMART // Consider enchantments alongside armor value
	}
	public enum FishMode {
		OFF, ON
	}
	public enum HudPosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}
	public enum StepMode {
		OFF, ON
	}
	public enum DepositMode {
		OFF, CHEST, FURNACE, ALL
	}
	public Strategy autoSwap;
	public AttackMode autoAttack;
	public FarmMode autoFarm;
	public RefillMode autoRefill;
	public SprintMode autoSprint;
	public EatMode autoEat;
	public SortMode autoSort;
	public ArmorMode autoArmor;
	public FishMode autoFish;
	public int targetMonster;
	public int targetPassive;
	public int targetPlayer;
	public int targetDiamond;
	public int targetEmerald;
	public int targetGold;
	public int targetIron;
	public int targetDebris;
	public HudPosition targetHudPosition;
	public int targetLimit;
	public int targetHudColor;
	public int targetChest;
	public int targetSpawner;
	public StepMode autoStep;
	public DepositMode autoDeposit;
	public Config() {
		resetToDefault();
	}
	public void resetToDefault() {
		this.autoAttack = AttackMode.SWORD;
		this.autoFarm = FarmMode.BOTH;
		this.autoRefill = RefillMode.ON;
		this.autoSprint = SprintMode.ON;
		this.autoEat = EatMode.SMART;
		this.autoSort = SortMode.ALL;
		this.autoArmor = ArmorMode.SMART;
		this.autoFish = FishMode.ON;
		this.targetMonster = 1;
		this.targetPassive = 0;
		this.targetPlayer = 0;
		this.targetDiamond = 0;
		this.targetEmerald = 0;
		this.targetGold = 0;
		this.targetIron = 0;
		this.targetDebris = 0;
		this.targetChest = 0;
		this.targetSpawner = 0;
		this.targetLimit = 5;
		this.targetHudColor = 0xFFFFFFFF;
		this.targetHudPosition = HudPosition.TOP_LEFT;
		this.autoStep = StepMode.ON;
		this.autoDeposit = DepositMode.ALL;
		this.autoSwap = Strategy.BEST;
	}
}
