package com.angelofcreation.pet_necropolis;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PetNecropolis.MODID)
public class PetNecropolis {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "pet_necropolis";

    public PetNecropolis(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
