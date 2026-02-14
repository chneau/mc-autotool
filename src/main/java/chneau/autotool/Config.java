package chneau.autotool;

public class Config {
    public enum Strategy { OFF, FIRST, BEST }

    public boolean autoAttackEnabled = true;
    public boolean autoFarmEnabled = true;
    public boolean autoRefillEnabled = true;
    public boolean autoSprintEnabled = true;
    public Strategy autoSwap = Strategy.BEST;
}
