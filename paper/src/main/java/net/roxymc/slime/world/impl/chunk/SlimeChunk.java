package net.roxymc.slime.world.impl.chunk;

import ca.spottedleaf.moonrise.patches.chunk_system.level.entity.ChunkEntitySlices;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.roxymc.slime.ignite.level.SlimeLevel;
import net.roxymc.slime.ignite.level.chunk.SlimeLevelChunk;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.Heightmaps;
import net.roxymc.slime.world.block.entity.BlockEntity;
import net.roxymc.slime.world.entity.Entity;
import net.roxymc.slime.world.impl.SlimeHeightmapsSnapshot;
import net.roxymc.slime.world.impl.block.entity.SlimeBlockEntitySnapshot;
import net.roxymc.slime.world.impl.entity.SlimeEntitySnapshot;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public record SlimeChunk(SlimeLevel level, SlimeLevelChunk handle) implements BaseSlimeChunk {
    public static final String PERSISTENT_DATA_CONTAINER_KEY = "ChunkBukkitValues";

    @Override
    public int x() {
        return handle.locX;
    }

    @Override
    public int z() {
        return handle.locZ;
    }

    @Override
    public SlimeSection[] sections() {
        SlimeSection[] sections = new SlimeSection[handle.getSectionsCount()];

        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = handle.getSections()[i];

            sections[i] = new SlimeSection(level, handle, section, i);
        }

        return sections;
    }

    public SlimeSectionSnapshot[] snapshotSections() {
        return Arrays.stream(sections())
                .map(SlimeSection::getSnapshot)
                .toArray(SlimeSectionSnapshot[]::new);
    }

    @Override
    public Heightmaps heightmaps() {
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

        for (Map.Entry<Heightmap.Types, Heightmap> entry : handle.heightmaps.entrySet()) {
            if (!entry.getKey().keepAfterWorldgen()) {
                continue;
            }

            builder.put(
                    entry.getKey().getSerializationKey(),
                    LongArrayBinaryTag.longArrayBinaryTag(entry.getValue().getRawData())
            );
        }

        return new SlimeHeightmapsSnapshot(builder.build());
    }

    @Override
    public BlockEntity[] blockEntities() {
        Set<BlockPos> set = handle.getBlockEntitiesPos();

        BlockEntity[] blockEntities = new BlockEntity[set.size()];
        int i = 0;
        for (BlockPos blockPos : set) {
            CompoundTag tag = handle.getBlockEntityNbtForSaving(blockPos, level.registryAccess());

            blockEntities[i++] = new SlimeBlockEntitySnapshot(AdventureUtils.asAdventure(tag));
        }

        return blockEntities;
    }

    @Override
    public Entity[] entities() {
        ChunkEntitySlices entitySlices = level.moonrise$getEntityLookup().getChunk(x(), z());
        if (entitySlices == null) {
            return new Entity[0];
        }

        List<Entity> entities = new ArrayList<>();
        for (net.minecraft.world.entity.Entity entity : entitySlices.getAllEntities()) {
            //noinspection ConstantValue
            if (entity == null) continue;

            CompoundTag tag = new CompoundTag();
            entity.save(tag);

            entities.add(new SlimeEntitySnapshot(AdventureUtils.asAdventure(tag)));
        }

        return entities.toArray(Entity[]::new);
    }

    @Override
    public CompoundBinaryTag tag() {
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

        if (!handle.persistentDataContainer.isEmpty()) {
            CompoundTag rawPdc = handle.persistentDataContainer.toTagCompound();

            builder.put(PERSISTENT_DATA_CONTAINER_KEY, AdventureUtils.asAdventure(rawPdc));
        }

        return builder.build();
    }

    @Override
    public SlimeChunkSnapshot getSnapshot() {
        return new SlimeChunkSnapshot(this);
    }
}
