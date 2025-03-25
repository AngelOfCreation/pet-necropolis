package com.angelofcreation.pet_necropolis.mixin;

import com.angelofcreation.pet_necropolis.compat.ACCompat;
import com.angelofcreation.pet_necropolis.compat.ACECompat;
import com.angelofcreation.pet_necropolis.compat.AMCompat;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.teamabnormals.pet_cemetery.common.entity.ZombieCat;
import com.teamabnormals.pet_cemetery.common.entity.ZombieParrot;
import com.teamabnormals.pet_cemetery.common.item.PetCollarItem;
import com.teamabnormals.pet_cemetery.core.PetCemetery;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import com.teamabnormals.pet_cemetery.core.registry.PCEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.util.List;


@Mixin(PetCollarItem.class)
public abstract class PetCollarItemMixin extends Item {


    public PetCollarItemMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author AngelOfCreation
     * @reason Adding compatibility with additional mobs as defined by #drops_pet_collar
     */
    @Overwrite
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(PCUtil.PET_ID)) {
            String petID = tag.getString(PCUtil.PET_ID);
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(petID));

            if (entityType.create(world) instanceof LivingEntity pet) {
                Component petType = Component.translatable(entityType.getDescriptionId()).withStyle(ChatFormatting.GRAY);
                if (tag.contains(PCUtil.PET_VARIANT)) {
                    String variant = "";

                    if (pet instanceof Cat || pet instanceof ZombieCat) {
                        ResourceLocation catVariant = new ResourceLocation(tag.getString(PCUtil.PET_VARIANT));
                        variant = catVariant.getPath();
                    }

                    if (pet instanceof Parrot || pet instanceof ZombieParrot) {
                        variant = Parrot.Variant.byId(tag.getInt(PCUtil.PET_VARIANT)).getSerializedName();
                    }

                    if (ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals("alexsmobs")) {
                        variant = AMCompat.getVariantTranslatable(pet, tag);
                    }

                    if (ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals("alexscaves")) {
                        variant = ACCompat.getVariantTranslatable(pet, tag);
                    }

                    if (!variant.isEmpty()) variant = variant.replace("_", " ").concat(" ");
                    variant.stripLeading();
                    petType = Component.literal(WordUtils.capitalize(variant)).withStyle(ChatFormatting.GRAY).append(petType);

                    if (tag.getBoolean(PCUtil.IS_CHILD))
                        petType = Component.translatable("tooltip." + PetCemetery.MOD_ID + ".baby").withStyle(ChatFormatting.GRAY).append(" ").append(petType);

                    tooltip.add(petType);
                }
            }
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }
}
