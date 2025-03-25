package com.angelofcreation.pet_necropolis.compat;

import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import org.crimsoncrips.alexscavesexemplified.misc.interfaces.Gammafied;

public class ACECompat {
    public static int getVariantTag(LivingEntity entity) {
        if (entity instanceof TremorzillaEntity tremorzilla) {
            Gammafied gammafied = (Gammafied) tremorzilla;
            return (gammafied.isGamma() ? 1 : 0) << 2 | (tremorzilla.getAltSkin());
        }
        return 0;
    }

    public static TamableAnimal getRespawnedEntity(TamableAnimal pet, CompoundTag tag) {
        if (pet instanceof TremorzillaEntity tremorzilla) {
            if (tag.getInt(PCUtil.PET_VARIANT) > 3) {
                Gammafied gammafied = (Gammafied) tremorzilla;
                gammafied.setGamma((tag.getInt(PCUtil.PET_VARIANT) >>> 2) == 1);
                tremorzilla.setAltSkin((tag.getInt(PCUtil.PET_VARIANT) & 3));
            } else {
                tremorzilla.setAltSkin(tag.getInt(PCUtil.PET_VARIANT) & 3);
            }
            return tremorzilla;
        }
        return pet;
    }
}
