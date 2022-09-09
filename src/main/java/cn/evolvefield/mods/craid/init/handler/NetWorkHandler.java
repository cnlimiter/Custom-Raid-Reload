package cn.evolvefield.mods.craid.init.handler;

import cn.evolvefield.mods.craid.CRaid;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/9 19:59
 * Version: 1.0
 */
public class NetWorkHandler {
    public static void onRun() {
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(CRaid.MOD_ID, "main"),
                (minecraft, packetListener, buf, sender) -> {
                    SoundEvent sound = Registry.SOUND_EVENT.get(buf.readResourceLocation());
                    Minecraft.getInstance().player.playSound(sound, 1F, 1F);
                });
    }
}
