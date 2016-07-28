import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class GeoJSONConsumer {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //set up the consumer
        KafkaConsumer<String, String> consumer;
        try (InputStream props = Resources.getResource("consumer-geo.props").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            if (properties.getProperty("group.id") == null) {
                properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
            }
            consumer = new KafkaConsumer<>(properties);
        }
        consumer.subscribe(Arrays.asList("test-geojson3"));

        // consume new messages in the topic until termination
        int timeouts = 0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (records.count() == 0) {
                timeouts++;
            } else {
                System.out.printf("Got %d records after %d timeouts\n", records.count(), timeouts);
                timeouts = 0;
            }
            for (ConsumerRecord<String, String> record : records) {
                try {
                    JsonNode msg = mapper.readTree(record.value());
                    System.out.println("Received and parsed GeoJSON message of size: " + record.value().length());
                } catch (Exception e) {
                    System.out.println("failed to parse: " + record.value() + ", skipping...");
                    continue;
                }
            }
        }
    }
}