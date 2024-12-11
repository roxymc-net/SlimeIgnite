package net.roxymc.slime.ignite.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.roxymc.slime.ignite.LoadedSlimeWorld;
import net.roxymc.slime.ignite.LoadedSlimeWorldImpl;
import net.roxymc.slime.ignite.SlimeIgnite;
import net.roxymc.slime.ignite.SlimeWorldSkeleton;
import net.roxymc.slime.ignite.level.storage.SlimeLevelStorageSource;
import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import net.roxymc.slime.ignite.storage.WorldSaveResult;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.World;
import net.roxymc.slime.world.impl.SlimeWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class SlimeLevel extends ServerLevel {
    public final SlimeWorld slimeWorld;
    public final LoadedSlimeWorld loadedSlimeWorld;
    private final Object lock = new Object();

    public SlimeLevel(
            World original,
            SlimeWorldStorage storage,
            boolean readOnly,
            PrimaryLevelData levelData,
            ResourceKey<Level> levelKey,
            ResourceKey<LevelStem> dimensionKey,
            LevelStem dimension,
            org.bukkit.World.Environment environment
    ) throws IOException {
        //noinspection DataFlowIssue
        super(
                MinecraftServer.getServer(),
                MinecraftServer.getServer().executor,
                SlimeLevelStorageSource.INSTANCE.createAccess(levelData.getLevelName() + "-" + UUID.randomUUID(), dimensionKey),
                levelData,
                levelKey,
                dimension,
                MinecraftServer.getServer().progressListenerFactory.create(11),
                false,
                0,
                List.of(),
                true,
                null,
                environment,
                null,
                null
        );

        this.slimeWorld = new SlimeWorld(this, original);
        this.loadedSlimeWorld = new LoadedSlimeWorldImpl(this, storage, readOnly);

        getWorld().readBukkitValues(AdventureUtils.asVanilla(
                original.tag().getCompound(SlimeWorld.PERSISTENT_DATA_CONTAINER_KEY)
        ));
    }

    @Override
    public void save(@Nullable ProgressListener progressListener, boolean flush, boolean savingDisabled, boolean close) {
        if (savingDisabled) return;

        Bukkit.getPluginManager().callEvent(new WorldSaveEvent(getWorld()));

        try {
            if (close) {
                chunkSource.close(false);
                saveFuture(true).get();
            } else {
                saveFuture(false);
            }

            serverLevelData.setWorldBorder(getWorldBorder().createSettings());
            serverLevelData.setCustomBossEvents(getServer().getCustomBossEvents().save(this.registryAccess()));
            serverLevelData.createTag(getServer().registryAccess(), new CompoundTag());
        } catch (Exception e) {
            SlimeIgnite.LOGGER.atError()
                    .addArgument(serverLevelData.getLevelName())
                    .setCause(e)
                    .log("Failed to save world '{}'");
        }
    }

    @Override
    public void saveIncrementally(boolean doFull) {
        if (doFull) {
            save(null, false, false);
        }
    }

    private Future<?> saveFuture(boolean logSuccess) {
        if (loadedSlimeWorld.readOnly()) {
            return CompletableFuture.completedFuture(null);
        }

        synchronized (lock) {
            SlimeWorldSkeleton snapshot = loadedSlimeWorld.snapshot();

            return CompletableFuture
                    .runAsync(() -> {
                        WorldSaveResult result = loadedSlimeWorld.storage().saveWorld(snapshot);
                        if (!logSuccess) return;

                        SlimeIgnite.LOGGER.atInfo()
                                .addArgument(serverLevelData.getLevelName())
                                .addArgument(result.serializationTime())
                                .addArgument(result.saveTime())
                                .log("Slime world '{}' serialized in {}ms and saved in {}ms");
                    })
                    .exceptionally(throwable -> {
                        SlimeIgnite.LOGGER.atError()
                                .addArgument(serverLevelData.getLevelName())
                                .setCause(throwable)
                                .log("Failed to save world '{}'");
                        return null;
                    });
        }
    }
}
