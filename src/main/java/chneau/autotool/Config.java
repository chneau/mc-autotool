package chneau.autotool;

public class Config {
    public enum Strategy { BEST, FIRST }

    public boolean autoAttackEnabled = true;
    public boolean autoFarmEnabled = true;
    public boolean autoToolEnabled = true;
    public boolean autoRefillEnabled = true;
    public long defaultAttackDelayMs = 625;
    public Strategy strategy = Strategy.BEST;
}
