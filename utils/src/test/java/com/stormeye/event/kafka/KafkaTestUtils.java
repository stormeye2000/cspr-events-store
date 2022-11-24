package com.stormeye.event.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

import static com.stormeye.event.utils.ThreadUtils.sleepNoSonarWarnings;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author ian@meywood.com
 */
public class KafkaTestUtils {

    private KafkaTestUtils() {
        // prevent construction
    }


    public static void waitSent(final Future<RecordMetadata> send) {
        var count = 0;
        while (!send.isDone()) {
            sleepNoSonarWarnings(1000L);
            if (++count > 5) {
                fail("Should have sent event");
            }
        }
        sleepNoSonarWarnings(1000L);
    }
}
