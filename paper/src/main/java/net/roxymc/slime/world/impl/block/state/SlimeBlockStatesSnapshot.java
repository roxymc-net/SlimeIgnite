package net.roxymc.slime.world.impl.block.state;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.block.state.BlockStates;

public record SlimeBlockStatesSnapshot(CompoundBinaryTag tag) implements BlockStates {
}
