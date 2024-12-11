package net.roxymc.slime.ignite.storage.file;

import net.roxymc.slime.ignite.LoadedSlimeWorld;
import net.roxymc.slime.ignite.SlimeWorldSkeleton;
import net.roxymc.slime.ignite.SlimeWorldSkeletonImpl;
import net.roxymc.slime.ignite.loader.SlimeWorldLoader;
import net.roxymc.slime.ignite.storage.*;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NullMarked
public record FileSlimeWorldStorage(File directory) implements SlimeWorldStorage {
    public static final File WORLD_DIRECTORY = new File("slime_worlds").getAbsoluteFile();
    public static final String WORLD_EXTENSION = ".slime";

    public FileSlimeWorldStorage(File directory) {
        this.directory = directory.getAbsoluteFile();
        //noinspection ResultOfMethodCallIgnored
        this.directory.mkdirs();
    }

    @Override
    public String name() {
        return "file";
    }

    @Override
    public boolean worldExists(String world) {
        return worldFile(world).exists();
    }

    @Override
    public Collection<String> listWorlds() {
        File[] files = directory.listFiles(($, name) -> name.endsWith(WORLD_EXTENSION));
        if (files == null || files.length == 0) {
            return List.of();
        }

        return Arrays.stream(files)
                .map(file -> file.getName().substring(0, file.getName().lastIndexOf('.')))
                .toList();
    }

    @Override
    public SlimeWorldSkeleton readWorld(String world) {
        if (!worldExists(world)) {
            throw new WorldDoesNotExistException(world);
        }

        try (FileInputStream is = new FileInputStream(worldFile(world))) {
            return new SlimeWorldSkeletonImpl(
                    world,
                    SlimeWorldLoader.get().slimeLoader().load(is.readAllBytes())
            );
        } catch (IOException e) {
            throw new WorldFailedToReadException(world, e);
        }
    }

    @Override
    public LoadedSlimeWorld loadWorld(SlimeWorldSkeleton world, boolean readOnly) {
        try {
            return SlimeWorldLoader.get().loadWorld(world, this, readOnly);
        } catch (IOException e) {
            throw new WorldFailedToLoadException(world.name(), e);
        }
    }

    @Override
    public WorldSaveResult saveWorld(SlimeWorldSkeleton world) {
        try {
            long serializationStart = System.currentTimeMillis();
            byte[] bytes = SlimeWorldLoader.get().slimeLoader().save(world.data());

            long saveStart = System.currentTimeMillis();
            try (FileOutputStream os = new FileOutputStream(worldFile(world.name()))) {
                os.write(bytes);
            }

            return new WorldSaveResult(saveStart - serializationStart, System.currentTimeMillis() - saveStart);
        } catch (IOException e) {
            throw new WorldFailedToSaveException(world.name(), e);
        }
    }

    @Override
    public void deleteWorld(String world) {
        if (!worldExists(world)) {
            throw new WorldDoesNotExistException(world);
        }

        //noinspection ResultOfMethodCallIgnored
        worldFile(world).delete();
    }

    private File worldFile(String world) {
        return new File(directory, world + WORLD_EXTENSION);
    }
}
