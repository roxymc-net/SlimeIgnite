package net.roxymc.slime.ignite.storage;

public class WorldFailedToReadException extends RuntimeException {
    public WorldFailedToReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
