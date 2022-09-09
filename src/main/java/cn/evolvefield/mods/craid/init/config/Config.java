package cn.evolvefield.mods.craid.init.config;

import com.google.gson.annotations.Expose;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/9 21:22
 * Version: 1.0
 */
public class Config {

    @Expose
    boolean enableRaid = true;

    @Expose
    int raidWaitTime = 400;

    @Expose
    int raidRange = 50;

    public boolean isEnableRaid() {
        return enableRaid;
    }

    public int getRaidWaitTime() {
        return raidWaitTime;
    }

    public int getRaidRange() {
        return raidRange;
    }
}
