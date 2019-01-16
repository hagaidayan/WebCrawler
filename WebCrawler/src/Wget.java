import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

//ive been working next to Ben the exchange student
public class Wget {

    private static final ReentrantLock lock = new ReentrantLock();
    private static final int THEAD_NUM = 10;
    private static String proxy_host;
    private static int proxy_port;
    private static final BlockingListQueue queue = new BlockingListQueue();
    private static final HashSet<String> seen = new HashSet<String>();
    private static Thread myThread[];
    private static boolean isFinish = false;

  public static void doIterative(String requestedURL, String proxyHost,
      int proxyPort) {
    final URLQueue queue = new ListQueue();
    final HashSet<String> seen = new HashSet<String>();
    URLprocessing.handler = new URLprocessing.URLhandler() {
      // this method is called for each matched url
      public void takeUrl(String url) {
        if(!seen.contains(url)){
            seen.add(url);
            queue.enqueue(url);
        }
      }
    };
    // to start, we push the initial url into the queue
    URLprocessing.handler.takeUrl(requestedURL);
    while (!queue.isEmpty()) {
      String url = queue.dequeue();
      Xurl.query(url, proxyHost, proxyPort); // or equivalent yours
    }

  }

    public static void doMultiThreaded(String requestedURL, String proxyHost,
                                   int proxyPort) { // Streight forward solution, without thread pool

        final SynchronizedListQueue queue = new SynchronizedListQueue();
        final HashSet<String> seen = new HashSet<String>();
        URLprocessing.handler = new URLprocessing.URLhandler() {
            public void takeUrl(String url) {

                lock.lock();
                try {
                    if (!seen.contains(url)) {
                        seen.add(url);
                        queue.enqueue(url);
                    }
                }
                finally {
                    lock.unlock();
                }

            }
        };
        // to start, we push the initial url into the queue
        URLprocessing.handler.takeUrl(requestedURL);
        Thread t;
        while (!queue.isEmpty()) {
            String url = queue.dequeue();
            callXURL startThread = new callXURL(url, proxyHost, proxyPort);
            t = new Thread(startThread);
            t.start();
            try{
            t.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        }
    }


    public static void doThreadedPool(String requestedURL, String proxyHost,
                                       int proxyPort) {
        int numThreadSleep = 0;
        Thread myThreads[] = new Thread[THEAD_NUM];

        URLprocessing.handler = new URLprocessing.URLhandler() {
            // this method is called for each matched url
            public void takeUrl(String url) {
                if(!seen.contains(url)){
                    seen.add(url);
                    queue.enqueue(url);
                }
            }
        };
        doQueueLoop startThread = new doQueueLoop(proxyHost, proxyPort); // starting threads
        queue.enqueue(requestedURL);
        seen.add(requestedURL);
        for (int i=0; i < THEAD_NUM; i++){
            myThreads[i] = new Thread(startThread);
            myThreads[i].setName("t" + i);
            myThreads[i].start();
        }
        while(numThreadSleep != THEAD_NUM){ // loop until all threads sleep
            numThreadSleep = 0;
            for (int i=0; i < THEAD_NUM; i++){
                if(myThreads[i].getState() == Thread.State.WAITING){
                    numThreadSleep += 1;
                }
            }
        }
        for (int i=0; i < THEAD_NUM; i++){ // interrupt all threads to let them finish
            myThreads[i].interrupt();
        }
            try{
                for (int i=0; i < THEAD_NUM; i++){
                    myThreads[i].join();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
    }

    private static void queueLoop(String proxyHost,
                                  int proxyPort) throws InterruptedException{
        try {
            while (true) {
                String url = queue.dequeue();
                if(Thread.currentThread().isInterrupted())
                {
                    throw new InterruptedException(); //Interrupted by the main thread
                }
                Xurl.query(url, proxyHost, proxyPort);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public static class callXURL implements Runnable {

        private String requestedURL;
        private String proxyHost;
        private int proxyPort;
        public callXURL(String requestedURL, String proxyHost,
                        int proxyPort) {
            this.requestedURL = requestedURL;
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
        }
        public void run(){Xurl.query(requestedURL, proxyHost, proxyPort);}
    }

    public static class doQueueLoop implements Runnable {

        private String proxyHost;
        private int proxyPort;

        public doQueueLoop( String proxyHost,
                        int proxyPort) {
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
        }
        public void run(){
            try{
                queueLoop(this.proxyHost, this.proxyPort);
            }
            catch (InterruptedException e)
            {
                //Main thread woke you, program should terminate
            }
        }
    }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: java Wget url [proxyHost proxyPort]");
      System.exit(-1);
    }
    String proxyHost = null;
    if (args.length > 1)
      proxyHost = args[1];
    int proxyPort = -1;
    if (args.length > 2)
      proxyPort = Integer.parseInt(args[2]);
      doThreadedPool(args[0], proxyHost, proxyPort);
  }

}
