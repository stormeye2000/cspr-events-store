package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.Event;
import com.stormeye.producer.config.ServiceProperties;
import com.stormeye.producer.service.emitter.EmitterService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;

import static com.stormeye.event.utils.ThreadUtils.sleepNoSonarWarnings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
@EmbeddedKafka(topics = {"main", "deploys", "sigs"}, partitions = 1, ports = {9099})
class ProducerServiceTest {

    public MockWebServer mockWebServer;
    @Autowired
    private ProducerService producerService;
    @Autowired
    private EmitterService emitterService;
    @Autowired
    private IdStorageService idStorageService;
    @Autowired
    private KafkaProducer<Integer, Event<String>> kafkaProducer;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @BeforeEach
    void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.close();
        kafkaProducer.close();
        kafkaBroker.destroy();
    }

    @Test
    void testSendEvents() {

        // Assert produce and kafka are created
        assertThat(producerService, is(notNullValue()));
        assertThat(kafkaProducer, is(notNullValue()));

        // Mock a casper node
        mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {

                return new MockResponse()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setBody(getEventResourceStream(recordedRequest.getPath()))
                        .setResponseCode(200);
            }

            private String getEventResourceStream(final String path) {
                try (InputStream resourceAsStream = ProducerServiceTest.class.getResourceAsStream(getEventResource(path))) {
                    return new String(Objects.requireNonNull(resourceAsStream).readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @NotNull
            private String getEventResource(final String path) {
                if (path.contains("main")) {
                    return "/main.events.stream";
                } else if (path.contains("sigs")) {
                    return "/sigs.events.stream";
                } else if (path.contains("deploys")) {
                    return "/deploys.events.stream";
                } else {
                    throw new IllegalArgumentException("Invalid Path:" + path);
                }
            }
        });

        //  final ExecutorService executor = Executors.newFixedThreadPool(1);
        final URI emitter = URI.create(String.format("http://localhost:%s", mockWebServer.getPort()));

        final ServiceProperties serviceProperties = Mockito.mock(ServiceProperties.class);
        Mockito.when(serviceProperties.getEmitters()).thenReturn(List.of(emitter));

        assertThat(serviceProperties.getEmitters(), hasItem(emitter));

        final int[] count = {0};
        final int[] main = {0};
        final int[] deploys = {0};
        final int[] sigs = {0};
        final int[] ids = {0};

        final ProducerService localProducerService = new ProducerService(
                serviceProperties,
                emitterService,
                idStorageService,
                kafkaProducer
        ) {
            @Override
            void sendEvent(URI emitter, Event<String> event) {

                super.sendEvent(emitter, event);

                switch (event.getEventType()) {
                    case MAIN -> main[0]++;
                    case DEPLOYS -> deploys[0]++;
                    case SIGS -> sigs[0]++;
                }
                count[0]++;

                // Assert IDs are stored for each processed event with an ID
                if (event.getId().isPresent()) {
                    assertThat(idStorageService.getCurrentId(emitter, event.getEventType()), is(event.getId().get()));
                    assertThat(idStorageService.getNextId(emitter, event.getEventType()), is(event.getId().get() + 1L));
                    ids[0]++;
                }
            }
        };

        assertThat(localProducerService, is(notNullValue()));

        localProducerService.startEventConsumers();

        sleepNoSonarWarnings(20000L);

        assertThat(count[0], is(greaterThan(0)));
        assertThat(main[0], is(greaterThan(0)));
        assertThat(deploys[0], is(greaterThan(0)));
        assertThat(sigs[0], is(greaterThan(0)));
        assertThat(ids[0], is(greaterThan(0)));
    }
}
