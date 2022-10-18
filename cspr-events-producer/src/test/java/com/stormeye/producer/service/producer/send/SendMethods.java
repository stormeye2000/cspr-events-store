package com.stormeye.producer.service.producer.send;

import static java.util.Map.entry;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.util.ResourceUtils;
import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;
import com.stormeye.producer.json.CsprEventSerializer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Map;

abstract class SendMethods {

    final static String TOPIC = "main";
    final static int MB256 = 268435456;
    final static int MB1 = 1048576;

    Map<String, Object> producerConfigs(final int bytes, final String port) {

        return Map.ofEntries(
                entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + port),
                entry("buffer.memory", bytes),
                entry("max.request.size", bytes),
                entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CsprEventSerializer.class)
        );
    }

    Event<String> buildEvent(final String event) throws Exception {

        final Class<?> eventBuildClass = Class.forName("com.casper.sdk.service.impl.event.EventBuilder");
        final Constructor<?> ctor = eventBuildClass.getDeclaredConstructor(EventType.class, EventTarget.class, String.class);
        ctor.setAccessible(true);
        final Object eventBuilder = ctor.newInstance(EventType.MAIN, EventTarget.RAW, event);

        final Method processLine = eventBuildClass.getDeclaredMethod("processLine", String.class);
        processLine.setAccessible(true);

        processLine.invoke(eventBuilder, event);

        final Method buildEvent = eventBuildClass.getDeclaredMethod("buildEvent");
        buildEvent.setAccessible(true);

        //noinspection unchecked
        return (Event<String>) buildEvent.invoke(eventBuilder);
    }

    String getEventFile(final String fileName) throws IOException {
        return new String(Files.readAllBytes(ResourceUtils.getFile("classpath:" + fileName).toPath()));
    }


}
