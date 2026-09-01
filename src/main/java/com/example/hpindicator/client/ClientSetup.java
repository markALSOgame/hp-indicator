package com.example.hpindicator.client;

import com.example.hpindicator.HpIndicatorMod;
import com.example.hpindicator.ModConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@Mod.EventBusSubscriber(modid = HpIndicatorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModConfig.load();
            ClientRegistry.registerKeyBinding(ModKeys.OPEN_MENU);
        });
    }
}
