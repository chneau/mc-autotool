package chneau.autotool;

public class Config {
    public enum Strategy {
        OFF,   // Disable tool swapping
        FIRST, // Pick the first compatible tool found
        BEST   // Pick the most efficient tool (e.g., Diamond over Stone)
    }

    public enum AttackMode {
        OFF,   // Disable auto-attack
        SWORD, // Only auto-attack when a sword is held
        ALL    // Auto-attack with any item held
    }

    public enum SprintMode {
        OFF,       // Disable auto-sprint
        ON,        // Sprint if hunger is above vanilla threshold (6 points)
        HUNGER_50  // Sprint only if hunger is above 50% (10 points)
    }

    public enum FarmMode {
        OFF,     // Disable auto-farming
        HARVEST, // Only harvest mature crops
        BOTH     // Harvest mature crops and replant seeds
    }

    public enum RefillMode {
        OFF,   // Disable auto-refill
        ON,    // Keep the held stack full whenever possible
        SMART  // Only refill when the held stack is down to the last item
    }

    public AttackMode autoAttack = AttackMode.SWORD;
    public FarmMode autoFarm = FarmMode.BOTH;
    public RefillMode autoRefill = RefillMode.ON;
    public SprintMode autoSprint = SprintMode.ON;
    public Strategy autoSwap = Strategy.BEST;
}
