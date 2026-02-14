package chneau.autotool;

public class Config {
    public enum Strategy { OFF, FIRST, BEST }
    public enum AttackMode { OFF, SWORD, ALL }
    public enum SprintMode { OFF, ON, HUNGER_50 }

    public AttackMode autoAttack = AttackMode.SWORD;
    public boolean autoFarmEnabled = true;
    public boolean autoRefillEnabled = true;
    public SprintMode autoSprint = SprintMode.ON;
    public Strategy autoSwap = Strategy.BEST;
}
