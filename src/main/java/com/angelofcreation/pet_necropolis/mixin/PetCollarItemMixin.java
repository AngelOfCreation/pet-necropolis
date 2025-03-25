package com.angelofcreation.pet_necropolis.mixin;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.teamabnormals.pet_cemetery.common.item.PetCollarItem;
import com.teamabnormals.pet_cemetery.core.PetCemetery;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import com.teamabnormals.pet_cemetery.core.registry.PCEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(PCUtil.PET_ID)) {
            String petID = tag.getString(PCUtil.PET_ID);
            EntityType<?> pet = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(petID));

            Component petType = Component.translatable(pet.getDescriptionId()).withStyle(ChatFormatting.GRAY);
            if (tag.contains(PCUtil.PET_VARIANT)) {
                String variant = "";

                if (pet == EntityType.CAT || pet == PCEntityTypes.ZOMBIE_CAT.get()) {
                    ResourceLocation catVariant = new ResourceLocation(tag.getString(PCUtil.PET_VARIANT));
                    variant = catVariant.getPath();
                }

                if (pet == EntityType.PARROT || pet == PCEntityTypes.ZOMBIE_PARROT.get()) {
                    variant = Parrot.Variant.byId(tag.getInt(PCUtil.PET_VARIANT)).getSerializedName();
                }

                if (ForgeRegistries.ENTITY_TYPES.getKey(pet).getNamespace().equals("alexsmobs")) {
                    if (pet == AMEntityRegistry.TARANTULA_HAWK.get()) {
                        variant = (tag.getInt(PCUtil.PET_VARIANT) == 1) ? Component.translatable("variant.alexsmobs.tarantula_hawk.nether").getString() : "";
                    }

                    if (pet == AMEntityRegistry.ELEPHANT.get()) {
                        variant = ((tag.getInt(PCUtil.PET_VARIANT) >>> 1) == 1) ? Component.translatable("variant.alexsmobs.elephant.tusked").getString() : "";
                        variant = ((tag.getInt(PCUtil.PET_VARIANT) & 1) == 1) ? variant + " " + Component.translatable("variant.alexsmobs.elephant.trader").getString() : variant;
                        variant = variant.stripLeading();
                    }

                    if (pet == AMEntityRegistry.CAPUCHIN_MONKEY.get()) {
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

                    if (pet == AMEntityRegistry.CROCODILE.get()) {
                        variant = (tag.getInt(PCUtil.PET_VARIANT) == 1) ? Component.translatable("variant.alexsmobs.crocodile.desert").getString() : "";
                    }

                    if (pet == AMEntityRegistry.MANTIS_SHRIMP.get()) {
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

                    if (pet == AMEntityRegistry.GORILLA.get()) {
                        variant = (tag.getInt(PCUtil.PET_VARIANT) == 1) ? Component.translatable("variant.alexsmobs.gorilla.silverback").getString() : "";
                    }
                }

                if (ForgeRegistries.ENTITY_TYPES.getKey(pet).getNamespace().equals("alexscaves")) {
                    if (pet.create(worldIn) instanceof DinosaurEntity && pet != ACEntityRegistry.VALLUMRAPTOR.get()) {
                        switch (tag.getInt(PCUtil.PET_VARIANT)) {
                            case 1:
                                variant = Component.translatable("variant.alexscaves.dinosaur.retro").getString();
                                break;
                            case 2:
                                variant = Component.translatable("variant.alexscaves.dinosaur.tectonic").getString();
                                break;
                        }
                    }

                    if (pet == ACEntityRegistry.VALLUMRAPTOR.get()) {
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

                    if (pet == ACEntityRegistry.CANDICORN.get()) {
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
                        if (pet == ACEntityRegistry.TREMORZILLA.get()) {
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
                }

                if (!variant.isEmpty()) variant = variant.replace("_", " ").concat(" ");
                variant.stripLeading();
                petType = Component.literal(WordUtils.capitalize(variant)).withStyle(ChatFormatting.GRAY).append(petType);
            }

            if (tag.getBoolean(PCUtil.IS_CHILD))
                petType = Component.translatable("tooltip." + PetCemetery.MOD_ID + ".baby").withStyle(ChatFormatting.GRAY).append(" ").append(petType);

            tooltip.add(petType);
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
