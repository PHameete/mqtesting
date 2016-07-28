import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class PrintCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        e.printStackTrace();
        //System.out.println("COMPLETED SEND: " + recordMetadata.offset() + " " + e.getMessage());
    }
}