package cn.evolvefield.mods.craid.init.callback;

import cn.evolvefield.mods.craid.common.raid.Raid;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/9 21:29
 * Version: 1.0
 */
public class RaidCallback {
    public static final Event<RaidLossTick> RAID_LOSS_TICK_EVENT = EventFactory.createArrayBacked(RaidLossTick.class,
            listeners -> raid -> {
                for (RaidLossTick callback : listeners) {
                    callback.onRaidLossTick(raid);
                }
            }
    );

    @FunctionalInterface
    public interface RaidLossTick {
        void onRaidLossTick(Raid raid);
    }

    public static final Event<RaidStartTick> RAID_START_TICK_EVENT = EventFactory.createArrayBacked(RaidStartTick.class,
            listeners -> raid -> {
                for (RaidStartTick callback : listeners) {
                    callback.onRaidStartTick(raid);
                }
            }
    );

    @FunctionalInterface
    public interface RaidStartTick {
        void onRaidStartTick(Raid raid);
    }

    public static final Event<RaidWinTick> RAID_WIN_TICK_EVENT = EventFactory.createArrayBacked(RaidWinTick.class,
            listeners -> raid -> {
                for (RaidWinTick callback : listeners) {
                    if (callback.onRaidWinTick(raid)) return true;
                }
                return false;
            }
    );

    @FunctionalInterface
    public interface RaidWinTick {
        boolean onRaidWinTick(Raid raid);
    }
}
