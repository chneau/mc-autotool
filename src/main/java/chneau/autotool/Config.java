package chneau.autotool;

public class Config {
    public enum Strategy { OFF, FIRST, BEST }
    public enum AttackMode { OFF, SWORD, ALL }

    public AttackMode autoAttack = AttackMode.SWORD;
    public boolean autoFarmEnabled = true;
    public boolean autoRefillEnabled = true;
    public boolean autoSprintEnabled = true;
    public Strategy autoSwap = Strategy.BEST;
}
