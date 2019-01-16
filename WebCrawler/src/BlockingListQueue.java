import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

/**
 * Created by hagai on 06/10/2018.
 */
public class BlockingListQueue implements URLQueue{

    private final Semaphore available = new Semaphore(0, true);
    private final LinkedList<String> queue;
    private final ReentrantLock lock = new ReentrantLock();

    public BlockingListQueue() {
        queue = new LinkedList<String>();
    }

    public boolean isEmpty() {
        return queue.size() == 0;
    }

    public boolean isFull() {
        return false;
    }

    public void enqueue(String url) {

        lock.lock();
        try {
            queue.add(url);
        }
        finally {
            lock.unlock();
        }
        available.release();
    }

    public String dequeue() {
        String res = "";
        try{
            available.acquire();

            lock.lock();
            try {
                res = queue.remove();
            } finally {
                lock.unlock();
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return res;

    }


}
