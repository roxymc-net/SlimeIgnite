package net.roxymc.slime.ignite.level.chunk;

import ca.spottedleaf.concurrentutil.executor.PrioritisedExecutor;
import ca.spottedleaf.concurrentutil.util.Priority;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.ChunkTaskScheduler;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.task.ChunkLoadTask;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.roxymc.slime.ignite.level.SlimeLevel;

public class SlimeChunkDataLoadTask extends ChunkLoadTask.ChunkDataLoadTask {
    private final SlimeLevel level;
    private final PrioritisedExecutor.PrioritisedTask task;

    public SlimeChunkDataLoadTask(ChunkTaskScheduler scheduler, SlimeLevel level, int chunkX, int chunkZ, Priority priority) {
        super(scheduler, level, chunkX, chunkZ, priority);

        this.level = level;
        this.task = scheduler.createChunkTask(chunkX, chunkZ, () -> {
            try {
                onComplete(new TaskResult<>(loadChunk(), null));
            } catch (Exception e) {
                onComplete(new TaskResult<>(null, e));
            }
        }, priority);
    }

    private ChunkAccess loadChunk() {
        return new ImposterProtoChunk(
                this.level.slimeWorld
                        .loadChunk(new ChunkPos(chunkX, chunkZ))
                        .handle(),
                false
        );
    }

    @Override
    protected boolean hasOffMain() {
        return false;
    }

    @Override
    protected boolean hasOnMain() {
        return false;
    }

    @Override
    public Priority getPriority() {
        return task.getPriority();
    }

    @Override
    public void setPriority(Priority priority) {
        task.setPriority(priority);
    }

    @Override
    public void raisePriority(Priority priority) {
        task.raisePriority(priority);
    }

    @Override
    public void lowerPriority(Priority priority) {
        task.lowerPriority(priority);
    }

    @Override
    public boolean schedule(boolean delay) {
        scheduler.scheduleChunkTask(chunkX, chunkZ, task::execute);
        return false;
    }

    @Override
    public void scheduleNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel() {
        return task.cancel();
    }

    public static final class DummyTask implements PrioritisedExecutor.PrioritisedTask {
        public static final DummyTask INSTANCE = new DummyTask();

        private DummyTask() {
        }

        @Override
        public PrioritisedExecutor getExecutor() {
            return null;
        }

        @Override
        public boolean queue() {
            return false;
        }

        @Override
        public boolean isQueued() {
            return false;
        }

        @Override
        public boolean cancel() {
            return false;
        }

        @Override
        public boolean execute() {
            return false;
        }

        @Override
        public Priority getPriority() {
            return null;
        }

        @Override
        public boolean setPriority(Priority priority) {
            return false;
        }

        @Override
        public boolean raisePriority(Priority priority) {
            return false;
        }

        @Override
        public boolean lowerPriority(Priority priority) {
            return false;
        }

        @Override
        public long getSubOrder() {
            return 0;
        }

        @Override
        public boolean setSubOrder(long subOrder) {
            return false;
        }

        @Override
        public boolean raiseSubOrder(long subOrder) {
            return false;
        }

        @Override
        public boolean lowerSubOrder(long subOrder) {
            return false;
        }

        @Override
        public boolean setPriorityAndSubOrder(Priority priority, long subOrder) {
            return false;
        }
    }
}
