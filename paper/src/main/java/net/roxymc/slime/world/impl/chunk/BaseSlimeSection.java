package net.roxymc.slime.world.impl.chunk;

import net.roxymc.slime.world.chunk.Section;

public sealed interface BaseSlimeSection extends Section permits SlimeSection, SlimeSectionSnapshot {
    boolean hasOnlyAir();
}
