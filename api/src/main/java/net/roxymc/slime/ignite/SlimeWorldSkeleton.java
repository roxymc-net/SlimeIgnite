package net.roxymc.slime.ignite;

import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import net.roxymc.slime.world.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SlimeWorldSkeleton {
    String name();

    World data();

    default SlimeWorldSkeleton snapshot() {
        return snapshot(name());
    }

    SlimeWorldSkeleton snapshot(String name);

    boolean isLoaded();

    default LoadedSlimeWorld load(SlimeWorldStorage storage) {
        return load(storage, false);
    }

    default LoadedSlimeWorld load(SlimeWorldStorage storage, boolean readOnly) {
        return storage.loadWorld(this, readOnly);
    }
}
