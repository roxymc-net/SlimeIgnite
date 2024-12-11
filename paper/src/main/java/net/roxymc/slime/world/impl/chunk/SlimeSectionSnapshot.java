package net.roxymc.slime.world.impl.chunk;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.biome.Biomes;
import net.roxymc.slime.world.block.state.BlockStates;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SlimeSectionSnapshot implements BaseSlimeSection {
    private final byte @Nullable [] blockLight;
    private final byte @Nullable [] skyLight;
    private final BlockStates blockStates;
    private final Biomes biomes;
    private @Nullable Boolean hasOnlyAir;

    public SlimeSectionSnapshot(SlimeSection section) {
        this(section.blockLight(), section.skyLight(), section.blockStates().getSnapshot(), section.biomes().getSnapshot());
        this.hasOnlyAir = section.hasOnlyAir();
    }

    public SlimeSectionSnapshot(byte @Nullable [] blockLight, byte @Nullable [] skyLight, BlockStates blockStates, Biomes biomes) {
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.blockStates = blockStates;
        this.biomes = biomes;
    }

    @Override
    public byte @Nullable [] blockLight() {
        return blockLight;
    }

    @Override
    public byte @Nullable [] skyLight() {
        return skyLight;
    }

    @Override
    public BlockStates blockStates() {
        return blockStates;
    }

    @Override
    public Biomes biomes() {
        return biomes;
    }

    @Override
    public boolean hasOnlyAir() {
        if (hasOnlyAir != null) {
            return hasOnlyAir;
        }

        DataResult<PalettedContainer<BlockState>> blockStates = SerializableChunkData.BLOCK_STATE_CODEC.parse(
                NbtOps.INSTANCE, AdventureUtils.asVanilla(blockStates().tag())
        );
        PalettedContainer<BlockState> palette = blockStates.result().orElse(null);
        if (palette == null) {
            return hasOnlyAir = true; // if it fails then probably skip it since it won't work?
        }

        return hasOnlyAir = !palette.maybeHas(blockState -> !blockState.isAir());
    }
}
