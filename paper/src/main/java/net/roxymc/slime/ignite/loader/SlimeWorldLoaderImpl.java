package net.roxymc.slime.ignite.loader;

import com.mojang.serialization.Lifecycle;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.roxymc.slime.ignite.LoadedSlimeWorld;
import net.roxymc.slime.ignite.SlimeWorldSkeleton;
import net.roxymc.slime.ignite.SlimeWorldSkeletonImpl;
import net.roxymc.slime.ignite.level.SlimeLevel;
import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import net.roxymc.slime.ignite.storage.WorldAlreadyLoadedException;
import net.roxymc.slime.ignite.storage.file.FileSlimeWorldStorage;
import net.roxymc.slime.loader.SlimeLoader;
import net.roxymc.slime.world.World;
import net.roxymc.slime.world.chunk.Chunk;
import net.roxymc.slime.world.impl.SlimeHeightmapsSnapshot;
import net.roxymc.slime.world.impl.SlimeWorldSnapshot;
import net.roxymc.slime.world.impl.biome.SlimeBiomesSnapshot;
import net.roxymc.slime.world.impl.block.entity.SlimeBlockEntitySnapshot;
import net.roxymc.slime.world.impl.block.state.SlimeBlockStatesSnapshot;
import net.roxymc.slime.world.impl.chunk.SlimeChunkSnapshot;
import net.roxymc.slime.world.impl.chunk.SlimeSectionSnapshot;
import net.roxymc.slime.world.impl.entity.SlimeEntitySnapshot;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class SlimeWorldLoaderImpl extends SlimeWorldLoader {
    private static final SlimeLoader LOADER = SlimeLoader.builder()
            .deserializers(builder -> builder
                    .biomes(SlimeBiomesSnapshot::new)
                    .blockEntity(SlimeBlockEntitySnapshot::new)
                    .blockStates(SlimeBlockStatesSnapshot::new)
                    .chunk(SlimeChunkSnapshot::new)
                    .section(SlimeSectionSnapshot::new)
                    .entity(SlimeEntitySnapshot::new)
                    .heightmaps(SlimeHeightmapsSnapshot::new)
                    .world((version, chunks, tag) -> dataFix(new SlimeWorldSnapshot(version, chunks, tag)))
            )
            .build();

    private final Map<String, SlimeWorldStorage> storageMap = new ConcurrentHashMap<>();

    public SlimeWorldLoaderImpl() {
        registerStorage(new FileSlimeWorldStorage(FileSlimeWorldStorage.WORLD_DIRECTORY));
    }

    private static SlimeWorldSnapshot dataFix(World world) {
        return new SlimeDataFixer(world).dataFix(world);
    }

    @Override
    public @Nullable LoadedSlimeWorld slimeWorld(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle() instanceof SlimeLevel slimeLevel ? slimeLevel.loadedSlimeWorld : null;
    }

    @Override
    public Set<String> storages() {
        return Set.copyOf(storageMap.keySet());
    }

    @Override
    public @UnknownNullability SlimeWorldStorage storage(String name) {
        return storageMap.get(name);
    }

    @Override
    public boolean registerStorage(SlimeWorldStorage storage) {
        return storageMap.putIfAbsent(storage.name(), storage) == null;
    }

    @Override
    public SlimeLoader slimeLoader() {
        return LOADER;
    }

    @Override
    public SlimeWorldSkeleton createEmptyWorld(String name) {
        return new SlimeWorldSkeletonImpl(name, new SlimeWorldSnapshot(
                SharedConstants.getCurrentVersion().getDataVersion().getVersion(),
                new Chunk[0],
                CompoundBinaryTag.empty()
        ));
    }

    @Override
    public LoadedSlimeWorld loadWorld(SlimeWorldSkeleton world, SlimeWorldStorage storage, boolean readOnly) throws IOException {
        if (world.isLoaded() || Bukkit.getWorld(world.name()) != null) {
            throw new WorldAlreadyLoadedException(world.name());
        }

        MinecraftServer server = MinecraftServer.getServer();

        ResourceKey<Level> levelKey = ResourceKey.create(
                Registries.DIMENSION,
                ResourceLocation.parse(world.name().toLowerCase(Locale.ENGLISH))
        );

        SlimeLevel level = new SlimeLevel(
                world.data(),
                storage,
                readOnly,
                createLevelData(world),
                levelKey,
                LevelStem.OVERWORLD,
                server.registryAccess()
                        .lookupOrThrow(Registries.LEVEL_STEM)
                        .getValueOrThrow(LevelStem.OVERWORLD),
                org.bukkit.World.Environment.NORMAL
        );
        level.getChunkSource().setSpawnSettings(false, false);

        server.initWorld(level, level.serverLevelData, server.getWorldData(), level.serverLevelData.worldGenOptions());
        server.addLevel(level);
        server.getPlayerList().addWorldborderListener(level);

        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(level.getWorld()));

        return level.loadedSlimeWorld;
    }

    private PrimaryLevelData createLevelData(SlimeWorldSkeleton world) {
        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();
        DedicatedServerProperties properties = server.getProperties();

        LevelSettings settings = new LevelSettings(
                world.name(),
                properties.gamemode,
                false,
                properties.difficulty,
                true,
                new GameRules(server.worldLoader.dataConfiguration().enabledFeatures()),
                server.worldLoader.dataConfiguration()
        );
        WorldOptions options = new WorldOptions(0, false, false);

        //noinspection deprecation
        PrimaryLevelData levelData = new PrimaryLevelData(
                settings,
                options,
                PrimaryLevelData.SpecialWorldProperty.FLAT,
                Lifecycle.stable()
        );
        levelData.checkName(world.name());
        levelData.setModdedInfo(server.getServerModName(), server.getModdedStatus().shouldReportAsModified());
        levelData.setInitialized(true);

        return levelData;
    }
}
