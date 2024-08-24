package eu.magicmine.pivot.api.database.lock;

public interface ILockManager<T> {

    Object lockObject(T id);

    boolean isLocked(T id);

    void unlockObject(T id);

}
