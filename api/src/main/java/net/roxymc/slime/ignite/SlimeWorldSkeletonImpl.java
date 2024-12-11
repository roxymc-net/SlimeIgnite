package net.roxymc.slime.ignite;

import net.roxymc.slime.world.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SlimeWorldSkeletonImpl(String name, World data) implements SlimeWorldSkeleton {
    @Override
    public SlimeWorldSkeleton snapshot(String name) {
        return new SlimeWorldSkeletonImpl(name, data);
    }

    @Override
    public boolean isLoaded() {
        return false;
    }
}
