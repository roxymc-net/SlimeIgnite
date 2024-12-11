package net.roxymc.slime.ignite.level.storage;

import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@NullMarked
public class SlimeLevelStorageSource extends LevelStorageSource {
    public static final SlimeLevelStorageSource INSTANCE;

    static {
        try {
            Path path = Files.createTempDirectory("roxyslime-" + UUID.randomUUID()).toAbsolutePath();

            INSTANCE = new SlimeLevelStorageSource(path);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> FileUtils.deleteQuietly(path.toFile())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SlimeLevelStorageSource(Path path) {
        super(path, path, parseValidator(path.resolve(ALLOWED_SYMLINKS_CONFIG_NAME)), DataFixers.getDataFixer());
    }

    @Override
    public String getName() {
        return "Slime";
    }
}
