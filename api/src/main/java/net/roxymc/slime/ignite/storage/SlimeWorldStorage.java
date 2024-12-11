package net.roxymc.slime.ignite.storage;

import net.roxymc.slime.ignite.LoadedSlimeWorld;
import net.roxymc.slime.ignite.SlimeWorldSkeleton;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface SlimeWorldStorage {
    String name();

    boolean worldExists(String world);

    Collection<String> listWorlds();

    SlimeWorldSkeleton readWorld(String world);

    default LoadedSlimeWorld loadWorld(SlimeWorldSkeleton world) {
        return loadWorld(world, false);
    }

    LoadedSlimeWorld loadWorld(SlimeWorldSkeleton world, boolean readOnly);

    WorldSaveResult saveWorld(SlimeWorldSkeleton snapshot);

    void deleteWorld(String world);
}
