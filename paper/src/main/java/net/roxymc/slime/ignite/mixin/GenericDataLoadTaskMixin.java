package net.roxymc.slime.ignite.mixin;

import ca.spottedleaf.concurrentutil.executor.PrioritisedExecutor;
import ca.spottedleaf.concurrentutil.util.Priority;
import ca.spottedleaf.moonrise.patches.chunk_system.io.MoonriseRegionFileIO;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.task.GenericDataLoadTask;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.roxymc.slime.ignite.level.chunk.SlimeChunkDataLoadTask;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(GenericDataLoadTask.class)
public abstract class GenericDataLoadTaskMixin {
    @ModifyExpressionValue(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lca/spottedleaf/moonrise/patches/chunk_system/scheduling/task/GenericDataLoadTask;processOnMain:Lca/spottedleaf/concurrentutil/executor/PrioritisedExecutor$PrioritisedTask;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private PrioritisedExecutor.PrioritisedTask dummyProcessTaskIfSlime(PrioritisedExecutor.PrioritisedTask original) {
        return ((GenericDataLoadTask<?, ?>) (Object) this) instanceof SlimeChunkDataLoadTask ? SlimeChunkDataLoadTask.DummyTask.INSTANCE : original;
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/server/level/ServerLevel;IILca/spottedleaf/moonrise/patches/chunk_system/io/MoonriseRegionFileIO$RegionFileType;Ljava/util/function/BiConsumer;Lca/spottedleaf/concurrentutil/util/Priority;)Lca/spottedleaf/moonrise/patches/chunk_system/scheduling/task/GenericDataLoadTask$LoadDataFromDiskTask;"
            )
    )
    private GenericDataLoadTask.LoadDataFromDiskTask noLoadTaskIfSlime(ServerLevel world, int chunkX, int chunkZ, MoonriseRegionFileIO.RegionFileType type, BiConsumer<CompoundTag, Throwable> onComplete, Priority priorityOperation, Operation<GenericDataLoadTask.LoadDataFromDiskTask> original) {
        if (((GenericDataLoadTask<?, ?>) (Object) this) instanceof SlimeChunkDataLoadTask) {
            return null;
        }

        return original.call(world, chunkX, chunkZ, type, onComplete, priorityOperation);
    }
}
