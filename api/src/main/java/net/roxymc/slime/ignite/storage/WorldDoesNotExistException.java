package net.roxymc.slime.ignite.storage;

public class WorldDoesNotExistException extends RuntimeException {
    public WorldDoesNotExistException(String message) {
        super(message);
    }
}
