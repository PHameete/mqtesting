import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.HdrHistogram.Histogram;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by hameetepa on 26-7-2016.
 */
public class Consumer implements Runnable, ExceptionListener {

    ObjectMapper mapper = new ObjectMapper();
    Histogram stats = new Histogram(1, 10000000, 2);
    Histogram global = new Histogram(1, 10000000, 2);

    public static void main(String[] args) {
        Thread brokerThread = new Thread(new Consumer());
        brokerThread.setDaemon(false);
        brokerThread.start();
    }

    public void run() {
        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connectionFactory.setUseCompression(true);
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination messages = session.createTopic("messages");
            Destination markers = session.createTopic("markers");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer messageConsumer = session.createConsumer(messages);
            MessageConsumer markerConsumer = session.createConsumer(markers);

            boolean running = true;
            while (running) {
                Message message = messageConsumer.receive(100);
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    JsonNode msg = mapper.readTree(textMessage.getText());
                    switch (msg.get("type").asText()) {
                        case "test":
                            long latency = (long) ((System.nanoTime() * 1e-9 - msg.get("t").asDouble()) * 1000);
                            stats.recordValue(latency);
                            global.recordValue(latency);
                            break;
                        case "marker":
                            // whenever we get a marker message, we should dump out the stats
                            // note that the number of fast messages won't necessarily be quite constant
                            System.out.printf("%d messages received in period, latency(min, max, avg, 99%%) = %d, %d, %.1f, %d (ms)\n",
                                    stats.getTotalCount(),
                                    stats.getValueAtPercentile(0), stats.getValueAtPercentile(100),
                                    stats.getMean(), stats.getValueAtPercentile(99));
                            System.out.printf("%d messages received overall, latency(min, max, avg, 99%%) = %d, %d, %.1f, %d (ms)\n",
                                    global.getTotalCount(),
                                    global.getValueAtPercentile(0), global.getValueAtPercentile(100),
                                    global.getMean(), global.getValueAtPercentile(99));

                            stats.reset();
                            break;
                        default:
                            throw new IllegalArgumentException("Illegal message type: " + msg.get("type"));
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
