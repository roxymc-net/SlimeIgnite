package net.roxymc.slime.world.impl.biome;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.biome.Biomes;

public record SlimeBiomesSnapshot(CompoundBinaryTag tag) implements Biomes {
}
