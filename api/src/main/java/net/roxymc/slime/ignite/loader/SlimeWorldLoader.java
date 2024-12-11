package net.roxymc.slime.ignite.loader;

import net.roxymc.slime.ignite.LoadedSlimeWorld;
import net.roxymc.slime.ignite.SlimeWorldSkeleton;
import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import net.roxymc.slime.loader.SlimeLoader;
import org.bukkit.World;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

@NullMarked
public abstract class SlimeWorldLoader {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static final Optional<SlimeWorldLoader> INSTANCE = ServiceLoader.load(SlimeWorldLoader.class).findFirst();

    SlimeWorldLoader() {
    }

    public static SlimeWorldLoader get() {
        return INSTANCE.orElseThrow();
    }

    public abstract @Nullable LoadedSlimeWorld slimeWorld(World world);

    public abstract Set<String> storages();

    public abstract @UnknownNullability SlimeWorldStorage storage(String name);

    public abstract boolean registerStorage(SlimeWorldStorage storage);

    public abstract SlimeLoader slimeLoader();

    public abstract SlimeWorldSkeleton createEmptyWorld(String name);

    public abstract LoadedSlimeWorld loadWorld(SlimeWorldSkeleton world, SlimeWorldStorage storage, boolean readOnly) throws IOException;
}
