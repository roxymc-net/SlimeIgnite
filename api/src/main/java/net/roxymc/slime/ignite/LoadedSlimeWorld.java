package net.roxymc.slime.ignite;

import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LoadedSlimeWorld extends SlimeWorldSkeleton {
    boolean readOnly();

    SlimeWorldStorage storage();

    World serverWorld();

    @Override
    default boolean isLoaded() {
        return true;
    }
}
