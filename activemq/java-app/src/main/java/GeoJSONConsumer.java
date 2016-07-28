import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.HdrHistogram.Histogram;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by hameetepa on 26-7-2016.
 */
public class GeoJSONConsumer implements Runnable, ExceptionListener {

    ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        Thread brokerThread = new Thread(new GeoJSONConsumer());
        brokerThread.setDaemon(false);
        brokerThread.start();
    }

    public void run() {
        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");
            connectionFactory.setUseCompression(true);
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination messages = session.createTopic("test-geojson");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer messageConsumer = session.createConsumer(messages);

            boolean running = true;
            while (running) {
                Message message = messageConsumer.receive(100);
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        JsonNode msg = mapper.readTree(textMessage.getText());
                        System.out.println("Received and parsed GeoJSON message of size: " + textMessage.getText().length());
                    } catch (Exception e) {
                        System.out.println("failed to parse: " + textMessage.getText() + ", skipping...");
                        continue;
                    }
                } else {
                    if(message == null) {
                        Thread.sleep(100);
                    }
                    else {
                        System.out.println("Received: " + message);
                    }
                }
            }

//            messageConsumer.close();
//            markerConsumer.close();
//            session.close();
//            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}
