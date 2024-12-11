package net.roxymc.slime.world.impl.block.entity;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.roxymc.slime.world.block.entity.BlockEntity;

public record SlimeBlockEntitySnapshot(CompoundBinaryTag tag) implements BlockEntity {
}
