package net.roxymc.slime.ignite.storage;

public class WorldAlreadyLoadedException extends RuntimeException {
    public WorldAlreadyLoadedException(String message) {
        super(message);
    }
}
