package com.angelofcreation.pet_necropolis.compat;

import com.github.alexthe666.alexsmobs.entity.*;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

public class AMCompat {
    public static int getVariantTag(LivingEntity entity) {
        if (entity instanceof EntityTarantulaHawk tarantulaHawk) {
            return tarantulaHawk.isNether() ? 1 : 0;
        }

        if (entity instanceof EntityElephant elephant) {
            return (elephant.isTusked() ? 1 : 0) << 1 | (elephant.isTrader() ? 1 : 0);
        }

        if (entity instanceof EntityCapuchinMonkey capuchinMonkey) {
           return capuchinMonkey.getVariant();
        }

        if (entity instanceof EntityCrocodile crocodile) {
            return crocodile.isDesert() ? 1 : 0;
        }

        if (entity instanceof EntityMantisShrimp mantisShrimp) {
            return mantisShrimp.getVariant();
        }

        if (entity instanceof EntityGorilla gorilla) {
            return gorilla.isSilverback() ? 1 : 0;
        }
        return 0;
    }

    public static TamableAnimal getRespawnedEntity(TamableAnimal pet, CompoundTag tag) {
        if (pet instanceof EntityTarantulaHawk tarantulaHawk) {
            tarantulaHawk.setNether(tag.getInt(PCUtil.PET_VARIANT) == 1);
            return tarantulaHawk;
        }

        if (pet instanceof EntityElephant elephant) {
            elephant.setTusked((tag.getInt(PCUtil.PET_VARIANT) >>> 1) == 1);
            elephant.setTrader((tag.getInt(PCUtil.PET_VARIANT) & 1) == 1);
            return elephant;
        }

        if (pet instanceof EntityCapuchinMonkey capuchinMonkey) {
            capuchinMonkey.setVariant(tag.getInt(PCUtil.PET_VARIANT));
            return capuchinMonkey;
        }

        if (pet instanceof EntityCrocodile crocodile) {
            crocodile.setDesert(tag.getInt(PCUtil.PET_VARIANT) == 1);
            return crocodile;
        }

        if (pet instanceof EntityMantisShrimp mantisShrimp) {
            mantisShrimp.setVariant(tag.getInt(PCUtil.PET_VARIANT));
            return mantisShrimp;
        }

        if (pet instanceof EntityGorilla gorilla) {
            gorilla.setSilverback(tag.getInt(PCUtil.PET_VARIANT) == 1);
            return gorilla;
        }
        return pet;
    }

    public static String getVariantTranslatable(LivingEntity pet, CompoundTag tag) {
        String variant = "";
        if (pet instanceof EntityTarantulaHawk) {
            return (tag.getInt(PCUtil.PET_VARIANT) == 1) ? Component.translatable("variant.alexsmobs.tarantula_hawk.nether").getString() : "";
        }

        if (pet instanceof EntityElephant) {
            variant = ((tag.getInt(PCUtil.PET_VARIANT) >>> 1) == 1) ? Component.translatable("variant.alexsmobs.elephant.tusked").getString() : "";
            variant = ((tag.getInt(PCUtil.PET_VARIANT) & 1) == 1) ? variant + " " + Component.translatable("variant.alexsmobs.elephant.trader").getString() : variant;
            variant = variant.stripLeading();
        }

        if (pet instanceof EntityCapuchinMonkey) {
            switch (tag.getInt(PCUtil.PET_VARIANT)) {
                case 0:
                    variant = Component.translatable("variant.alexsmobs.capuchin_monkey.white_faced").getString();
                    break;
                case 1:
                    variant = Component.translatable("variant.alexsmobs.capuchin_monkey.blonde").getString();
                    break;
                case 2:
                    variant = Component.translatable("variant.alexsmobs.capuchin_monkey.chestnut").getString();
                    break;
                case 3:
                    variant = Component.translatable("variant.alexsmobs.capuchin_monkey.tufted").getString();
                    break;
            }
        }

        if (pet instanceof EntityCrocodile) {
            variant = (tag.getInt(PCUtil.PET_VARIANT) == 1) ? Component.translatable("variant.alexsmobs.crocodile.desert").getString() : "";
        }

        if (pet instanceof EntityMantisShrimp) {
            switch (tag.getInt(PCUtil.PET_VARIANT)) {
                case 0:
                    variant = Component.translatable("variant.alexsmobs.mantis_shrimp.rainbow").getString();
                    break;
                case 1:
                    variant = Component.translatable("variant.alexsmobs.mantis_shrimp.harlequin").getString();
                    break;
                case 2:
                    variant = Component.translatable("variant.alexsmobs.mantis_shrimp.peacock").getString();
                    break;
                case 3:
                    variant = Component.translatable("variant.alexsmobs.mantis_shrimp.zebra").getString();
                    break;
            }
        }

        if (pet instanceof EntityGorilla) {
            variant = (tag.getInt(PCUtil.PET_VARIANT) == 1) ? Component.translatable("variant.alexsmobs.gorilla.silverback").getString() : "";
        }
        return variant;
    }
}
