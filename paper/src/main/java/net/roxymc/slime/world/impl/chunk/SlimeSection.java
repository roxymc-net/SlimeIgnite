package net.roxymc.slime.world.impl.chunk;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.roxymc.slime.world.impl.biome.SlimeBiomes;
import net.roxymc.slime.world.impl.block.state.SlimeBlockStates;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SlimeSection implements BaseSlimeSection {
    public final LevelChunkSection handle;
    private final ServerLevel level;
    private final ChunkAccess chunkAccess;
    private final int index;

    public SlimeSection(ServerLevel level, ChunkAccess chunkAccess, LevelChunkSection handle, int index) {
        this.level = level;
        this.chunkAccess = chunkAccess;
        this.handle = handle;
        this.index = index;
    }

    private @Nullable DataLayer getLightDataLayer(LightLayer layer) {
        return level.chunkSource.getLightEngine()
                .getLayerListener(layer)
                .getDataLayerData(SectionPos.of(chunkAccess.getPos(), index));
    }

    @Override
    public byte @Nullable [] blockLight() {
        DataLayer layer = getLightDataLayer(LightLayer.BLOCK);
        if (layer == null) {
            return null;
        }

        return layer.getData();
    }

    @Override
    public byte @Nullable [] skyLight() {
        DataLayer layer = getLightDataLayer(LightLayer.SKY);
        if (layer == null) {
            return null;
        }

        return layer.getData();
    }

    @Override
    public SlimeBlockStates blockStates() {
        return new SlimeBlockStates(handle.getStates());
    }

    @Override
    public SlimeBiomes biomes() {
        return new SlimeBiomes(handle.getBiomes());
    }

    @Override
    public boolean hasOnlyAir() {
        return handle.hasOnlyAir();
    }

    public SlimeSectionSnapshot getSnapshot() {
        return new SlimeSectionSnapshot(this);
    }
}
