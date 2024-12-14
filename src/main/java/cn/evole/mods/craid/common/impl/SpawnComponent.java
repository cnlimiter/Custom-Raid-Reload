package cn.evole.mods.craid.common.impl;

import cn.evole.mods.craid.CRaid;
import cn.evole.mods.craid.CRaidUtil;
import cn.evole.mods.craid.api.IAmountComponent;
import cn.evole.mods.craid.api.IPlacementComponent;
import cn.evole.mods.craid.api.ISpawnComponent;
import cn.evole.mods.craid.api.StringUtil;
import cn.evole.mods.craid.common.impl.amount.ConstantAmount;
import cn.evole.mods.craid.common.raid.RaidManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map.Entry;

public class SpawnComponent implements ISpawnComponent {

	public static final String NAME = "default";
	private EntityType<?> entityType;
	private IAmountComponent spawnAmount = new ConstantAmount();
	private CompoundTag nbt = new CompoundTag();
	private IPlacementComponent placement;
	private int spawnTick;
	private boolean glowing;

	@Override
	public boolean readJson(JsonObject json) {

		/* entity type */
		this.entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(GsonHelper.getAsString(json, StringUtil.ENTITY_TYPE, "")));
		if(this.entityType == null) {
			throw new JsonSyntaxException("entity type cannot be empty or wrong format");
		}

		/* spawn amount */
		{
			JsonObject obj = GsonHelper.getAsJsonObject(json, StringUtil.SPAWN_AMOUNT);
	        if(obj != null && ! obj.entrySet().isEmpty()) {
	    	    for(Entry<String, JsonElement> entry : obj.entrySet()) {
	    		    final IAmountComponent tmp = RaidManager.getSpawnAmount(entry.getKey());
	    		    if(tmp != null) {
	    			    tmp.readJson(entry.getValue());
	    			    this.spawnAmount = tmp;
	    		    } else {
	    			    CRaid.LOGGER.warn("Amount Component : Read Spawn Amount Wrongly");
	    		    }
	    		    break;
	    	    }
	        }
		}

	    /* spawn placement */
		this.placement = CRaidUtil.readPlacement(json, false);

		/* spawn tick */
		this.spawnTick = GsonHelper.getAsInt(json, StringUtil.SPAWN_TICK, 0);

		/* glowing */
		this.glowing = GsonHelper.getAsBoolean(json, StringUtil.GLOWING, false);

		/* nbt */
		if(json.has(StringUtil.ENTITY_NBT)) {
			try {
			    nbt = TagParser.parseTag(GsonHelper.convertToString(json.get(StringUtil.ENTITY_NBT), StringUtil.ENTITY_NBT));
		    } catch (CommandSyntaxException e) {
			    throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
		    }
		}

		return true;
	}

	@Override
	public int getSpawnTick() {
		return this.spawnTick;
	}

	@Override
	public int getSpawnAmount() {
		return this.spawnAmount.getSpawnAmount();
	}

	@Override
	public boolean glowing() {
		return this.glowing;
	}

	@Override
	public IPlacementComponent getPlacement() {
		return this.placement;
	}

	@Override
	public CompoundTag getNBT() {
		return this.nbt;
	}

	@Override
	public EntityType<?> getSpawnType() {
		return this.entityType;
	}

}
