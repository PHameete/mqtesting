/**
 * Created by hameetepa on 26-7-2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        thread(new Producer(), false);
        //thread(new Consumer(), false);
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
}
