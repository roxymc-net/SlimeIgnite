package net.roxymc.slime.world.impl.entity;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.entity.Entity;

public record SlimeEntitySnapshot(CompoundBinaryTag tag) implements Entity {
}
