import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GeoJSONProducer {
    public static void main(String[] args) throws IOException {
        // set up the producer
        KafkaProducer<String, String> producer;
        try (InputStream props = Resources.getResource("producer.props").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            producer = new KafkaProducer<>(properties);
        }

        String geoJson = Resources.toString(Resources.getResource("test.geojson"), Charsets.UTF_8);
        try {
            for (int i = 0; i < 1000; i++) {
                // send a large number of large geojson messages
                producer.send(new ProducerRecord<String, String>(
                        "test-geojson3",
                        geoJson));
                if (i % 10 == 0) {
                    System.out.println("Sent " + (i + 1) + " large GeoJSON records");
                }
            }
        } catch (Throwable throwable) {
            System.out.printf("%s", throwable.getStackTrace());
        } finally {
            producer.close();
        }

    }
}
