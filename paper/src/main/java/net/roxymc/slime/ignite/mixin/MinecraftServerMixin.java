package net.roxymc.slime.ignite.mixin;

import net.minecraft.server.MinecraftServer;
import net.roxymc.slime.ignite.SlimeIgnite;
import net.roxymc.slime.ignite.loader.SlimeWorldLoader;
import net.roxymc.slime.ignite.storage.SlimeWorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(
            method = "loadWorld0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getScoreboard()Lnet/minecraft/server/ServerScoreboard;"
            )
    )
    private void loadSlimeWorlds(String s, CallbackInfo ci) {
        SlimeWorldStorage fileStorage = SlimeWorldLoader.get().storage("file");

        fileStorage.listWorlds().forEach(world -> {
            try {
                SlimeIgnite.LOGGER.info("Loading slime world '{}'", world);

                long start = System.currentTimeMillis();
                fileStorage.readWorld(world).load(fileStorage);

                SlimeIgnite.LOGGER.info("Successfully loaded slime world '{}' in {}ms", world, System.currentTimeMillis() - start);
            } catch (Exception e) {
                SlimeIgnite.LOGGER.atError()
                        .setCause(e)
                        .log("Failed to load slime world '{}'", world);
            }
        });
    }
}
