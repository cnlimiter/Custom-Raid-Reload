package cn.evolvefield.mods.craid.common.raid;

import cn.evolvefield.mods.craid.CRaid;
import cn.evolvefield.mods.craid.api.IRaidComponent;
import cn.evolvefield.mods.craid.api.StringUtil;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class RaidLoader implements SimpleSynchronousResourceReloadListener {

	private static final Map<ResourceLocation, IRaidComponent> RAID_MAP = Maps.newHashMap();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final String directory = "raids";
	private static final String PATH_SUFFIX = ".json";
	private static final int PATH_SUFFIX_LENGTH = ".json".length();

	@Override
	public ResourceLocation getFabricId() {
		return new ResourceLocation(CRaid.MOD_ID, directory);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
		int i = this.directory.length() + 1;
		for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(directory, (resourceLocationx) -> resourceLocationx.getPath().endsWith(".json")).entrySet()) {
			ResourceLocation resourceLocation = (ResourceLocation) entry.getKey();
			String string = resourceLocation.getPath();
			ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getNamespace(), string.substring(i, string.length() - PATH_SUFFIX_LENGTH));
			try {
				Reader reader = entry.getValue().openAsReader();
				try {
					JsonElement jsonElement = GsonHelper.fromJson(GSON, reader, JsonElement.class);
					if (jsonElement != null) {
						JsonElement jsonElement2 = map.put(resourceLocation2, jsonElement);
						if (jsonElement2 != null) {
							throw new IllegalStateException("Duplicate data file ignored with ID " + resourceLocation2);
						}
					} else {
						CRaid.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceLocation2, resourceLocation);
					}
				} catch (Throwable var14) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var13) {
							var14.addSuppressed(var13);
						}
					}

					throw var14;
				}
				reader.close();
			} catch (IllegalArgumentException | IOException | JsonParseException var15) {
				CRaid.LOGGER.error("Couldn't parse data file {} from {}", new Object[]{resourceLocation2, resourceLocation, var15});
			}
		}
		/* refresh */
		RAID_MAP.clear();

		/* load */
		map.forEach((res, jsonElement) -> {
			try {
				JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "raid");
				String type = GsonHelper.getAsString(jsonObject, StringUtil.TYPE, "test");
				IRaidComponent raid = RaidManager.getRaidType(type);
				if(!raid.readJson(jsonObject)) {
					CRaid.LOGGER.debug("Skipping loading custom raid {} as it's conditions were not met", res);
					return;
				}
				RAID_MAP.put(res, raid);
			} catch (IllegalArgumentException | JsonParseException e) {
				CRaid.LOGGER.error("Parsing error loading custom raid {}: {}", res, e.getMessage());
			}
		});

		/* finish */
		RaidManager.finishRaidMap(RAID_MAP);

		CRaid.LOGGER.info("Loaded {} Custom Raids", RAID_MAP.size());

	}
}
