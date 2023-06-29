package cn.evole.mods.craid.common.impl.amount;

import cn.evole.mods.craid.api.IAmountComponent;
import com.google.gson.JsonElement;
import net.minecraft.util.GsonHelper;

public class ConstantAmount implements IAmountComponent {

	public static final String NAME = "count";
	private int cnt;

	public ConstantAmount() {
	}

	@Override
	public int getSpawnAmount() {
		return this.cnt;
	}

	@Override
	public void readJson(JsonElement json) {
		this.cnt = GsonHelper.convertToInt(json, NAME);
	}

}
