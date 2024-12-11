package net.roxymc.slime.world.impl;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.Heightmaps;

public record SlimeHeightmapsSnapshot(CompoundBinaryTag tag) implements Heightmaps {
}
