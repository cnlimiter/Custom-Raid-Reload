package cn.evolvefield.mods.craid.init.handler;

import cn.evolvefield.mods.craid.common.raid.RaidLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/9 19:08
 * Version: 1.0
 */
public class ResourceHandler {
    public static void addReloadListenerEvent(){
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new RaidLoader());
    }
}
