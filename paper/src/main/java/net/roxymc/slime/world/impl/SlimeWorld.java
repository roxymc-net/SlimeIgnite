package net.roxymc.slime.world.impl;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.roxymc.slime.ignite.level.SlimeLevel;
import net.roxymc.slime.ignite.level.chunk.SlimeLevelChunk;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.World;
import net.roxymc.slime.world.chunk.Chunk;
import net.roxymc.slime.world.impl.chunk.BaseSlimeChunk;
import net.roxymc.slime.world.impl.chunk.SlimeChunk;
import net.roxymc.slime.world.impl.chunk.SlimeChunkSnapshot;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SlimeWorld implements World {
    public static final String PERSISTENT_DATA_CONTAINER_KEY = "BukkitValues";

    public final SlimeLevel handle;
    private final Map<ChunkPos, BaseSlimeChunk> chunks = new HashMap<>();

    public SlimeWorld(SlimeLevel handle, World original) {
        this.handle = handle;

        for (Chunk chunk : original.chunks()) {
            chunks.put(new ChunkPos(chunk.x(), chunk.z()), (SlimeChunkSnapshot) chunk);
        }
    }

    @Override
    public int version() {
        return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }

    @Override
    public BaseSlimeChunk[] chunks() {
        return chunks.values().toArray(BaseSlimeChunk[]::new);
    }

    public SlimeChunk loadChunk(ChunkPos pos) {
        return (SlimeChunk) this.chunks.compute(pos, ($, chunk) -> {
            if (chunk == null) {
                return new SlimeChunk(handle, new SlimeLevelChunk(
                        handle,
                        pos,
                        UpgradeData.EMPTY,
                        new LevelChunkTicks<>(),
                        new LevelChunkTicks<>(),
                        0,
                        null,
                        null,
                        null
                ));
            }

            if (chunk instanceof SlimeChunkSnapshot snapshot) {
                return new SlimeChunk(handle, SlimeLevelChunk.load(handle, snapshot));
            }

            return chunk;
        });
    }

    public void unloadChunk(ChunkPos pos) {
        this.chunks.computeIfPresent(pos, ($, chunk) -> chunk.isEmpty() ? null : chunk.getSnapshot());
    }

    public void ensureChunkLoaded(SlimeLevelChunk levelChunk) {
        chunks.computeIfPresent(levelChunk.getPos(), ($, chunk) -> {
            if (chunk instanceof SlimeChunkSnapshot) {
                return new SlimeChunk(handle, levelChunk);
            }

            return chunk;
        });
    }

    @Override
    public CompoundBinaryTag tag() {
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) handle.getWorld().getPersistentDataContainer();
        if (!persistentDataContainer.isEmpty()) {
            CompoundTag rawPdc = persistentDataContainer.toTagCompound();

            builder.put(PERSISTENT_DATA_CONTAINER_KEY, AdventureUtils.asAdventure(rawPdc));
        }

        return builder.build();
    }

    public SlimeWorldSnapshot getSnapshot() {
        return new SlimeWorldSnapshot(
                version(),
                Arrays.stream(chunks())
                        .map(chunk -> chunk.isEmpty() ? null : chunk.getSnapshot())
                        .filter(Objects::nonNull)
                        .toArray(Chunk[]::new),
                tag()
        );
    }
}
