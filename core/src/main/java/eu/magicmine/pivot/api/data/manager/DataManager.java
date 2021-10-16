package eu.magicmine.pivot.api.data.manager;

import eu.magicmine.pivot.api.data.DataObject;
import eu.magicmine.pivot.api.utils.mongo.DataUpdate;
import lombok.SneakyThrows;
import org.bson.conversions.Bson;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class DataManager<T extends DataObject> {

    private final Thread saveThread,updateThread;
    private final BlockingQueue<T> saveQueue;
    private final BlockingQueue<DataUpdate> updateQueue;

    public DataManager() {
        saveQueue = new LinkedBlockingQueue<>();
        updateQueue = new LinkedBlockingQueue<>();
        saveThread = new Thread(this::checkSaveQueue);
        updateThread = new Thread(this::checkUpdateQueue);
        saveThread.start();
        updateThread.start();
    }


    @SneakyThrows
    public void checkSaveQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            T object = saveQueue.take();
            saveObject(object);
        }
    }

    @SneakyThrows
    public void checkUpdateQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            DataUpdate object = updateQueue.take();
            updateObject(object);
        }
    }

    public void save(T data) {
        saveQueue.add(data);
    }

    public void update(Object identifier, Bson update) {
        updateQueue.add(new DataUpdate(identifier,update));
    }

    public void load() {}

    public void disable() {
        saveThread.interrupt();
        updateThread.interrupt();
        saveAll();
    }

    public abstract void saveAll();

    public abstract void saveObject(T object);


    public abstract void updateObject(DataUpdate object);


}
