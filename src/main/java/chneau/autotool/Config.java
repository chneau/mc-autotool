package chneau.autotool;

public class Config {
    public enum Strategy { BEST, FIRST }

    public boolean autoAttackEnabled = true;
    public boolean autoFarmEnabled = true;
    public boolean autoSwapEnabled = true;
    public boolean autoRefillEnabled = true;
    public boolean autoSprintEnabled = true;
    public Strategy strategy = Strategy.BEST;
}
