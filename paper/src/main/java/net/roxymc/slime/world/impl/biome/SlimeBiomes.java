package net.roxymc.slime.world.impl.biome;

import com.mojang.serialization.Codec;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.biome.Biomes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SlimeBiomes(PalettedContainerRO<Holder<Biome>> biomes) implements Biomes {
    @Override
    public CompoundBinaryTag tag() {
        Codec<PalettedContainerRO<Holder<Biome>>> codec = SerializableChunkData.makeBiomeCodec(
                MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.BIOME)
        );

        CompoundTag tag = codec
                .encodeStart(NbtOps.INSTANCE, biomes)
                .map(CompoundTag.class::cast)
                .getOrThrow();

        return AdventureUtils.asAdventure(tag);
    }

    public SlimeBiomesSnapshot getSnapshot() {
        return new SlimeBiomesSnapshot(tag());
    }
}
