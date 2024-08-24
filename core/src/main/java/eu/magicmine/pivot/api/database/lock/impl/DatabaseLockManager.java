package eu.magicmine.pivot.api.database.lock.impl;

import eu.magicmine.pivot.api.database.lock.ILockManager;

import java.util.HashMap;
import java.util.Map;

public class DatabaseLockManager<T> implements ILockManager<T> {


    private final Map<T,Object> locks = new HashMap<>();

    @Override
    public Object lockObject(T id) {
        Object lock = new Object();
        locks.put(id, lock);
        return lock;
    }

    @Override
    public void unlockObject(T id) {
        Object lock = locks.remove(id);
        lock.notifyAll();
    }

    @Override
    public boolean isLocked(T id) {
        return locks.containsKey(id);
    }
}
