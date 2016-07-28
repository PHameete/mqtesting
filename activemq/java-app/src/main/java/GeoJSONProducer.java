import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Locale;

/**
 * Created by hameetepa on 26-7-2016.
 */
public class GeoJSONProducer implements Runnable {

    public static void main(String[] args) {
        Thread brokerThread = new Thread(new GeoJSONProducer());
        brokerThread.setDaemon(false);
        brokerThread.start();
    }

    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");
            connectionFactory.setUseAsyncSend(true);
            connectionFactory.setUseCompression(true);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination messages = session.createTopic("test-geojson");

            // Create MessageProducers from the Session to the Topic or Queue
            MessageProducer messageProducer = session.createProducer(messages);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

            String geoJson = Resources.toString(Resources.getResource("test.geojson"), Charsets.UTF_8);

            for (int i = 0; i < 1000; i++) {
                // send a large number of large geojson messages
                messageProducer.send(session.createTextMessage(geoJson));
                if (i % 10 == 0) {
                    System.out.println("Sent " + (i + 1) + " large GeoJSON records");
                }
            }

            messageProducer.close();
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}
