package com.angelofcreation.pet_necropolis.mixin;

import com.angelofcreation.pet_necropolis.compat.ACCompat;
import com.angelofcreation.pet_necropolis.compat.ACECompat;
import com.angelofcreation.pet_necropolis.compat.AMCompat;
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

        if (entity instanceof TamableAnimal pet && pet.isTame()) {
            ItemStack collar = new ItemStack(PCItems.PET_COLLAR.get());
            CompoundTag tag = collar.getOrCreateTag();

            tag.putString(PCUtil.PET_ID, ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
            tag.putBoolean(PCUtil.IS_CHILD, entity.isBaby());
            if (entity.hasCustomName()) {
                collar.setHoverName(entity.getCustomName());
            }

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
                tag.putInt(PCUtil.PET_VARIANT, AMCompat.getVariantTag(entity));
            }

            if (ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace().equals("alexscaves")) {
                tag.putInt(PCUtil.PET_VARIANT, ACCompat.getVariantTag(entity));
                tag.putInt(PCUtil.COLLAR_COLOR, ACCompat.getCollarTag(entity));

                if (ModList.get().isLoaded("alexscavesexemplified")) {
                    tag.putInt(PCUtil.PET_VARIANT, ACECompat.getVariantTag(entity));
                }
            }
            entity.spawnAtLocation(collar);
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
                        respawnedEntity = AMCompat.getRespawnedEntity(pet, tag);
                    }

                    if (ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals("alexscaves")) {
                        respawnedEntity = ACCompat.getRespawnedEntity(pet, tag);

                        if (ModList.get().isLoaded("alexscavesexemplified")) {
                            respawnedEntity = ACECompat.getRespawnedEntity(pet, tag);
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