package net.roxymc.slime.ignite.mixin;

import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.ChunkHolderManager;
import net.minecraft.server.level.ServerLevel;
import net.roxymc.slime.ignite.level.SlimeLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkHolderManager.class)
public abstract class ChunkHolderManagerMixin {
    @Shadow
    private @Final ServerLevel world;

    @ModifyVariable(method = "close", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private boolean dontCloseIfSlime(boolean value) {
        if (world instanceof SlimeLevel) {
            return false;
        }

        return value;
    }
}
