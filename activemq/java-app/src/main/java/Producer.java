import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Locale;

/**
 * Created by hameetepa on 26-7-2016.
 */
public class Producer implements Runnable {

    public static void main(String[] args) {
        Thread brokerThread = new Thread(new Producer());
        brokerThread.setDaemon(false);
        brokerThread.start();
    }

    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");
            connectionFactory.setUseCompression(true);
            connectionFactory.setUseAsyncSend(true);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination messages = session.createTopic("messages");
            Destination markers = session.createTopic("markers");

            // Create MessageProducers from the Session to the Topic or Queue
            MessageProducer messageProducer = session.createProducer(messages);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            MessageProducer markerProducer = session.createProducer(markers);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

            for (int i = 0; i < 1000000; i++) {
                // send lots of messages
                messageProducer.send(session.createTextMessage(
                        String.format(Locale.ROOT, "{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));

                // every so often send to a different topic
                if (i % 1000 == 0) {
                    messageProducer.send(session.createTextMessage(
                            String.format(Locale.ROOT, "{\"type\":\"marker\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
                    markerProducer.send(session.createTextMessage(
                            String.format(Locale.ROOT, "{\"type\":\"other\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
                    System.out.println("Sent msg number " + i);
                }
            }

            messageProducer.close();
            markerProducer.close();
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}
