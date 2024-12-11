package net.roxymc.slime.ignite.level.chunk;

import ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray;
import ca.spottedleaf.moonrise.patches.starlight.light.StarLightEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.roxymc.slime.CompoundBinaryTagHolder;
import net.roxymc.slime.ignite.level.SlimeLevel;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.chunk.Section;
import net.roxymc.slime.world.impl.chunk.SlimeChunk;
import net.roxymc.slime.world.impl.chunk.SlimeChunkSnapshot;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class SlimeLevelChunk extends LevelChunk {
    public SlimeLevelChunk(SlimeLevel world, ChunkPos pos, UpgradeData upgradeData, LevelChunkTicks<Block> blockTickScheduler, LevelChunkTicks<Fluid> fluidTickScheduler, long inhabitedTime, LevelChunkSection @Nullable [] sectionArrayInitializer, LevelChunk.@Nullable PostLoadProcessor entityLoader, @Nullable BlendingData blendingData) {
        super(world, pos, upgradeData, blockTickScheduler, fluidTickScheduler, inhabitedTime, sectionArrayInitializer, entityLoader, blendingData);
    }

    public static SlimeLevelChunk load(SlimeLevel level, SlimeChunkSnapshot snapshot) {
        Codec<PalettedContainer<Holder<Biome>>> biomeCodec = SerializableChunkData.makeBiomeCodecRW(level.registryAccess().lookupOrThrow(Registries.BIOME));

        ChunkPos pos = new ChunkPos(snapshot.x(), snapshot.z());

        SWMRNibbleArray[] blockNibbles = StarLightEngine.getFilledEmptyLight(level);
        SWMRNibbleArray[] skyNibbles = StarLightEngine.getFilledEmptyLight(level);
        level.getServer().scheduleOnMain(() ->
                level.getLightEngine().retainData(pos, true)
        );

        LevelChunkSection[] sections = new LevelChunkSection[snapshot.sections().length];
        for (int i = 0; i < sections.length; i++) {
            Section section = snapshot.sections()[i];

            if (section.blockLight() != null) {
                blockNibbles[i] = new SWMRNibbleArray(section.blockLight());
            }

            if (section.skyLight() != null) {
                skyNibbles[i] = new SWMRNibbleArray(section.skyLight());
            }

            DataResult<PalettedContainer<BlockState>> blockStates = SerializableChunkData.BLOCK_STATE_CODEC.parse(
                    NbtOps.INSTANCE, AdventureUtils.asVanilla(section.blockStates().tag())
            );

            DataResult<PalettedContainer<Holder<Biome>>> biomes = biomeCodec.parse(
                    NbtOps.INSTANCE, AdventureUtils.asVanilla(section.biomes().tag())
            );

            sections[i] = new LevelChunkSection(blockStates.getOrThrow(), biomes.getOrThrow());
        }

        SlimeLevelChunk chunk = new SlimeLevelChunk(
                level,
                pos,
                UpgradeData.EMPTY,
                new LevelChunkTicks<>(),
                new LevelChunkTicks<>(),
                0L,
                sections,
                SerializableChunkData.postLoadChunk(
                        level,
                        asCompoundTagList(snapshot.entities()),
                        asCompoundTagList(snapshot.blockEntities())
                ),
                null
        );

        chunk.starlight$setBlockNibbles(blockNibbles);
        chunk.starlight$setSkyNibbles(skyNibbles);

        CompoundBinaryTag heightmaps = snapshot.heightmaps().tag();
        EnumSet<Heightmap.Types> heightmapTypes = chunk.getPersistedStatus().heightmapsAfter();
        EnumSet<Heightmap.Types> unsetHeightmapTypes = EnumSet.noneOf(Heightmap.Types.class);

        for (Heightmap.Types type : heightmapTypes) {
            String name = type.getSerializedName();

            if (heightmaps.keySet().contains(name)) {
                chunk.setHeightmap(type, heightmaps.getLongArray(name));
            } else {
                unsetHeightmapTypes.add(type);
            }
        }

        if (!unsetHeightmapTypes.isEmpty()) {
            Heightmap.primeHeightmaps(chunk, unsetHeightmapTypes);
        }

        chunk.persistentDataContainer.putAll(AdventureUtils.asVanilla(
                snapshot.tag().getCompound(SlimeChunk.PERSISTENT_DATA_CONTAINER_KEY)
        ));

        return chunk;
    }

    private static List<CompoundTag> asCompoundTagList(CompoundBinaryTagHolder[] holders) {
        return Arrays.stream(holders).map(holder -> AdventureUtils.asVanilla(holder.tag())).toList();
    }

    @Override
    public void loadCallback() {
        super.loadCallback();
        ((SlimeLevel) level).slimeWorld.ensureChunkLoaded(this);
    }

    @Override
    public void unloadCallback() {
        super.unloadCallback();
        ((SlimeLevel) level).slimeWorld.unloadChunk(getPos());
    }
}
