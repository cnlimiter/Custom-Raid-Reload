package cn.evole.mods.craid.api.events;

import cn.evole.mods.craid.common.raid.Raid;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class RaidEvent extends Event {

	private final Raid raid;

	public RaidEvent(Raid raid) {
		this.raid = raid;
	}

	public Raid getRaid() {
		return this.raid;
	}

	public static class RaidStartEvent extends RaidEvent{

		public RaidStartEvent(Raid raid) {
			super(raid);
		}

	}

	/**
	 * cancel this event to avoid reward.
	 */
	@Cancelable
    public static class RaidWinEvent extends RaidEvent{

		public RaidWinEvent(Raid raid) {
			super(raid);
		}

	}

	public static class RaidLossEvent extends RaidEvent{

		public RaidLossEvent(Raid raid) {
			super(raid);
		}

	}

}
