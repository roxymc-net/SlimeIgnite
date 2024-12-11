package net.roxymc.slime.ignite.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.roxymc.slime.ignite.level.SlimeLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RegionFileStorage.class)
public abstract class RegionFileStorageMixin {
    @Shadow
    private @Final RegionStorageInfo info;

    @ModifyVariable(method = "moonrise$startWrite", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private @Nullable CompoundTag dontWriteIfSlime(CompoundTag value) {
        return MinecraftServer.getServer().getLevel(info.dimension()) instanceof SlimeLevel ? null : value;
    }
}
