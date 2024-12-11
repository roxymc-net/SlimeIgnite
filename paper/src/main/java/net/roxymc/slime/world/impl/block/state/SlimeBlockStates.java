package net.roxymc.slime.world.impl.block.state;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.block.state.BlockStates;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SlimeBlockStates(PalettedContainer<BlockState> blockStates) implements BlockStates {
    @Override
    public CompoundBinaryTag tag() {
        CompoundTag tag = SerializableChunkData.BLOCK_STATE_CODEC
                .encodeStart(NbtOps.INSTANCE, blockStates)
                .map(CompoundTag.class::cast)
                .getOrThrow();

        return AdventureUtils.asAdventure(tag);
    }

    public SlimeBlockStatesSnapshot getSnapshot() {
        return new SlimeBlockStatesSnapshot(tag());
    }
}
