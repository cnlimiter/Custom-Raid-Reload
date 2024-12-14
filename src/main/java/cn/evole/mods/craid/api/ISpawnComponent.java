package cn.evole.mods.craid.api;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

public interface ISpawnComponent {

	/**
	 * make sure constructer has no argument,
	 * and use this method to initiate instance.
	 */
	boolean readJson(JsonObject json);

	int getSpawnTick();

	int getSpawnAmount();

	boolean glowing();//可以配置袭击生物是否带有永久发光效果

	IPlacementComponent getPlacement();

	CompoundTag getNBT();

	EntityType<?> getSpawnType();
}
