package net.roxymc.slime.ignite.storage;

public class WorldFailedToSaveException extends RuntimeException {
    public WorldFailedToSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
