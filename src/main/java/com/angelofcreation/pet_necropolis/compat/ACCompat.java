package com.angelofcreation.pet_necropolis.compat;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.fml.ModList;

public class ACCompat {
    public static int getVariantTag(LivingEntity entity) {
        if (entity instanceof DinosaurEntity dinosaur) {
            return dinosaur.getAltSkin();
        }

        if (entity instanceof VallumraptorEntity vallumraptor) {
            return (vallumraptor.isElder() ? 1 : 0) << 2 | (vallumraptor.getAltSkin());
        }

        if (entity instanceof CandicornEntity candicorn) {
            return candicorn.getVariant();
        }
        return 0;
    }

    public static int getCollarTag(LivingEntity entity) {
        if (entity instanceof DinosaurEntity dinosaur) {
            switch (dinosaur.getAltSkin()) {
                case 1:
                    return DyeColor.PURPLE.getId();
                case 2:
                    return DyeColor.ORANGE.getId();
            }
        }

        if (entity instanceof CandicornEntity candicorn) {
            return DyeColor.PINK.getId();
        }
        return 14;
    }

    public static TamableAnimal getRespawnedEntity(TamableAnimal pet, CompoundTag tag) {
        if (pet instanceof DinosaurEntity dinosaur) {
            dinosaur.setAltSkin(tag.getInt(PCUtil.PET_VARIANT));
            pet = dinosaur;
        }

        if (pet instanceof VallumraptorEntity vallumraptor) {
            vallumraptor.setElder((tag.getInt(PCUtil.PET_VARIANT) >>> 2) == 1);
            vallumraptor.setAltSkin((tag.getInt(PCUtil.PET_VARIANT) & 3));
            return vallumraptor;
        }

        if (pet instanceof CandicornEntity candicorn) {
            candicorn.setVariant(tag.getInt(PCUtil.PET_VARIANT));
            return candicorn;
        }
        return pet;
    }

    public static String getVariantTranslatable(LivingEntity pet, CompoundTag tag) {
        String variant = "";
        if (pet instanceof DinosaurEntity && !(pet instanceof VallumraptorEntity)) {
            switch (tag.getInt(PCUtil.PET_VARIANT)) {
                case 1:
                    variant = Component.translatable("variant.alexscaves.dinosaur.retro").getString();
                    break;
                case 2:
                    variant = Component.translatable("variant.alexscaves.dinosaur.tectonic").getString();
                    break;
            }
        }

        if (pet instanceof VallumraptorEntity) {
            switch (tag.getInt(PCUtil.PET_VARIANT) & 3) {
                case 1:
                    variant = Component.translatable("variant.alexscaves.dinosaur.retro").getString();
                    break;
                case 2:
                    variant = Component.translatable("variant.alexscaves.dinosaur.tectonic").getString();
                    break;
            }
            variant = ((tag.getInt(PCUtil.PET_VARIANT) >>> 2) == 1) ? variant + " " + Component.translatable("variant.alexscaves.vallumraptor.elder").getString() : variant;
            variant = variant.stripLeading();
        }

        if (pet instanceof CandicornEntity) {
            switch (tag.getInt(PCUtil.PET_VARIANT)) {
                case 0:
                    variant = Component.translatable("variant.alexscaves.candicorn.candy_corn").getString();
                    break;
                case 1:
                    variant = Component.translatable("variant.alexscaves.candicorn.peppermint").getString();
                    break;
                case 2:
                    variant = Component.translatable("variant.alexscaves.candicorn.mint_chocolate_chip").getString();
                    break;
                case 3:
                    variant = Component.translatable("variant.alexscaves.candicorn.popsicle").getString();
                    break;
                case 4:
                    variant = Component.translatable("variant.alexscaves.candicorn.cotton_candy").getString();
                    break;
            }
        }

        if (ModList.get().isLoaded("alexscavesexemplified")) {
            if (pet instanceof TremorzillaEntity) {
                switch (tag.getInt(PCUtil.PET_VARIANT) & 3) {
                    case 1:
                        variant = Component.translatable("variant.alexscaves.dinosaur.retro").getString();
                        break;
                    case 2:
                        variant = Component.translatable("variant.alexscaves.dinosaur.tectonic").getString();
                        break;
                }
                variant = ((tag.getInt(PCUtil.PET_VARIANT) >>> 2) == 1) ? variant + " " + Component.translatable("variant.alexscavesexemplified.tremorzilla.gammafied").getString() : variant;
                variant = variant.stripLeading();
            }
        }

        return variant;
    }
}
