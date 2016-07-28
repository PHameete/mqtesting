import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hameetepa on 27-7-2016.
 */
public class StreamProcessor {

    public static void main(String[] args) {
        startStream();
    }

    public static void startStream() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream-processing-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "streams-pipe");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        StreamsConfig config = new StreamsConfig(props);

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> stream = builder.stream("test-messages");
        stream.mapValues(p -> p.length() + "").to("test-stream-output");
        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();
    }
}
