package net.roxymc.slime.ignite.loader;

import ca.spottedleaf.dataconverter.minecraft.datatypes.MCDataType;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCValueType;
import ca.spottedleaf.dataconverter.minecraft.walkers.generic.WalkerUtils;
import ca.spottedleaf.dataconverter.types.nbt.NBTMapType;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.roxymc.slime.CompoundBinaryTagHolder;
import net.roxymc.slime.ignite.util.AdventureUtils;
import net.roxymc.slime.world.World;
import net.roxymc.slime.world.biome.Biomes;
import net.roxymc.slime.world.block.entity.BlockEntity;
import net.roxymc.slime.world.block.state.BlockStates;
import net.roxymc.slime.world.chunk.Chunk;
import net.roxymc.slime.world.chunk.Section;
import net.roxymc.slime.world.entity.Entity;
import net.roxymc.slime.world.impl.SlimeWorldSnapshot;
import net.roxymc.slime.world.impl.biome.SlimeBiomesSnapshot;
import net.roxymc.slime.world.impl.block.entity.SlimeBlockEntitySnapshot;
import net.roxymc.slime.world.impl.block.state.SlimeBlockStatesSnapshot;
import net.roxymc.slime.world.impl.chunk.SlimeChunkSnapshot;
import net.roxymc.slime.world.impl.chunk.SlimeSectionSnapshot;
import net.roxymc.slime.world.impl.entity.SlimeEntitySnapshot;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

final class SlimeDataFixer {
    private static final String PALETTE = "palette";

    private final int fromVersion, toVersion;

    SlimeDataFixer(World world) {
        this(world.version(), SharedConstants.getCurrentVersion().getDataVersion().getVersion());
    }

    SlimeDataFixer(int fromVersion, int toVersion) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    private static <F, T> T[] transform(F[] array, IntFunction<T[]> generator, Function<F, T> mapper) {
        return Arrays.stream(array)
                .map(mapper)
                .toArray(generator);
    }

    private static CompoundBinaryTag convert(CompoundBinaryTag tag, Consumer<CompoundTag> consumer) {
        CompoundTag vanillaTag = AdventureUtils.asVanilla(tag);
        consumer.accept(vanillaTag);

        return AdventureUtils.asAdventure(vanillaTag);
    }

    private static <T extends CompoundBinaryTagHolder> T convert(T holder, Consumer<CompoundTag> consumer, Function<CompoundBinaryTag, T> function) {
        return function.apply(convert(holder.tag(), consumer));
    }

    private <T extends CompoundBinaryTagHolder> T convert(T holder, MCDataType type, Function<CompoundBinaryTag, T> function) {
        return convert(holder, tag -> type.convert(new NBTMapType(tag), fromVersion, toVersion), function);
    }

    private <T extends CompoundBinaryTagHolder> T convertList(T holder, MCDataType type, String path, Function<CompoundBinaryTag, T> function) {
        return convert(holder, tag -> WalkerUtils.convertList(type, new NBTMapType(tag), path, fromVersion, toVersion), function);
    }

    private <T extends CompoundBinaryTagHolder> T convertList(T holder, MCValueType type, String path, Function<CompoundBinaryTag, T> function) {
        return convert(holder, tag -> WalkerUtils.convertList(type, new NBTMapType(tag), path, fromVersion, toVersion), function);
    }

    SlimeWorldSnapshot dataFix(World world) {
        if (fromVersion == toVersion) {
            return (SlimeWorldSnapshot) world;
        }

        Chunk[] chunks = transform(world.chunks(), Chunk[]::new, this::dataFixChunk);

        return new SlimeWorldSnapshot(
                toVersion,
                chunks,
                world.tag()
        );
    }

    private Chunk dataFixChunk(Chunk chunk) {
        Section[] sections = transform(chunk.sections(), Section[]::new, this::dataFixSection);
        BlockEntity[] blockEntities = transform(chunk.blockEntities(), BlockEntity[]::new, this::dataFixBlockEntity);
        Entity[] entities = transform(chunk.entities(), Entity[]::new, this::dataFixEntity);

        return new SlimeChunkSnapshot(
                chunk.x(),
                chunk.z(),
                sections,
                chunk.heightmaps(),
                blockEntities,
                entities,
                chunk.tag()
        );
    }

    private BlockEntity dataFixBlockEntity(BlockEntity blockEntity) {
        return convert(blockEntity, MCTypeRegistry.TILE_ENTITY, SlimeBlockEntitySnapshot::new);
    }

    private Entity dataFixEntity(Entity entity) {
        return convert(entity, MCTypeRegistry.ENTITY, SlimeEntitySnapshot::new);
    }

    private Section dataFixSection(Section section) {
        BlockStates blockStates = dataFixBlockStates(section.blockStates());
        Biomes biomes = dataFixBiomes(section.biomes());

        return new SlimeSectionSnapshot(
                section.blockLight(),
                section.skyLight(),
                blockStates,
                biomes
        );
    }

    private BlockStates dataFixBlockStates(BlockStates blockStates) {
        return convertList(blockStates, MCTypeRegistry.BLOCK_STATE, PALETTE, SlimeBlockStatesSnapshot::new);
    }

    private Biomes dataFixBiomes(Biomes biomes) {
        return convertList(biomes, MCTypeRegistry.BIOME, PALETTE, SlimeBiomesSnapshot::new);
    }
}
