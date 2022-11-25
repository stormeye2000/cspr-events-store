package com.stormeye.kafka;

import com.stormeye.event.utils.ThreadUtils;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

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
            ThreadUtils.sleepNoSonarWarnings(1000L);
            if (++count > 5) {
                fail("Should have sent event");
            }
        }
        ThreadUtils.sleepNoSonarWarnings(1000L);
    }
}
