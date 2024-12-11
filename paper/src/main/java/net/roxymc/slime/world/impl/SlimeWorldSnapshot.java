package net.roxymc.slime.world.impl;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.World;
import net.roxymc.slime.world.chunk.Chunk;

public record SlimeWorldSnapshot(int version, Chunk[] chunks, CompoundBinaryTag tag) implements World {
}
