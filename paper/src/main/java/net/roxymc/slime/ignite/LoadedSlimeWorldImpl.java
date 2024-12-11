package net.roxymc.slime.ignite;

import net.roxymc.slime.ignite.level.SlimeLevel;
import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import net.roxymc.slime.ignite.storage.WorldNotLoadedException;
import org.jspecify.annotations.NullMarked;

import java.lang.ref.WeakReference;
import java.util.Optional;

@SuppressWarnings("resource")
@NullMarked
public final class LoadedSlimeWorldImpl implements LoadedSlimeWorld {
    private final WeakReference<SlimeLevel> level;
    private final SlimeWorldStorage storage;
    private final boolean readOnly;
    private final String name;

    public LoadedSlimeWorldImpl(SlimeLevel level, SlimeWorldStorage storage, boolean readOnly) {
        this.level = new WeakReference<>(level);
        this.storage = storage;
        this.readOnly = readOnly;
        this.name = level.serverLevelData.getLevelName();
    }

    private SlimeLevel level() {
        return Optional.ofNullable(level.get()).orElseThrow(() -> new WorldNotLoadedException(name));
    }

    @Override
    public boolean readOnly() {
        return readOnly;
    }

    @Override
    public SlimeWorldStorage storage() {
        return storage;
    }

    @Override
    public org.bukkit.World serverWorld() {
        return level().getWorld();
    }

    @Override
    public SlimeWorldSkeleton snapshot(String name) {
        return new SlimeWorldSkeletonImpl(name, data());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public net.roxymc.slime.world.World data() {
        return level().slimeWorld.getSnapshot();
    }
}
