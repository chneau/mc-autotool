package chneau.autotool;
public class Config {
	public enum Strategy {
		OFF, FIRST, BEST
	}
	public enum AttackMode {
		OFF, SWORD, OMNI, OMNI_ALL
	}
	public enum SprintMode {
		OFF, ON, HUNGER_50
	}
	public enum FarmMode {
		OFF, HARVEST, BOTH
	}
	public enum RefillMode {
		OFF, ON, SMART
	}
	public enum EatMode {
		OFF, HUNGER, HEALTH, SMART
	}
	public enum SortMode {
		OFF, HOTBAR, INVENTORY, BOTH, ALL
	}
	public enum ArmorMode {
		OFF, BETTER, SMART
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
	public Strategy autoSwap = Strategy.BEST;
	public AttackMode autoAttack = AttackMode.OMNI;
	public FarmMode autoFarm = FarmMode.BOTH;
	public RefillMode autoRefill = RefillMode.ON;
	public SprintMode autoSprint = SprintMode.ON;
	public EatMode autoEat = EatMode.SMART;
	public SortMode autoSort = SortMode.ALL;
	public ArmorMode autoArmor = ArmorMode.SMART;
	public FishMode autoFish = FishMode.ON;
	public int targetMonster = 1, targetPassive = 1, targetPlayer = 1, targetDiamond = 0, targetEmerald = 0,
			targetGold = 0, targetIron = 0, targetDebris = 0, targetChest = 1, targetSpawner = 1, targetLimit = 5,
			targetHudColor = 0xFFFFFFFF;
	public HudPosition targetHudPosition = HudPosition.TOP_LEFT;
	public StepMode autoStep = StepMode.ON;
	public DepositMode autoDeposit = DepositMode.ALL;
	public void resetToDefault() {
		var d = new Config();
		for (var f : getClass().getFields())
			try {
				f.set(this, f.get(d));
			} catch (Exception ignored) {
			}
	}
}
