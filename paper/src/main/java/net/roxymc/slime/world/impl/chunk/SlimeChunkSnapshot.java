package net.roxymc.slime.world.impl.chunk;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.Heightmaps;
import net.roxymc.slime.world.block.entity.BlockEntity;
import net.roxymc.slime.world.chunk.Section;
import net.roxymc.slime.world.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SlimeChunkSnapshot implements BaseSlimeChunk {
    private final int x;
    private final int z;
    private final Section[] sections;
    private final Heightmaps heightmaps;
    private final BlockEntity[] blockEntities;
    private final Entity[] entities;
    private final CompoundBinaryTag tag;
    private @Nullable Boolean isEmpty;

    public SlimeChunkSnapshot(SlimeChunk chunk) {
        this(chunk.x(), chunk.z(), chunk.snapshotSections(), chunk.heightmaps(), chunk.blockEntities(), chunk.entities(), chunk.tag());
        this.isEmpty = chunk.isEmpty();
    }

    public SlimeChunkSnapshot(int x, int z, Section[] sections, Heightmaps heightmaps, BlockEntity[] blockEntities, Entity[] entities, CompoundBinaryTag tag) {
        this.x = x;
        this.z = z;
        this.sections = sections;
        this.heightmaps = heightmaps;
        this.blockEntities = blockEntities;
        this.entities = entities;
        this.tag = tag;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int z() {
        return z;
    }

    @Override
    public Section[] sections() {
        return sections;
    }

    @Override
    public Heightmaps heightmaps() {
        return heightmaps;
    }

    @Override
    public BlockEntity[] blockEntities() {
        return blockEntities;
    }

    @Override
    public Entity[] entities() {
        return entities;
    }

    @Override
    public CompoundBinaryTag tag() {
        return tag;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty != null ? isEmpty : (isEmpty = BaseSlimeChunk.super.isEmpty());
    }

    @Override
    public SlimeChunkSnapshot getSnapshot() {
        return this;
    }
}
