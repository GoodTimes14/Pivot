package eu.magicmine.pivot.api.data.manager;

import com.mongodb.MongoInterruptedException;
import eu.magicmine.pivot.api.data.DataObject;
import eu.magicmine.pivot.api.utils.mongo.SavePair;
import org.bson.conversions.Bson;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SingleQueueDataManager<T extends DataObject> {

    private final Thread saveThread;
    private final BlockingQueue<SavePair<T>> saveQueue;

    public SingleQueueDataManager() {
        saveQueue = new LinkedBlockingQueue<>();
        saveThread = new Thread(this::checkQueue);
        saveThread.start();
    }


    public void checkQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SavePair<T> savePair = saveQueue.take();
                processSave(savePair);
            } catch (InterruptedException | MongoInterruptedException e) {
                break;
            }
        }
    }

    public void processSave(SavePair<T> savePair) {
        if(savePair.isUpdate()) {
            saveObject(savePair.getSaveObject());
        } else {
            updateObject(savePair.getIdentfier(), savePair.getUpdate());
        }
    }


    public void save(T data) {
        saveQueue.add(new SavePair<>(null,data,null));
    }

    public void update(Object identifier, Bson update) {
        saveQueue.add(new SavePair<>(identifier,null,update));
    }



    public void disable() {
        saveThread.interrupt();
        while (!saveQueue.isEmpty()) {
            processSave(saveQueue.poll());
        }
        saveAll();
    }


    public abstract void saveAll();

    public abstract void saveObject(T object);

    public abstract void updateObject(Object identifier,Bson update);


}