package net.roxymc.slime.world.impl.chunk;

import net.roxymc.slime.world.chunk.Chunk;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public sealed interface BaseSlimeChunk extends Chunk permits SlimeChunk, SlimeChunkSnapshot {
    default boolean isEmpty() {
        if (Arrays.stream(sections()).anyMatch(section -> !((BaseSlimeSection) section).hasOnlyAir())) {
            return false;
        }

        return blockEntities().length == 0 && entities().length == 0;
    }

    SlimeChunkSnapshot getSnapshot();
}
