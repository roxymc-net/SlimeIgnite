package net.roxymc.slime.ignite.mixin;

import ca.spottedleaf.concurrentutil.util.Priority;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.ChunkTaskScheduler;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.task.ChunkLoadTask;
import net.minecraft.server.level.ServerLevel;
import net.roxymc.slime.ignite.level.SlimeLevel;
import net.roxymc.slime.ignite.level.chunk.SlimeChunkDataLoadTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkLoadTask.class)
public abstract class ChunkLoadTaskMixin {
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(Lca/spottedleaf/moonrise/patches/chunk_system/scheduling/ChunkTaskScheduler;Lnet/minecraft/server/level/ServerLevel;IILca/spottedleaf/concurrentutil/util/Priority;)Lca/spottedleaf/moonrise/patches/chunk_system/scheduling/task/ChunkLoadTask$ChunkDataLoadTask;"
            )
    )
    private ChunkLoadTask.ChunkDataLoadTask chunkLoadTask(ChunkTaskScheduler scheduler, ServerLevel world, int chunkX, int chunkZ, Priority priority) {
        if (world instanceof SlimeLevel slimeLevel) {
            return new SlimeChunkDataLoadTask(scheduler, slimeLevel, chunkX, chunkZ, priority);
        }

        return new ChunkLoadTask.ChunkDataLoadTask(scheduler, world, chunkX, chunkZ, priority);
    }
}
