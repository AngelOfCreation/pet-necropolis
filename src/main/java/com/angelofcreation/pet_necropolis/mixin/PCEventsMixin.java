package com.angelofcreation.pet_necropolis.mixin;

import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexthe666.alexsmobs.entity.*;
import com.teamabnormals.pet_cemetery.core.other.PCCriteriaTriggers;
import com.teamabnormals.pet_cemetery.core.other.PCEvents;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import com.teamabnormals.pet_cemetery.core.other.tags.PCEntityTypeTags;
import com.teamabnormals.pet_cemetery.core.registry.PCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.crimsoncrips.alexscavesexemplified.misc.interfaces.Gammafied;
import org.crimsoncrips.alexscavesexemplified.mixins.mobs.tremorzilla.ACETremorzillaMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;
import java.util.UUID;

@Mixin(PCEvents.class)
public abstract class PCEventsMixin {

    /**
     * @author AngelOfCreation
     * @reason Adding compatibility with additional mobs as defined by #drops_pet_collar
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType<?> type = entity.getType();

        if (type.is(PCEntityTypeTags.DROPS_PET_COLLAR)) {
            ItemStack collar = new ItemStack(PCItems.PET_COLLAR.get());
            CompoundTag tag = collar.getOrCreateTag();

            tag.putString(PCUtil.PET_ID, ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
            tag.putBoolean(PCUtil.IS_CHILD, entity.isBaby());
            if (entity.hasCustomName()) {
                collar.setHoverName(entity.getCustomName());
            }

            if (entity instanceof TamableAnimal pet && pet.isTame()) {
                if (pet instanceof Wolf)
                tag.putString(PCUtil.OWNER_ID, pet.getOwnerUUID().toString());
                if (entity instanceof Wolf wolf) {
                    tag.putInt(PCUtil.COLLAR_COLOR, wolf.getCollarColor().getId());
                }

                if (entity instanceof Cat cat) {
                    String variant = cat.level().registryAccess().registry(Registries.CAT_VARIANT).get().getKey(cat.getVariant()).toString();
                    tag.putString(PCUtil.PET_VARIANT, variant);
                    tag.putInt(PCUtil.COLLAR_COLOR, cat.getCollarColor().getId());
                }

                if (entity instanceof Parrot parrot) {
                    tag.putInt(PCUtil.PET_VARIANT, parrot.getVariant().getId());
                }

                if (ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace().equals("alexsmobs")) {
                    if (entity instanceof EntityTarantulaHawk tarantulaHawk) {
                        int variant = tarantulaHawk.isNether() ? 1 : 0;
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }

                    if (entity instanceof EntityElephant elephant) {
                        int variant = (elephant.isTusked() ? 1 : 0) << 1 | (elephant.isTrader() ? 1 : 0);
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }

                    if (entity instanceof EntityCapuchinMonkey capuchinMonkey) {
                        int variant = capuchinMonkey.getVariant();
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }

                    if (entity instanceof EntityCrocodile crocodile) {
                        int variant = crocodile.isDesert() ? 1 : 0;
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }

                    if (entity instanceof EntityMantisShrimp mantisShrimp) {
                        int variant = mantisShrimp.getVariant();
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }

                    if (entity instanceof EntityGorilla gorilla) {
                        int variant = gorilla.isSilverback() ? 1 : 0;
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }
                }

                if (ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace().equals("alexscaves")) {
                    if (entity instanceof DinosaurEntity dinosaur) {
                        tag.putInt(PCUtil.PET_VARIANT, dinosaur.getAltSkin());
                        switch (dinosaur.getAltSkin()) {
                            case 1:
                                tag.putInt(PCUtil.COLLAR_COLOR, DyeColor.PURPLE.getId());
                                break;
                            case 2:
                                tag.putInt(PCUtil.COLLAR_COLOR, DyeColor.ORANGE.getId());
                                break;
                        }
                    }

                    if (entity instanceof VallumraptorEntity vallumraptor) {
                        int variant = (vallumraptor.isElder() ? 1 : 0) << 2 | (vallumraptor.getAltSkin());
                        tag.putInt(PCUtil.PET_VARIANT, variant);
                    }



                    if (entity instanceof CandicornEntity candicorn) {
                        tag.putInt(PCUtil.COLLAR_COLOR, DyeColor.PINK.getId());
                        tag.putInt(PCUtil.PET_VARIANT, candicorn.getVariant());
                    }

                    if (ModList.get().isLoaded("alexscavesexemplified")) {
                        if (entity instanceof TremorzillaEntity tremorzilla) {
                            Gammafied gammafied = (Gammafied) tremorzilla;
                            int variant = (gammafied.isGamma() ? 1 : 0) << 2 | (tremorzilla.getAltSkin());
                            tag.putInt(PCUtil.PET_VARIANT, variant);
                        }
                    }
                }
                entity.spawnAtLocation(collar);
            }
        }
    }

    /**
     * @author AngelOfCreation
     * @reason Adding compatibility with additional mobs as defined by #drops_pet_collar
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = event.getItemStack();
        BlockPos offsetPos = pos.above();

        if (stack.is(PCItems.PET_COLLAR.get()) && state.is(Blocks.RESPAWN_ANCHOR) && level.dimensionType().respawnAnchorWorks() && state.getValue(RespawnAnchorBlock.CHARGE) > RespawnAnchorBlock.MIN_CHARGES && level.getBlockState(offsetPos).getCollisionShape(level, offsetPos).isEmpty()) {
            Player player = event.getEntity();
            RandomSource random = player.getRandom();
            CompoundTag tag = stack.getOrCreateTag();

            if (tag.contains(PCUtil.PET_ID)) {
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(tag.getString(PCUtil.PET_ID)));
                if (PCUtil.UNDEAD_MAP.containsKey(entityType)) entityType = PCUtil.UNDEAD_MAP.get(entityType);

                Animal entity = (Animal) entityType.create(level);
                UUID owner = tag.contains(PCUtil.OWNER_ID) ? UUID.fromString(tag.getString(PCUtil.OWNER_ID)) : player.getUUID();
                DyeColor collarColor = DyeColor.byId(tag.getInt(PCUtil.COLLAR_COLOR));

                entity.setBaby(tag.getBoolean(PCUtil.IS_CHILD));
                entity.setPos(offsetPos.getX() + 0.5F, offsetPos.getY(), offsetPos.getZ() + 0.5F);
                if (stack.hasCustomHoverName())
                    entity.setCustomName(stack.getHoverName());

                TamableAnimal respawnedEntity = null;
                if (entity instanceof TamableAnimal pet) {
                    pet.setTame(true);
                    pet.setOwnerUUID(owner);

                    if (pet instanceof Cat cat) {
                        Optional<Registry<CatVariant>> registry = level.registryAccess().registry(Registries.CAT_VARIANT);
                        if (registry.isPresent()) {
                            CatVariant variant = registry.get().get(new ResourceLocation(tag.getString(PCUtil.PET_VARIANT)));
                            if (variant != null) {
                                cat.setVariant(variant);
                                cat.setCollarColor(collarColor);
                                respawnedEntity = cat;
                            }
                        }
                    }

                    if (pet instanceof Parrot parrot) {
                        parrot.setVariant(Parrot.Variant.byId(tag.getInt(PCUtil.PET_VARIANT)));
                        respawnedEntity = parrot;
                    }

                    if (pet instanceof Wolf wolf) {
                        wolf.setCollarColor(collarColor);
                        respawnedEntity = wolf;
                    }

                    if (ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals("alexsmobs")) {
                        if (entity instanceof EntityTarantulaHawk tarantulaHawk) {
                            tarantulaHawk.setNether(tag.getInt(PCUtil.PET_VARIANT) == 1);
                            respawnedEntity = tarantulaHawk;
                        }

                        if (entity instanceof EntityElephant elephant) {
                            elephant.setTusked((tag.getInt(PCUtil.PET_VARIANT) >>> 1) == 1);
                            elephant.setTrader((tag.getInt(PCUtil.PET_VARIANT) & 1) == 1);
                            respawnedEntity = elephant;
                        }

                        if (entity instanceof EntityCapuchinMonkey capuchinMonkey) {
                            capuchinMonkey.setVariant(tag.getInt(PCUtil.PET_VARIANT));
                            respawnedEntity = capuchinMonkey;
                        }

                        if (entity instanceof EntityCrocodile crocodile) {
                            crocodile.setDesert(tag.getInt(PCUtil.PET_VARIANT) == 1);
                            respawnedEntity = crocodile;
                        }

                        if (entity instanceof EntityMantisShrimp mantisShrimp) {
                            mantisShrimp.setVariant(tag.getInt(PCUtil.PET_VARIANT));
                            respawnedEntity = mantisShrimp;
                        }

                        if (entity instanceof EntityGorilla gorilla) {
                            gorilla.setSilverback(tag.getInt(PCUtil.PET_VARIANT) == 1);
                            respawnedEntity = gorilla;
                        }
                    }

                    if (ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals("alexscaves")) {
                        if (entity instanceof SubterranodonEntity subterranodon) {
                            subterranodon.setAltSkin(tag.getInt(PCUtil.PET_VARIANT));
                            respawnedEntity = subterranodon;
                        }

                        if (entity instanceof VallumraptorEntity vallumraptor) {
                            vallumraptor.setElder((tag.getInt(PCUtil.PET_VARIANT) >>> 2) == 1);
                            vallumraptor.setAltSkin((tag.getInt(PCUtil.PET_VARIANT) & 3));
                            respawnedEntity = vallumraptor;
                        }

                        if (entity instanceof TremorsaurusEntity tremorsaurus) {
                            tremorsaurus.setAltSkin(tag.getInt(PCUtil.PET_VARIANT));
                            respawnedEntity = tremorsaurus;
                        }

                        if (entity instanceof TremorzillaEntity tremorzilla) {
                            tremorzilla.setAltSkin(tag.getInt(PCUtil.PET_VARIANT));
                            respawnedEntity = tremorzilla;
                        }

                        if (entity instanceof CandicornEntity candicorn) {
                            candicorn.setVariant(tag.getInt(PCUtil.PET_VARIANT));
                            respawnedEntity = candicorn;
                        }

                        if (ModList.get().isLoaded("alexscavesexemplified")) {
                            if (entity instanceof TremorzillaEntity tremorzilla) {
                                Gammafied gammafied = (Gammafied) tremorzilla;
                                gammafied.setGamma((tag.getInt(PCUtil.PET_VARIANT) >>> 2) == 1);
                                tremorzilla.setAltSkin((tag.getInt(PCUtil.PET_VARIANT) & 3));
                                respawnedEntity = tremorzilla;
                            }
                        }
                    }
                    if (respawnedEntity == null) respawnedEntity = pet;
                }

                if (respawnedEntity != null) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        PCCriteriaTriggers.RESPAWN_PET.trigger(serverPlayer, entity, respawnedEntity);
                    }

                    level.setBlockAndUpdate(pos, state.setValue(RespawnAnchorBlock.CHARGE, state.getValue(RespawnAnchorBlock.CHARGE) - 1));

                    for (int i = 0; i < 10; ++i) {
                        double d0 = random.nextGaussian() * 0.025D;
                        double d1 = random.nextGaussian() * 0.025D;
                        double d2 = random.nextGaussian() * 0.025D;
                        level.addParticle(ParticleTypes.LARGE_SMOKE, respawnedEntity.getRandomX(0.75D), respawnedEntity.getRandomY(), respawnedEntity.getRandomZ(0.75D), d0, d1, d2);
                    }

                    level.playSound(player, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.addFreshEntity(respawnedEntity);
                    if (!player.getAbilities().instabuild)
                        stack.shrink(1);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                }
            }
        }
    }
}